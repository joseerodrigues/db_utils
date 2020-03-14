package com.cave.utils.dbutil;

public interface ResultSetIterator<T> {

	void init();
	
	void terminate();
	
	/**
	 * 
	 * @param item the item mapped from the resultSet
	 * @return true to continue iterating, false otherwise
	 */
	boolean iterate(T item);
	
}
