package xyz.upperlevel.uppercore.util.nms.refl.field;

import static xyz.upperlevel.uppercore.util.nms.NmsUtil.handleException;

public class IntField {
    private final java.lang.reflect.Field handle;

    public IntField(java.lang.reflect.Field handle) {
        this.handle = handle;
    }

    public void set(Object instance, int value) {
        try {
            handle.setInt(instance, value);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public int get(Object instance) {
        try {
            return handle.getInt(instance);
        } catch (Exception e) {
            handleException(e);
            return -1;
        }
    }

    public static IntField of(java.lang.reflect.Field handle) {
        return new IntField(handle);
    }
}
