package io.github.joseerodrigues.utils.dbutil;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLConnectionFactory {

    Connection getConnection() throws SQLException;

}
