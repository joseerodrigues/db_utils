package com.cave.utils;

public class Checks {

	public static void checkNull(Object o, String msg){
		if (o == null){
			throw new NullPointerException(msg);
		}
	}

	public static void checkEmpty(String s, String msg){
		if (s.isEmpty()){
			throw new IllegalArgumentException(msg);
		}
	}

	public static void checkNullOrEmpty(String s, String msg){
		checkNull(s, msg);
		checkEmpty(s, msg);
	}
}
