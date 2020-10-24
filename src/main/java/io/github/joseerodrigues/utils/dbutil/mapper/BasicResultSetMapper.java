package io.github.joseerodrigues.utils.dbutil.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BasicResultSetMapper<E> extends SimpleResultSetMapper<E> {

    private String colName = null;
    private int colIndex = -1;

    public BasicResultSetMapper(String colName) {

        if (colName == null){
            throw new NullPointerException("colName");
        }

        if (colName.trim().isEmpty()){
            throw new IllegalArgumentException("colName is empty");
        }
        this.colName = colName;
    }

    public BasicResultSetMapper(int colIndex) {

        if (colIndex < 1){
            throw new IllegalArgumentException( "colIndex < 1 : " + colIndex);
        }

        this.colIndex = colIndex;
    }

    @SuppressWarnings("unchecked")
	@Override
    public E mapObject(ResultSet rs) throws SQLException {
        if (colName != null){
            return (E) rs.getObject(this.colName);
        }
        return (E) rs.getObject(this.colIndex);
    }
}
