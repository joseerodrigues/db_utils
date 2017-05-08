package com.cave.utils.dbutil.mapper;

import com.cave.utils.dbutil.SimpleResultSetMapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BeanResultSetMapper<E> extends SimpleResultSetMapper<E> {

    private static class FieldInfo {
        Field field;
        boolean trim = false;
    }

    private Class<E> typeToken = null;
    private Map<String, FieldInfo> colsFields = new HashMap<>();

    public BeanResultSetMapper(Class<E> typeToken) {
        this.typeToken = typeToken;

        Field[] fields = typeToken.getDeclaredFields();

        for (Field f : fields){
            DBColumn dbCol = f.getAnnotation(DBColumn.class);
            if (dbCol == null){
                continue;
            }

            String dbColName = dbCol.value().isEmpty() ? f.getName() : dbCol.value();
            f.setAccessible(true);

            FieldInfo fi = new FieldInfo();
            fi.field = f;
            fi.trim = dbCol.trim();

            colsFields.put(dbColName, fi);
        }
    }

    @Override
    public void init(ResultSet rs) throws SQLException {

        ResultSetMetaData metadata = rs.getMetaData();
        int colCount = metadata.getColumnCount();

        for (int i = 1; i <= colCount; i++){

            String colLabel = metadata.getColumnLabel(i);

            if (!this.colsFields.containsKey(colLabel)){
                System.out.println("WARN: Column '" + colLabel + "' not found");
            }
        }
    }

    @Override
    public E mapObject(ResultSet rs) throws SQLException {

        E instance;

        try {
            instance = typeToken.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        for (Map.Entry<String, FieldInfo> entry : this.colsFields.entrySet()){

            String colName = entry.getKey();
            FieldInfo fi = entry.getValue();
            Field field = fi.field;
            boolean trim = fi.trim;

            try {
                Object value = rs.getObject(colName);
                if (trim){
                    value = ((String)value).trim();
                }

                field.set(instance, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return instance;
    }
}
