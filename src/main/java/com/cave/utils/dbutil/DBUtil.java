package com.cave.utils.dbutil;

import com.cave.utils.dbutil.mapper.Mappers;
import com.cave.utils.dbutil.mapper.ResultSetMap;
import com.cave.utils.dbutil.mapper.SimpleResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

import static com.cave.utils.dbutil.Checks.checkNull;

/**
 *
 */
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

    private static final class RsResultSetMapper extends SimpleResultSetMapper<ResultSet>{
        @Override
        public ResultSet mapObject(ResultSet rs) throws SQLException {
            return rs;
        }
    }

	/**
	 *
	 * @param ds
	 */
	public DBUtil(DataSource ds){
		checkNull(ds, "ds");
		
		this.dataSource = ds;
	}

	/**
	 *
	 * @param conn
	 */
	public DBUtil(Connection conn){
		
		checkNull(conn, "conn");
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

	private DataAccessException translateException(Throwable t) {
		return new DataAccessException(t);
	}

    private void close(Object ... objs){
        for (Object o : objs){
            if (o == null){
                continue;
            }
            if (o instanceof Connection){
                Connection c = (Connection)o;
                boolean isClosed = false;
                boolean isConProvided = this.conn != null;

                try {
                    isClosed = c.isClosed();
                } catch (SQLException e1) {
                    e1.printStackTrace(System.err);
                }
                try {
                    // só fazer commit se a inicialização tiver sido via connectionType
                    if (!isConProvided && !isClosed){
                        c.commit();
                    }
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }finally{
                    try {
                        // só entrar aqui se a inicialização tiver sido via connectionType
                        // e nao partilhando a Connection
                        if (!isConProvided && !isClosed){
                            c.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace(System.err);
                    }
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

    private void rollback(Connection conn){
        try {
            if (!conn.getAutoCommit()){
                try {
                    conn.rollback();
                } catch (SQLException er) {
                    er.printStackTrace(System.err);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }

	private Statement createStatement(Connection conn, boolean generateKeys, String query, Object ... params) throws SQLException{

        Checks.checkNullOrEmpty(query, "query");
        Checks.checkNull(conn, "conn");

        PreparedStatement pstmt = null;

        if (params == null || params.length == 0){
            if (!generateKeys){
                return conn.createStatement();
            }
        }

        if (generateKeys){
            pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        }else{
            pstmt = conn.prepareStatement(query);
        }

        ParameterMetaData paramMetadata = null;
        int paramCount = 0;
        try{
            paramMetadata = pstmt.getParameterMetaData();
            paramCount = paramMetadata.getParameterCount();
        }catch(SQLException sqle){
            //driver sqlServer nao suporta getParameterMetaData
            String sqlServerMsg = "The PreparedStatement.getParameterMetaData method is not implemented.";
            String msg = "" + sqle.getMessage();

            if (!msg.equals(sqlServerMsg)){
                sqle.printStackTrace(System.err);
            }
        }

        if (paramMetadata == null && params != null){
            paramCount = params.length;
        }

        if (paramCount > 0 && (params != null && params.length > 0)){
            for (int i = 1; i <= paramCount; i++){
                Object paramValue = params[i - 1];
                pstmt.setObject(i, paramValue);
            }
        }

        return pstmt;
	}

    public void iterate(String query, ResultSetIterator<ResultSet> rsIterator) throws DataAccessException{
        iterate(query, new RsResultSetMapper(), rsIterator, (Object[])null);
    }

    public void iterate(String query, ResultSetIterator<ResultSet> rsIterator, Object ... params) throws DataAccessException{
        iterate(query, new RsResultSetMapper(), rsIterator, params);
    }

    public <T> void iterate(String query, ResultSetMapper<T> rsMapper, ResultSetIterator<T> rsIterator) throws DataAccessException{
        iterate(query, rsMapper, rsIterator, (Object[])null);
    }

    public <T> void iterate(String query, ResultSetMapper<T> rsMapper, ResultSetIterator<T> rsIterator, Object ... params) throws DataAccessException{
        Checks.checkNullOrEmpty(query, "query");
        Connection conn = createConnection();

        Checks.checkNull(conn, "conn");
        Checks.checkNull(rsMapper, "rsMapper");
        Checks.checkNull(rsIterator, "rsIterator");

        Statement stmt = null;
        ResultSet rs = null;

        try {
            long startTime = System.currentTimeMillis();
            stmt = createStatement(conn, false, query, params);

            if (stmt instanceof PreparedStatement){
                rs = ((PreparedStatement)stmt).executeQuery();
            }else{
                rs = stmt.executeQuery(query);
            }
            logQuery(query, startTime, params);

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
            logErrorInQuery(e, query, params);
            e.printStackTrace(System.err);
            throw translateException(e);
        }finally{
            close(rs, stmt, conn);
            rsMapper.terminate();
            rsIterator.terminate();
        }
    }

    public <T> List<T> selectAll(String query, ResultSetMapper<T> rsMapper) throws DataAccessException{
        return selectAll(query, rsMapper, (Object[])null);
    }

    public <T> List<T> selectAll(String query, ResultSetMapper<T> rsMapper, Object ... params) throws DataAccessException{
        ListSimpleResultSetIterator<T> listIterator = new ListSimpleResultSetIterator<T>(true);

        iterate(query, rsMapper, listIterator, params);

        return listIterator.getList();
    }

    public <T> T selectOne(String query, ResultSetMapper<T> rsMapper) throws DataAccessException{
        return selectOne(query, rsMapper, (Object[])null);
    }

    public <T> T selectOne(String query, ResultSetMapper<T> rsMapper, Object ... params) throws DataAccessException{

        ListSimpleResultSetIterator<T> listIterator = new ListSimpleResultSetIterator<T>(false);

        iterate(query, rsMapper, listIterator, params);

        final List<T> list = listIterator.getList();

        T ret = null;

        if (!list.isEmpty()){
            ret = list.get(0);
        }

        return ret;
    }

    /**
     * Checks if a query returns results
     *
     * @param query
     * @return true if the query returns results, false otherwise
     * @author 92429
     */
    public boolean hasResults(String query) throws DataAccessException{
        return hasResults(query, (Object[])null);
    }

    /**
     * Checks if a query returns results
     *
     * @param query
     * @param params used with preparedStatement query sintax (" WHERE XPTO = ?")
     * @return true if the query returns results, false otherwise
     */
    public boolean hasResults(String query, Object ... params) throws DataAccessException{

        Object result = selectOne(query, DBUtil.dummyMapper, params);

        return result != null;
    }

    /**
     * Provides a way to reuse this connection.
     *
     * @param action action to execute with the connection.
     * The connection will be closed when the action returns,
     * unless DBUtil was created with a connection in the first place.
     *
     * @return the value that JDBCAction returned
     */
    public <T> T useConnection(JDBCAction<T> action) throws DataAccessException{
        Checks.checkNull(action, "action");
        Connection conn = createConnection();
        Checks.checkNull(conn, "conn");

        try{
            return action.execute(new UncloseableConnectionImpl(conn));
        }catch(Throwable t){
            rollback(conn);
            t.printStackTrace(System.err);
            throw translateException(t);
        }finally{
            close(conn);
        }
    }

    private int executeUpdate(String sqlUpdate, Object ... params) throws DataAccessException{
        Connection conn = createConnection();
        Checks.checkNull(conn, "conn");
        Statement stmt = null;

        try {
            int ret = -1;
            long startTime = System.currentTimeMillis();
            stmt = createStatement(conn, false, sqlUpdate, params);

            if (stmt instanceof PreparedStatement){
                ret = ((PreparedStatement)stmt).executeUpdate();
            }else{
                ret = stmt.executeUpdate(sqlUpdate);
            }
            logQuery(sqlUpdate, startTime, params);

            return ret;

        } catch (SQLException e) {
            logErrorInQuery(e, sqlUpdate, params);
            e.printStackTrace(System.err);
            throw translateException(e);
        }finally{
            close(stmt, conn);
        }
    }

    public int insert(String sqlInsert) throws DataAccessException{
        return executeUpdate(sqlInsert, (Object[])null);
    }

    /**
     *
     * @param sqlInsert
     * @param params variable list of params
     * @return the count of rows for INSERT, UPDATE or DELETE statements, 0 for statements that return nothing, -1 in case of error
     */
    public int insert(String sqlInsert, Object ... params) throws DataAccessException{
        return executeUpdate(sqlInsert, params);
    }

    /**
     *
     * @param sqlUpdate
     * @return the count of rows for INSERT, UPDATE or DELETE statements, 0 for statements that return nothing, -1 in case of error
     */
    public int update(String sqlUpdate) throws DataAccessException{
        return executeUpdate(sqlUpdate, (Object[])null);
    }

    /**
     *
     * @param sqlUpdate
     * @param params variable list of params
     * @return the count of rows for INSERT, UPDATE or DELETE statements, 0 for statements that return nothing, -1 in case of error
     */
    public int update(String sqlUpdate, Object ... params) throws DataAccessException{
        return executeUpdate(sqlUpdate, params);
    }

    /**
     *
     * @param sqlDelete
     * @return the count of rows for INSERT, UPDATE or DELETE statements, 0 for statements that return nothing, -1 in case of error
     */
    public int delete(String sqlDelete) throws DataAccessException{
        return executeUpdate(sqlDelete, (Object[])null);
    }

    /**
     *
     * @param sqlDelete
     * @param params variable list of params
     * @return the count of rows for INSERT, UPDATE or DELETE statements, 0 for statements that return nothing, -1 in case of error
     */
    public int delete(String sqlDelete, Object ... params) throws DataAccessException{
        return executeUpdate(sqlDelete, params);
    }

    public ResultSetMap getKeysForInsert(String sqlInsert) throws DataAccessException{
        return getKeysForInsert(sqlInsert, (Object[])null);
    }

    public ResultSetMap getKeysForInsert(String sqlInsert, Object ... params) throws DataAccessException{
        Connection conn = createConnection();
        Statement stmt = null;

        ResultSetMap ret = new ResultSetMap();
        ResultSet generatedKeys = null;
        try {
            long startTime = System.currentTimeMillis();
            stmt = createStatement(conn, true, sqlInsert, params);

            if (stmt instanceof PreparedStatement){
                ((PreparedStatement)stmt).executeUpdate();
            }else{
                stmt.executeUpdate(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            }
            logQuery(sqlInsert, startTime, params);

            generatedKeys = stmt.getGeneratedKeys();

            ResultSetMapper<ResultSetMap> mapper = Mappers.mapMapper();
            mapper.init(generatedKeys);

            while(generatedKeys.next()){
                ret.putAll(mapper.mapObject(generatedKeys));
            }

            mapper.terminate();

        } catch (SQLException e) {
            logErrorInQuery(e, sqlInsert, params);
            e.printStackTrace(System.err);
            throw translateException(e);
        }finally{
            close(generatedKeys, stmt, conn);
        }

        return ret;
    }

    private String normalizeSQL(String query){
        return query.replaceAll("\\r", "").replaceAll("\\n", "");
    }

    private String genSQLLog(String query, Object ... params){
        StringBuilder sb = new StringBuilder();

        sb.append("SQL = ").append(normalizeSQL(query));

        if (params != null && params.length > 0){
            sb.append(". PARAMS = [");

            for (int i = 0, l = params.length; i < l; i++){
                sb.append(params[i]);

                if (i + 1 < l){
                    sb.append(", ");
                }
            }
            sb.append("]");
        }

        return sb.toString();
    }

    private void logQuery(String query, long startTime, Object ... params){
        long diff = System.currentTimeMillis() - startTime;
        String msg = genSQLLog(query, params) + " (t=" + diff + "ms)";
        logger.debug(msg);
    }

    private void logErrorInQuery(Throwable t, String query, Object ... params){
        StringBuilder sb = new StringBuilder();

        sb.append("Exception Thrown: ").append("msg = ").append(t.getMessage())
                .append(". ").append(genSQLLog(query, params));

        logger.error(sb.toString());
    }
}
