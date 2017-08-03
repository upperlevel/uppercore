package xyz.upperlevel.uppercore.util.nms.refl.field;

import static xyz.upperlevel.uppercore.util.nms.Nms.handleException;

public class BoolField {
    private final java.lang.reflect.Field handle;

    public BoolField(java.lang.reflect.Field handle) {
        this.handle = handle;
    }

    public void set(Object instance, boolean value) {
        try {
            handle.setBoolean(instance, value);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public boolean get(Object instance) {
        try {
            return handle.getBoolean(instance);
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    public static BoolField of(java.lang.reflect.Field handle) {
        return new BoolField(handle);
    }
}
