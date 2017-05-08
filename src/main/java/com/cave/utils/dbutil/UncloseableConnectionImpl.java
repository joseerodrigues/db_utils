package com.cave.utils.dbutil;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

class UncloseableConnectionImpl implements Connection{


    private Connection con = null;

	UncloseableConnectionImpl(Connection con) {
		super();
		this.con = con;
	}

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return con.unwrap(aClass);
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return con.isWrapperFor(aClass);
    }


    @Override
	public Statement createStatement() throws SQLException {
		return con.createStatement();
	}

	@Override
	public PreparedStatement prepareStatement(String s) throws SQLException {
		return con.prepareStatement(s);
	}

	@Override
	public CallableStatement prepareCall(String s) throws SQLException {
		return con.prepareCall(s);
	}

	@Override
	public String nativeSQL(String s) throws SQLException {
		return con.nativeSQL(s);
	}

	@Override
	public void setAutoCommit(boolean b) throws SQLException {
		con.setAutoCommit(b);
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return con.getAutoCommit();
	}

	@Override
	public void commit() throws SQLException {
		con.commit();
	}

	@Override
	public void rollback() throws SQLException {
		con.rollback();
	}

	@Override
	public void close() throws SQLException {
		//con.close();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return con.isClosed();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return con.getMetaData();
	}

	@Override
	public void setReadOnly(boolean b) throws SQLException {
		con.setReadOnly(b);
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return con.isReadOnly();
	}

	@Override
	public void setCatalog(String s) throws SQLException {
		con.setCatalog(s);
	}

	@Override
	public String getCatalog() throws SQLException {
		return con.getCatalog();
	}

	@Override
	public void setTransactionIsolation(int i) throws SQLException {
		con.setTransactionIsolation(i);
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return con.getTransactionIsolation();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return con.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		con.clearWarnings();
	}

	@Override
	public Statement createStatement(int i, int i1) throws SQLException {
		return con.createStatement(i, i1);
	}

	@Override
	public PreparedStatement prepareStatement(String s, int i, int i1) throws SQLException {
		return con.prepareStatement(s, i, i1);
	}

	@Override
	public CallableStatement prepareCall(String s, int i, int i1) throws SQLException {
		return con.prepareCall(s, i, i1);
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return con.getTypeMap();
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		con.setTypeMap(map);
	}

	@Override
	public void setHoldability(int i) throws SQLException {
		con.setHoldability(i);
	}

	@Override
	public int getHoldability() throws SQLException {
		return con.getHoldability();
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return con.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String s) throws SQLException {
		return con.setSavepoint(s);
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		con.rollback(savepoint);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		con.releaseSavepoint(savepoint);
	}

	@Override
	public Statement createStatement(int i, int i1, int i2) throws SQLException {
		return con.createStatement(i, i1, i2);
	}

	@Override
	public PreparedStatement prepareStatement(String s, int i, int i1, int i2) throws SQLException {
		return con.prepareStatement(s, i, i1, i2);
	}

	@Override
	public CallableStatement prepareCall(String s, int i, int i1, int i2) throws SQLException {
		return con.prepareCall(s, i, i1, i2);
	}

	@Override
	public PreparedStatement prepareStatement(String s, int i) throws SQLException {
		return con.prepareStatement(s, i);
	}

	@Override
	public PreparedStatement prepareStatement(String s, int[] ints) throws SQLException {
		return con.prepareStatement(s, ints);
	}

	@Override
	public PreparedStatement prepareStatement(String s, String[] strings) throws SQLException {
		return con.prepareStatement(s, strings);
	}

	@Override
	public Clob createClob() throws SQLException {
		return con.createClob();
	}

	@Override
	public Blob createBlob() throws SQLException {
		return con.createBlob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return con.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return con.createSQLXML();
	}

	@Override
	public boolean isValid(int i) throws SQLException {
		return con.isValid(i);
	}

	@Override
	public void setClientInfo(String s, String s1) throws SQLClientInfoException {
		con.setClientInfo(s, s1);
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		con.setClientInfo(properties);
	}

	@Override
	public String getClientInfo(String s) throws SQLException {
		return con.getClientInfo(s);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return con.getClientInfo();
	}

	@Override
	public Array createArrayOf(String s, Object[] objects) throws SQLException {
		return con.createArrayOf(s, objects);
	}

	@Override
	public Struct createStruct(String s, Object[] objects) throws SQLException {
		return con.createStruct(s, objects);
	}

	@Override
	public void setSchema(String s) throws SQLException {
		con.setSchema(s);
	}

	@Override
	public String getSchema() throws SQLException {
		return con.getSchema();
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		con.abort(executor);
	}

	@Override
	public void setNetworkTimeout(Executor executor, int i) throws SQLException {
		con.setNetworkTimeout(executor, i);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return con.getNetworkTimeout();
	}
}
