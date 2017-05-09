package com.cave.utils.dbutil;

import com.cave.utils.dbutil.mapper.SimpleResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class DBUtil {

	private static final String CLASSNAME = DBUtil.class.getSimpleName();
	private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);
	private static final DummyResultSetMapper dummyMapper = new DummyResultSetMapper();
	
	private DataSource dataSource = null;
	private Connection conn = null;

	private static final class DummyResultSetMapper extends SimpleResultSetMapper<Object> {

		private static final Object o = new Object();
		
		@Override
		public Object mapObject(ResultSet rs) throws SQLException {
			return o;
		}		
	}
	
	public DBUtil(DataSource ds){
		
		if (ds == null){
			throw new NullPointerException("ds");
		}
		
		this.dataSource = ds;
	}
	
	public DBUtil(Connection conn){
		
		if (conn == null){
			throw new NullPointerException("conn");
		}
		
		this.conn = conn;
	}


	private Connection createConnection(){

		if (this.conn != null){
			return this.conn;			
		}

		try {

			return this.dataSource.getConnection();
			
		} catch (Exception e) {		
			logger.error("Erro ao obter conexção. msg=" + e.getMessage());
			e.printStackTrace(System.err);
		}
		
		return null;
	}
	
	private void close(Object ... objs){
	
		for (Object o : objs){
			
			if (o == null){
				continue;
			}
			
			if (o instanceof Connection){
				
				Connection c = (Connection)o;
				try {
					if (this.conn == null && !c.isClosed()){
						c.commit();
						c.close();
					}									
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}			
				
			}else if(o instanceof Statement){
				
				Statement s = (Statement)o;
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}			
				
			}else if (o instanceof ResultSet){
				
				ResultSet rs = (ResultSet) o;
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}			
			}
		}
	}
	
	private PreparedStatement createPreparedStatement(Connection conn, String query, Object ... params) throws SQLException{
		
		PreparedStatement pstmt = conn.prepareStatement(query);		
		ParameterMetaData paramMetadata = pstmt.getParameterMetaData();
		
		int paramCount = paramMetadata.getParameterCount();
		
		if (paramCount > 0 && (params != null && params.length > 0)){
			
			for (int i = 1; i <= paramCount; i++){					
				
				Object paramValue = params[i - 1];				
				pstmt.setObject(i, paramValue);
			}										
		}
		
		return pstmt;
	}	
	
	public <T> void iterate(String query, ResultSetMapper<T> rsMapper, ResultSetIterator<T> rsIterator){
		iterate(query, rsMapper, rsIterator, (Object[])null);
	}
	
	public <T> void iterate(String query, ResultSetMapper<T> rsMapper, ResultSetIterator<T> rsIterator, Object ... params){
		Connection conn = createConnection();
		
		if (conn == null){
			throw new NullPointerException("conn");
		}
		
		if (rsMapper == null){
			throw new NullPointerException("rsMapper");
		}
		
		if (rsIterator == null){
			throw new NullPointerException("rsIterator");
		}
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = createPreparedStatement(conn, query, params);				
			rs = pstmt.executeQuery();
			
			rsMapper.init(rs);
			rsIterator.init();
			
			while(rs.next()){
				
				T mappedObject = rsMapper.mapObject(rs);
				
				if (mappedObject != null){
					boolean continueIteration = rsIterator.iterate(mappedObject);
					
					if (!continueIteration){
						break;
					}
				}
			}						
			
		} catch (SQLException e) {
			e.printStackTrace(System.err);
		}finally{
			close(rs, pstmt, conn);	
			rsMapper.terminate();
			rsIterator.terminate();
		}
	}
	
	public <T> List<T> selectAll(String query, ResultSetMapper<T> rsMapper){
		return selectAll(query, rsMapper, (Object[])null);
	}
	
	public <T> List<T> selectAll(String query, ResultSetMapper<T> rsMapper, Object ... params){
				
		ListSimpleResultSetIterator<T> listIterator = new ListSimpleResultSetIterator<>(true);
		
		iterate(query, rsMapper, listIterator, params);
				
		return listIterator.getList();
	}
	
	public <T> T selectOne(String query, ResultSetMapper<T> rsMapper){
		return selectOne(query, rsMapper, (Object[])null);
	}
	
	public <T> T selectOne(String query, ResultSetMapper<T> rsMapper, Object ... params){
		
		ListSimpleResultSetIterator<T> listIterator = new ListSimpleResultSetIterator<>(false);
					
		iterate(query, rsMapper, listIterator, params);
		
		final List<T> list = listIterator.getList();
		
		T ret = null;
		
		if (!list.isEmpty()){
			ret = list.get(0);
		}
		
		return ret;
	}
	
	public boolean hasResults(String query){
		return hasResults(query, (Object[])null);
	}
	
	public boolean hasResults(String query, Object ... params){
		
		Object result = selectOne(query, DBUtil.dummyMapper, params);
		
		return result != null;
	}
	
	public <T> T useConnection(JDBCAction<T> action){
		Connection conn = createConnection();
		
		if (conn == null){
			throw new NullPointerException("conn");
		}
		
		try{			
			return action.execute(new UncloseableConnectionImpl(conn));			
		}catch(Throwable t){
			t.printStackTrace(System.err);
		}finally{
			close(conn);
		}
		
		return null;
	}
	
	private int executeUpdate(String sqlUpdate, Object ... params){
		
		Connection conn = createConnection();
		
		if (conn == null){
			throw new NullPointerException("conn");
		}
		
		PreparedStatement pstmt = null;
		
		try {
			pstmt = createPreparedStatement(conn, sqlUpdate, params);				
			return pstmt.executeUpdate();						
			
		} catch (SQLException e) {
			e.printStackTrace(System.err);
		}finally{
			close(pstmt, conn);	
		}
		
		return -1;
	}
	
	public int insert(String sqlInsert){
		return executeUpdate(sqlInsert, (Object[])null);
	}
	
	public int insert(String sqlInsert, Object ... params){
		return executeUpdate(sqlInsert, params);
	}
	
	public int update(String sqlUpdate){
		return executeUpdate(sqlUpdate, (Object[])null);
	}
	
	public int update(String sqlUpdate, Object ... params){
		return executeUpdate(sqlUpdate, params);
	}
	
	public int delete(String sqlDelete){
		return executeUpdate(sqlDelete, (Object[])null);
	}
	
	public int delete(String sqlDelete, Object ... params){
		return executeUpdate(sqlDelete, params);
	}
}
