package com.cave.utils.dbutil.helper;

import java.util.*;
import java.util.Map.Entry;

public class SQLStatementHelper {

	private StringBuilder query = null;
	private List<String> paramsSql = new ArrayList<>();
	private Map<String, Integer> paramsInSql = new HashMap<>();
	private List<Object> paramValues = new ArrayList<>();
	
	public SQLStatementHelper(String sql){
		
		this.query = new StringBuilder(sql);		
	}
	
	public void addParam(String paramString, Object paramValue){
		paramsSql.add(paramString);
		paramValues.add(paramValue);
	}
	
	public void addInParam(String paramString, Set<String> paramValues){
		paramsInSql.put(paramString, paramValues.size());

		this.paramValues.addAll(paramValues);
	}
	
	private StringBuilder genSQLQuery(StringBuilder sqlQuery, List<String> paramsSql) {
		
		if (paramsSql == null || paramsSql.isEmpty()){
			return sqlQuery;
		}

		for (int i = 0, s = paramsSql.size(); i < s; i++){
			
			String param = paramsSql.get(i);						
			
			sqlQuery.append(param);
			
			if (i < s - 1){
				sqlQuery.append(" AND ");
			}
		}
		
		// tratar dos params in
		
		for (Entry<String, Integer> entry : paramsInSql.entrySet()){
			
			String param = entry.getKey();
			int nParams = entry.getValue();
			sqlQuery.append(" AND ").append(param).append(" IN (");
			
			for (int i = 0; i < nParams; i++){
				
				sqlQuery.append("?");
				
				if (i < nParams - 1){
					sqlQuery.append(",");
				}
			}
			sqlQuery.append(")");
		}
		
		return sqlQuery;
	}

	public StringBuilder getSQLQuery(){
		return genSQLQuery(this.query, this.paramsSql);
	}
	
	public Object[] getParamValues(){
		return this.paramValues.toArray();
	}
	
	
}
