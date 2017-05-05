package com.cave.utils.dbutil;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetMapper<E> {

	public void init(ResultSet rs) throws SQLException;
	
	public void terminate();
	
	public E mapObject(ResultSet rs) throws SQLException;
	
	
}
