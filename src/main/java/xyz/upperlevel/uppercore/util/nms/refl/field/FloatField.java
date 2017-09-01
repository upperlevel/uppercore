package xyz.upperlevel.uppercore.util.nms.refl.field;

import static xyz.upperlevel.uppercore.util.nms.NmsUtil.handleException;

public class FloatField {
    private final java.lang.reflect.Field handle;

    public FloatField(java.lang.reflect.Field handle) {
        this.handle = handle;
    }

    public void set(Object instance, float value) {
        try {
            handle.setFloat(instance, value);
        } catch (Exception e) {
            handleException(e);
        }
    }
    
    public float get(Object instance) {
        try {
            return handle.getFloat(instance);
        } catch (Exception e) {
            handleException(e);
            return -1;
        }
    }

    public static FloatField of(java.lang.reflect.Field handle) {
        return new FloatField(handle);
    }
}
