package com.cave.utils.dbutil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * 
 * NOT Thread-safe
 */
public class MapResultSetMapper implements ResultSetMapper<ResultSetMap>{

	private String[] columnLabels = null;
	
	@Override
	public ResultSetMap mapObject(ResultSet rs) throws SQLException {
		
		ResultSetMap ret = new ResultSetMap();

		for (String colName : this.columnLabels) {

			Object value = rs.getObject(colName);
			ret.put(colName, value);
		}
		
		return ret;
	}

	@Override
	public void init(ResultSet rs) throws SQLException {		
		
		ResultSetMetaData metadata = rs.getMetaData();
		
		int columnCount = metadata.getColumnCount();
		this.columnLabels = new String[columnCount];
		
		for (int i = 1; i <= columnCount; i++){
			
			String colName = metadata.getColumnLabel(i);				
			
			this.columnLabels[i - 1] = colName;
		}
	}

	@Override
	public void terminate() {				
	}

}
