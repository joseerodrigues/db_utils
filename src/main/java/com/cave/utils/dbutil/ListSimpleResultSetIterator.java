package com.cave.utils.dbutil;

import java.util.ArrayList;
import java.util.List;

class ListSimpleResultSetIterator<T> extends SimpleResultSetIterator<T> {

	private ArrayList<T> list = new ArrayList<T>();
	
	private boolean continueIteration = false;
	
	ListSimpleResultSetIterator(boolean continueIteration){
		this.continueIteration = continueIteration;
	}
	
	@Override
	public boolean iterate(T item) {
		this.list.add(item);
		return continueIteration;
	}

	List<T> getList(){
		
		this.list.trimToSize();
		
		return this.list;
	}
}
