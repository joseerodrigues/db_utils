package com.cave.utils.dbutil.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import com.cave.utils.Checks;
import com.cave.utils.dbutil.DBUtil;
import com.cave.utils.dbutil.ResultSetMapper;
import com.cave.utils.dbutil.dao.DAOBuilder.FieldInfo;
import com.cave.utils.dbutil.dao.DAOBuilder.InitContainer;
import com.cave.utils.dbutil.mapper.ResultSetMap;

public class SimpleDAOImpl<T> implements DAO<T>{

	private static final ResultSetMapper<Long> COUNT_MAPPER = new CountMapper();

	private DBUtil dbUtil = null;
	private InitContainer classInfo = null;


	public SimpleDAOImpl(DBUtil dbUtil, InitContainer classInfo) {
		super();
		this.dbUtil = dbUtil;
		this.classInfo = classInfo;
	}


	String getSqlSelect(String condition, String selectFields) {
		Checks.checkNull(condition, "condition cannot be null");

		StringBuilder query = new StringBuilder("SELECT ");
		query.append(selectFields).append(" FROM ");
		query.append(classInfo.tableName);

		if (!condition.trim().isEmpty()) {
			query.append(" WHERE ").append(condition);

		}else if (!classInfo.defaultSelectClause.trim().isEmpty()) {
			query.append(" WHERE ").append(classInfo.defaultSelectClause);
		}

		return query.toString();
	}

	@Override
	public long count(String sqlCondition, Object... params) {
		String query = getSqlSelect(sqlCondition, "COUNT(1) AS COUNT");

		return dbUtil.selectOne(query, COUNT_MAPPER, params);
	}

