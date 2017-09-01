package xyz.upperlevel.uppercore.util.nms.refl.field;

import static xyz.upperlevel.uppercore.util.nms.NmsUtil.handleException;

public class ShortField {
    private final java.lang.reflect.Field handle;

    public ShortField(java.lang.reflect.Field handle) {
        this.handle = handle;
    }

    public void set(Object instance, short value) {
        try {
            handle.setShort(instance, value);
        } catch (Exception e) {
            handleException(e);
        }
    }
    
    public short get(Object instance) {
        try {
            return handle.getShort(instance);
        } catch (Exception e) {
            handleException(e);
            return -1;
        }
    }

    public static ShortField of(java.lang.reflect.Field handle) {
        return new ShortField(handle);
    }
}
