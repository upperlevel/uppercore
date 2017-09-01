package xyz.upperlevel.uppercore.util.nms.refl.field;

import static xyz.upperlevel.uppercore.util.nms.NmsUtil.handleException;

public class ByteField {
    private final java.lang.reflect.Field handle;

    public ByteField(java.lang.reflect.Field handle) {
        this.handle = handle;
    }

    public void set(Object instance, byte value) {
        try {
            handle.setByte(instance, value);
        } catch (Exception e) {
            handleException(e);
        }
    }
    
    public byte get(Object instance) {
        try {
            return handle.getByte(instance);
        } catch (Exception e) {
            handleException(e);
            return -1;
        }
    }

    public static ByteField of(java.lang.reflect.Field handle) {
        return new ByteField(handle);
    }
}
