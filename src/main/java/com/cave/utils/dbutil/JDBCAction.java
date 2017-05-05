package com.cave.utils.dbutil;

import java.sql.Connection;
import java.sql.SQLException;

public interface JDBCAction<T> {

	public T execute(Connection conn) throws SQLException;
}
