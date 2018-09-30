package xyz.upperlevel.uppercore.util;

import lombok.NonNull;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public final class TypeUtil {
    public static Type typeOf(Type base, Type... typeArgs) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return typeArgs;
            }

            @Override
            public Type getRawType() {
                return base;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

    public static Type arrayTypeOf(Type base) {
        return (GenericArrayType) () -> base;
    }

    public static Builder typeBuilder(Type base) {
        return new Builder(base);
    }

    public static class Builder {
        private Type base;
        private List<Type> typeArgs;

        public Builder(@NonNull Type base) {
            this.base = base;
        }

        public void addType(@NonNull Type arg) {
            this.typeArgs.add(arg);
        }

        public Type build() {
            return typeOf(base, typeArgs.toArray(new Type[0]));
        }
    }
}
