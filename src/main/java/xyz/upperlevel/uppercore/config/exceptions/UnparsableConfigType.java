package xyz.upperlevel.uppercore.config.exceptions;

import java.lang.reflect.Type;

public class UnparsableConfigType extends RuntimeException {
    public UnparsableConfigType(Type type) {
        super("Cannot find parser for type " + extractName(type));
    }

    private static String extractName(Type t) {
        if (t instanceof Class) return ((Class) t).getName();
        return t.getTypeName();
    }
}
