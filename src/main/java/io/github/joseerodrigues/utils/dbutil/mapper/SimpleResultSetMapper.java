package io.github.joseerodrigues.utils.dbutil.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.github.joseerodrigues.utils.dbutil.ResultSetMapper;

public abstract class SimpleResultSetMapper<E> implements ResultSetMapper<E> {

	@Override
	public void init(ResultSet rs) throws SQLException {
		
	}

	@Override
	public abstract E mapObject(ResultSet rs) throws SQLException;

	@Override
	public void terminate() {	
		
	}

}
