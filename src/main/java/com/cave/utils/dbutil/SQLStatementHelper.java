package com.cave.utils.dbutil;

import java.util.*;
import java.util.Map.Entry;

public class SQLStatementHelper {

	private StringBuilder query = null;
	private List<String> paramsSql = new ArrayList<String>();
	private Map<String, Integer> paramsInSql = new HashMap<String, Integer>();
	private List<Object> paramValues = new ArrayList<Object>();		
	
	public SQLStatementHelper(String sql){
		
		this.query = new StringBuilder(sql);		
	}
	
	public void addParam(String paramString, Object paramValue){
		paramsSql.add(paramString);
		paramValues.add(paramValue);
	}
	
	public void addInParam(String paramString, Set<String> paramValues){
		paramsInSql.put(paramString, paramValues.size());
		
		for (Object v : paramValues){
			this.paramValues.add(v);
		}		
	}
	
	private StringBuilder genSQLQuery(StringBuilder sqlQuery, List<String> paramsSql) {
		
		if (paramsSql == null || paramsSql.isEmpty()){
			return sqlQuery;
		}
		
		StringBuilder ret = sqlQuery;
		
		for (int i = 0, s = paramsSql.size(); i < s; i++){
			
			String param = paramsSql.get(i);						
			
			ret.append(param);
			
			if (i < s - 1){
				ret.append(" AND ");
			}
		}
		
		// tratar dos params in
		
		for (Entry<String, Integer> entry : paramsInSql.entrySet()){
			
			String param = entry.getKey();
			int nParams = entry.getValue();
			ret.append(" AND ").append(param).append(" IN (");
			
			for (int i = 0; i < nParams; i++){
				
				ret.append("?");
				
				if (i < nParams - 1){
					ret.append(",");
				}
			}
			ret.append(")");
		}
		
		return ret;
	}

	public StringBuilder getSQLQuery(){
		return genSQLQuery(this.query, this.paramsSql);
	}
	
	public Object[] getParamValues(){
		return this.paramValues.toArray();
	}
	
	
}
