package xyz.upperlevel.uppercore.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class GenericUtil {

    public static Type getGenericChildren(Type rawType, int index) {
        ParameterizedType type = (ParameterizedType) rawType;

        return type.getActualTypeArguments()[index];
    }

    public static Type getGenericChildren(Type rawType) {
        return getGenericChildren(rawType, 0);
    }

    public static Class<?> extractClassFromType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            throw new IllegalStateException("Cannot find class for " + type);
        }
    }

    private GenericUtil(){}
}
