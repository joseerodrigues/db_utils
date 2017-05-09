package com.cave.utils.dbutil.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringResultSetMapper extends BasicResultSetMapper<String> {

    private boolean trim = false;

    public StringResultSetMapper(String colName, boolean trim) {
        super(colName);
        this.trim = trim;
    }

    public StringResultSetMapper(int colIndex, boolean trim) {
        super(colIndex);
        this.trim = trim;
    }

    @Override
    public String mapObject(ResultSet rs) throws SQLException {

        String s = super.mapObject(rs);

        if (s != null && this.trim){
            return s.trim();
        }

        return s;
    }
}
