package com.cave.utils.dbutil;

public class DataAccessException extends RuntimeException{

	private static final long serialVersionUID = -5833140026330200485L;

	public DataAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataAccessException(Throwable cause) {
		super(cause);
	}
}
