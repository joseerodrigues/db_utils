package com.cave.utils.dbutil;


public interface ResultSetIterator<T> {

	void init();
	
	void terminate();
	
	/**
	 * 
	 * @param item
	 * @return true to continue iterating, false otherwise
	 */
	boolean iterate(T item);
	
}
