package com.cave.utils.dbutil.dao;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cave.utils.dbutil.DBUtil;
import com.cave.utils.dbutil.ResultSetMapper;
import com.cave.utils.dbutil.annotation.DBColumn;
import com.cave.utils.dbutil.annotation.DBTable;
import com.cave.utils.dbutil.annotation.DefaultSelectClause;
import com.cave.utils.dbutil.mapper.Mappers;
import com.cave.utils.reflection.ReflectionUtils;

public class DAOBuilder {

	static class FieldInfo {
		Field field;
		boolean trim = false;
	}

	static class InitContainer {
		Map<String, FieldInfo> colsFields = Collections.emptyMap();
		List<String> primaryKeysColumns = Collections.emptyList();
		List<String> generatedColumns = Collections.emptyList();
		String tableName = "";
		@SuppressWarnings("rawtypes")
		ResultSetMapper mapper = Mappers.mapMapper();
		String defaultSelectClause = "";
	}

	private static final ConcurrentHashMap<Class<?>, InitContainer> classInitContainer = new ConcurrentHashMap<>();

	private static<E> List<Field> getAnnotatedFields(Class<E> typeToken, Class<? extends Annotation> annotationClass) {

		List<Field> ret = new ArrayList<>();
		Class<?> parent = typeToken;

		while (parent != null) {
			Field[] fields = parent.getDeclaredFields();

			for (Field f : fields) {
				if (f.getAnnotation(annotationClass) != null) {
					ret.add(f);
				}
			}

			parent = parent.getSuperclass();
		}

		return ret;
	}

	public static <T> DAO<T> build(DBUtil dbUtil, Class<T> classType) {

		InitContainer ic = DAOBuilder.classInitContainer.computeIfAbsent(classType, (token) -> {
			InitContainer ret = new InitContainer();
			Map<String, FieldInfo> colsFields = new HashMap<>();
			List<Field> fields = getAnnotatedFields(classType, DBColumn.class);
			List<String> pkCols = new ArrayList<>();
			List<String> genCols = new ArrayList<>();

			DBTable tableA = ReflectionUtils.getAnnotationFromType(classType, DBTable.class);

			if (tableA != null) {
				ret.tableName = tableA.value();
			}

			DefaultSelectClause defaultSelectClauseA = ReflectionUtils.getAnnotationFromType(classType, DefaultSelectClause.class);

			if (defaultSelectClauseA != null) {
				ret.defaultSelectClause = defaultSelectClauseA.value();
			}

			ret.mapper = Mappers.beanMapper(classType);

			for (Field f : fields) {
				DBColumn dbCol = f.getAnnotation(DBColumn.class);

				String dbColName = dbCol.value().isEmpty() ? f.getName() : dbCol.value();
				f.setAccessible(true);

				FieldInfo fi = new FieldInfo();
				fi.field = f;
				fi.trim = dbCol.trim();

				colsFields.put(dbColName, fi);

				if (dbCol.pk()) {
					pkCols.add(dbColName);
				}

				if (dbCol.generated()) {
					genCols.add(dbColName);
				}
			}

			ret.primaryKeysColumns = Collections.unmodifiableList(pkCols);
			ret.colsFields = colsFields;
			ret.generatedColumns = Collections.unmodifiableList(genCols);
			return ret;
		});


		return new SimpleDAOImpl<>(dbUtil, ic);
	}
}
