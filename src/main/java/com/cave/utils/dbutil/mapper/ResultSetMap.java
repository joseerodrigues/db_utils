package com.cave.utils.dbutil.mapper;

import java.math.BigDecimal;
import java.util.*;

public class ResultSetMap extends TreeMap<String, Object> {
	
	private static final long serialVersionUID = 391972271611412480L;

	public ResultSetMap() {
		super();
	}

	ResultSetMap(Comparator<? super String> theComparator) {
		super(theComparator);
	}

	ResultSetMap(Map<? extends String, Object> map) {
		super(map);
	}

	ResultSetMap(SortedMap<String, Object> map) {
		super(map);
	}
	
	private void checkNullKey(String key){
		if (key == null){
			throw new NullPointerException("key");
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getAsType(String key, Class<T> type){
		checkNullKey(key);
		
		Object o = this.get(key);
		
		if (o != null){
			return (T)o;
		}
		
		return null;
	}
		
	public String getString(String key){
		return getAsType(key, String.class);
	}
	
	public Integer getInt(String key){		
		return getAsType(key, Integer.class);
	}
	
	public Long getLong(String key){
		return getAsType(key, Long.class);
	}
	
	public Byte getByte(String key){
		return getAsType(key, Byte.class);
	}
	
	public Double getDouble(String key){
		return getAsType(key, Double.class);
	}
	
	public Float getFloat(String key){
		return getAsType(key, Float.class);
	}
	
	public Date getDate(String key){
		return getAsType(key, Date.class);
	}

	public BigDecimal getBigDecimal(String key){
		return getAsType(key, BigDecimal.class);
	}
}