	@Override
	public long count() {
		return count("", (Object[])null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> selectAll(String sqlCondition, Object... params) {
		String query = getSqlSelect(sqlCondition, "*");

		return dbUtil.selectAll(query, classInfo.mapper, params);
	}

	@Override
	public List<T> selectAll() {
		return selectAll("", (Object[])null);
	}


	@SuppressWarnings("unchecked")
	@Override
	public T selectOne(String sqlCondition, Object... params) {
		String query = getSqlSelect(sqlCondition, "*");

		return (T) dbUtil.selectOne(query, classInfo.mapper, params);
	}


	@Override
	public boolean insert(T obj) {
		return this.insert(this.dbUtil, obj);
	}

	@Override
	public boolean insert(DBUtil dbUtil, T obj) {

		StringBuilder sb = new StringBuilder("INSERT INTO ");
		sb.append(classInfo.tableName).append(" (");

		Map<String, FieldInfo> colsFields = classInfo.colsFields;

		String[] cols = excludeGeneratedCols(colsFields.keySet()).toArray(new String[0]);

		for (int i = 0; i < cols.length; i++) {
			String col = cols[i];
			sb.append(col);

			if (i + 1 < cols.length) {
				sb.append(", ");
			}
		}
		sb.append(") VALUES (");

		for (int i = 0; i < cols.length; i++) {
			sb.append("?");

			if (i + 1 < cols.length) {
				sb.append(", ");
			}
		}

		sb.append(")");
		List<Object> params = new ArrayList<>();

		for (String c : cols) {
			FieldInfo fi = colsFields.get(c);

			try {
				Object paramValue = fi.field.get(obj);
				params.add(paramValue);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace(System.err);
				return false;
			}
		}

		if (classInfo.generatedColumns.isEmpty()) {
			int i = dbUtil.insert(sb.toString(), params.toArray(new Object[0]));
			return i > 0;
		}else {
			ResultSetMap generatedKeys = dbUtil.getKeysForInsert(sb.toString(), params.toArray(new Object[0]));

			for (Entry<String, Object> entry : generatedKeys.entrySet()) {
				try {
					classInfo.colsFields.get(entry.getKey()).field.set(obj, entry.getValue());
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace(System.err);
					return false;
				}
			}
			return true;
		}

	}


	private List<String> excludeGeneratedCols(Set<String> keySet) {
		List<String> ret = new ArrayList<>();

		for (String s : keySet) {
			if (!classInfo.generatedColumns.contains(s)) {
				ret.add(s);
			}
		}

		return ret;
	}


	@Override
	public boolean delete(DBUtil dbUtil, T obj) {
		String[] pkCols = classInfo.primaryKeysColumns.toArray(new String[0]);

		if (pkCols.length == 0) {
			throw new IllegalArgumentException("object does not have primary keys defined");
		}

		List<Object> params = new ArrayList<>();
		StringBuilder sb = new StringBuilder("DELETE FROM ");
		sb.append(classInfo.tableName).append(" WHERE ");

		for (int i = 0; i < pkCols.length; i++) {
			String col = pkCols[i];

			sb.append(col).append(" = ?");

			if (i + 1 < pkCols.length ) {
				sb.append(", ");
			}

			try {
				Object paramValue = classInfo.colsFields.get(col).field.get(obj);
				params.add(paramValue);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace(System.err);
				return false;
			}
		}

		int i = dbUtil.delete(sb.toString(), params.toArray(new Object[0]));
		return i > 0;
	}

	@Override
	public boolean delete(T obj) {
		return delete(dbUtil, obj);
	}

	@Override
	public String getTableName() {
		return this.classInfo.tableName;
	}


	@Override
	public boolean delete(DBUtil dbUtil, String sqlCondition, Object... params) {

		Checks.checkNull(sqlCondition, "condition cannot be null");

		StringBuilder sb = new StringBuilder("DELETE FROM ");
		sb.append(classInfo.tableName);

		if (!sqlCondition.trim().isEmpty()) {
			sb.append(" WHERE ").append(sqlCondition);
		}

		return dbUtil.delete(sb.toString(), params) > 0;
	}

	@Override
	public boolean delete(String sqlCondition, Object... params) {
		return delete(dbUtil, sqlCondition, params);
	}

	@Override
	public boolean update(DBUtil dbUtil, T obj) {
		String[] pkCols = classInfo.primaryKeysColumns.toArray(new String[0]);

		if (pkCols.length == 0) {
			throw new IllegalArgumentException("object does not have primary keys defined");
		}

		List<Object> params = new ArrayList<>();
		StringBuilder sb = new StringBuilder("UPDATE ");
		sb.append(classInfo.tableName).append(" SET ");


		String[] cols = classInfo.colsFields.keySet().toArray(new String[0]);
		for (int i = 0; i < cols.length; i++) {
			String col = cols[i];

			if (classInfo.primaryKeysColumns.contains(col)) {
				continue;
			}

			sb.append(col).append(" = ?");

			if (i + 1 < cols.length ) {
				sb.append(", ");
			}

			try {
				Object paramValue = classInfo.colsFields.get(col).field.get(obj);
				params.add(paramValue);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace(System.err);
				return false;
			}
		}

		sb.append(" WHERE ");

		for (int i = 0; i < pkCols.length; i++) {
			String col = pkCols[i];

			sb.append(col).append(" = ?");

			if (i + 1 < pkCols.length ) {
				sb.append(", ");
			}

			try {
				Object paramValue = classInfo.colsFields.get(col).field.get(obj);
				params.add(paramValue);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace(System.err);
				return false;
			}
		}

		int r = dbUtil.update(sb.toString(), params.toArray(new Object[0]));
		return r > 0;
	}

	@Override
	public boolean update(T obj) {
		return update(dbUtil, obj);
	}


	@Override
	public <R> R insideTransaction(Function<DBUtil, R> function) {
		return dbUtil.insideTransaction(function);
	}


	@Override
	public Set<String> getColumnNames() {
		return Collections.unmodifiableSet(this.classInfo.colsFields.keySet());
	}


	@SuppressWarnings("unchecked")
	@Override
	public ResultSetMapper<T> getResultSetMapper() {
		return this.classInfo.mapper;
	}

}
