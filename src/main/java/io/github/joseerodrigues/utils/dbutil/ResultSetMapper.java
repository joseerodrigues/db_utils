package io.github.joseerodrigues.utils.dbutil;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetMapper<E> {

	void init(ResultSet rs) throws SQLException;
	
	void terminate();
	
	E mapObject(ResultSet rs) throws SQLException;
}
