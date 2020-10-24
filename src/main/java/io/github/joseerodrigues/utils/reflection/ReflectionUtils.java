package io.github.joseerodrigues.utils.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

	private static Class<?> getClassByName(String name){

		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace(System.err);
		}

		return null;
	}
	public static Class<?> getCallingClass(){
		return getCallingClass((Class<?>[])null);
	}

	public static Class<?> getCallingClass(Class<?> ... classesToIgnore){

		StackTraceElement[] stack = Thread.currentThread().getStackTrace();

		List<String> ignoreClassNames = new ArrayList<String>();

		ignoreClassNames.add(ReflectionUtils.class.getName());

		if (classesToIgnore != null && classesToIgnore.length > 0) {
			for (Class<?> cls : classesToIgnore) {
				ignoreClassNames.add(cls.getName());
			}
		}

		int stackIdx = 1;
		for (; stackIdx < stack.length && ignoreClassNames.contains(stack[stackIdx].getClassName()); stackIdx++) {
			;
		}

		return getClassByName(stack[stackIdx+1].getClassName());
	}

	public static boolean hasPublicNoArgConstructor(Class<?> clazz) {

		Constructor<?>[] cs = clazz.getConstructors();

		for (Constructor<?> c : cs) {
			if (Modifier.isPublic(c.getModifiers()) && c.getParameterCount() == 0) {
				return true;
			}
		}

		return false;
	}

	public static <T extends Annotation> T getAnnotationFromType(Class<?> type, Class<T> annotationType){
		T ret = null;
		while(!type.equals(Object.class)) {
			ret = type.getAnnotation(annotationType);

			if (ret != null) {
				break;
			}

			type = type.getSuperclass();
		}

		return ret;
	}
}
