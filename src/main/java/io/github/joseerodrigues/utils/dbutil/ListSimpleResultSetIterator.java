package io.github.joseerodrigues.utils.dbutil;

import java.util.ArrayList;
import java.util.List;

import io.github.joseerodrigues.utils.dbutil.mapper.SimpleResultSetIterator;

class ListSimpleResultSetIterator<T> extends SimpleResultSetIterator<T> {

	private ArrayList<T> list = new ArrayList<>();
	
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
