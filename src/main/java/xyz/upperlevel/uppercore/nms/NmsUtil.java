package xyz.upperlevel.uppercore.nms;

import xyz.upperlevel.uppercore.nms.exceptions.UnsupportedVersionException;

import java.lang.reflect.Method;
import java.util.Arrays;

public final class NmsUtil {
    public static void handleException(Exception e) {
        throw new UnsupportedVersionException(e);
    }

    public static Method getMethod(Class<?> clazz, Class<?> returnType, Class<?>... args) throws NoSuchMethodException {
        return searchMethod(clazz.getMethods(), returnType, args);
    }

    public static Method getDeclaredMethod(Class<?> clazz, Class<?> returnType, Class<?>... args) throws NoSuchMethodException {
        return searchMethod(clazz.getDeclaredMethods(), returnType, args);
    }

    public static Method searchMethod(Method[] methods, Class<?> returnType, Class<?>... args) throws NoSuchMethodException {
        for (Method method : methods) {
            if (     method.getReturnType() == returnType &&
                    Arrays.equals(method.getParameterTypes(), args)) {
                return method;
            }
        }
        throw new NoSuchMethodException("Cannot find method that returns " + returnType + " with parameters " + Arrays.toString(args));
    }

    private NmsUtil(){}
}
