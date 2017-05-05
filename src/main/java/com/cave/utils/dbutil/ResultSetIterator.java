package com.cave.utils.dbutil;


public interface ResultSetIterator<T> {

	public void init();
	
	public void terminate();
	
	/**
	 * 
	 * @param item
	 * @return true to continue iterating, false otherwise
	 * @author 92429
	 */
	public boolean iterate(T item);
	
}
