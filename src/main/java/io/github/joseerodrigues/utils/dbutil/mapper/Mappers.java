package io.github.joseerodrigues.utils.dbutil.mapper;

import io.github.joseerodrigues.utils.dbutil.ResultSetMapper;

public final class Mappers {

    public static ResultSetMapper<ResultSetMap> mapMapper(){
        return new MapResultSetMapper();
    }

    public static ResultSetMapper<Integer> intMapper(String colName){
        return new BasicResultSetMapper<Integer>(colName);
    }

    public static ResultSetMapper<Integer> intMapper(int colIndex){
        return new BasicResultSetMapper<Integer>(colIndex);
    }

    public static ResultSetMapper<String> stringMapper(String colName){
        return new StringResultSetMapper(colName, false);
    }

    public static ResultSetMapper<String> stringMapper(int colIndex){
        return new StringResultSetMapper(colIndex, false);
    }

    public static ResultSetMapper<String> stringMapper(String colName, boolean trim){
        return new StringResultSetMapper(colName, trim);
    }

    public static ResultSetMapper<String> stringMapper(int colIndex, boolean trim){
        return new StringResultSetMapper(colIndex, false);
    }

    public static <E> ResultSetMapper<E> beanMapper(Class<E> typeToken){
        return new BeanResultSetMapper<>(typeToken);
    }
}
