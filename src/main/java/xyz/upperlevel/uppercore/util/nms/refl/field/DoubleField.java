package xyz.upperlevel.uppercore.util.nms.refl.field;

import static xyz.upperlevel.uppercore.util.nms.NmsUtil.handleException;

public class DoubleField {
    private final java.lang.reflect.Field handle;

    public DoubleField(java.lang.reflect.Field handle) {
        this.handle = handle;
    }

    public void set(Object instance, double value) {
        try {
            handle.setDouble(instance, value);
        } catch (Exception e) {
            handleException(e);
        }
    }
    
    public double get(Object instance) {
        try {
            return handle.getDouble(instance);
        } catch (Exception e) {
            handleException(e);
            return -1;
        }
    }

    public static DoubleField of(java.lang.reflect.Field handle) {
        return new DoubleField(handle);
    }
}
