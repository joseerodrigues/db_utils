package io.github.joseerodrigues.utils.dbutil.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.github.joseerodrigues.utils.dbutil.mapper.SimpleResultSetMapper;

class CountMapper extends SimpleResultSetMapper<Long>{

	@Override
	public Long mapObject(ResultSet rs) throws SQLException {

		Object rsObj = rs.getObject("COUNT");

		if (rsObj instanceof Integer) {
			return ((Integer)rsObj).longValue();
		}

		return (Long) rsObj;
	}

}
