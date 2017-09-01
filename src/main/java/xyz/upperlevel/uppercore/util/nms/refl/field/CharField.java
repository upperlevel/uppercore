package xyz.upperlevel.uppercore.util.nms.refl.field;

import static xyz.upperlevel.uppercore.util.nms.NmsUtil.handleException;

public class CharField {
    private final java.lang.reflect.Field handle;

    public CharField(java.lang.reflect.Field handle) {
        this.handle = handle;
    }

    public void set(Object instance, char value) {
        try {
            handle.setChar(instance, value);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public char get(Object instance) {
        try {
            return handle.getChar(instance);
        } catch (Exception e) {
            handleException(e);
            return '\0';
        }
    }

    public static CharField of(java.lang.reflect.Field handle) {
        return new CharField(handle);
    }
}