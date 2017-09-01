package xyz.upperlevel.uppercore.util.nms.refl.field;

import static xyz.upperlevel.uppercore.util.nms.NmsUtil.handleException;

public class Field<T> {
    private final java.lang.reflect.Field handle;

    public Field(java.lang.reflect.Field handle) {
        this.handle = handle;
    }

    public void set(Object instance, T value) {
        try {
            handle.set(instance, value);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public T get(Object instance) {
        try {
            return (T) handle.get(instance);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public static Field of(java.lang.reflect.Field handle) {
        return new Field(handle);
    }
}
