package xyz.upperlevel.uppercore.util.nms.refl.field;

import static xyz.upperlevel.uppercore.util.nms.NmsUtil.handleException;

public class LongField {
    private final java.lang.reflect.Field handle;

    public LongField(java.lang.reflect.Field handle) {
        this.handle = handle;
    }

    public void set(Object instance, long value) {
        try {
            handle.setLong(instance, value);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public long get(Object instance) {
        try {
            return handle.getLong(instance);
        } catch (Exception e) {
            handleException(e);
            return -1;
        }
    }

    public static LongField of(java.lang.reflect.Field handle) {
        return new LongField(handle);
    }
}
