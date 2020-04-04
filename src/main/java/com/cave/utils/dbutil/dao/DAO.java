package com.cave.utils.dbutil.dao;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.cave.utils.dbutil.DBUtil;
import com.cave.utils.dbutil.ResultSetMapper;

public interface DAO<T> {

	String getTableName();
	Set<String> getColumnNames();
	ResultSetMapper<T> getResultSetMapper();

	long count();
	long count(DBUtil dbUtil, String sqlCondition, Object ... params);
	long count(String sqlCondition, Object ... params);

	List<T> selectAll();
	List<T> selectAll(String sqlCondition, Object ... params);
	List<T> selectAll(DBUtil dbUtil, String sqlCondition, Object ... params);

	T selectOne(String sqlCondition, Object ... params);
	T selectOne(DBUtil dbUtil, String sqlCondition, Object ... params);

	boolean insert(T obj);
	boolean insert(DBUtil dbUtil, T obj);

	boolean update(T obj);
	boolean update(DBUtil dbUtil, T obj);

	boolean delete(T obj);
	boolean delete(DBUtil dbUtil, T obj);
	boolean delete(String sqlCondition, Object ... params);
	boolean delete(DBUtil dbUtil, String sqlCondition, Object ... params);

	<R> R insideTransaction(Function<DBUtil, R> function);
}
