package xyz.upperlevel.uppercore.util.nms;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import xyz.upperlevel.uppercore.util.nms.exceptions.UnsupportedVersionException;

import java.lang.reflect.Method;
import java.util.Arrays;

public final class NmsUtil {
	public static void handleException(Exception e) {
		throw new UnsupportedVersionException(e);
	}

	public static Method getMethod(Class<?> clazz, Class<?> returnType, Class<?>... args) throws NoSuchMethodException {
		return searchMethod(clazz.getMethods(), returnType, args);
	}

	public static Method getDeclaredMethod(Class<?> clazz, Class<?> returnType, Class<?>... args)
			throws NoSuchMethodException {
		return searchMethod(clazz.getDeclaredMethods(), returnType, args);
	}

	public static Method searchMethod(Method[] classes, Class<?> returnType, Class<?>... args)
			throws NoSuchMethodException {
		for (Method method : classes) {
			if (method.getReturnType() == returnType && Arrays.equals(method.getParameterTypes(), args)) {
				return method;
			}
		}
		throw new NoSuchMethodException(
				"Cannot find method that returns " + returnType + " with parameters " + Arrays.toString(args));
	}

	private NmsUtil() {
	}
}
