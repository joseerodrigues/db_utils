package com.cave.utils.dbutil.mapper;

import com.cave.utils.dbutil.SimpleResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BasicResultSetMapper<E> extends SimpleResultSetMapper<E> {

    private String colName = null;
    private int colIndex = -1;

    public BasicResultSetMapper(String colName) {
        this.colName = colName;
    }

    public BasicResultSetMapper(int colIndex) {
        this.colIndex = colIndex;
    }

    @Override
    public E mapObject(ResultSet rs) throws SQLException {
        if (colName != null){
            return (E) rs.getObject(this.colName);
        }
        return (E) rs.getObject(this.colIndex);
    }
}
