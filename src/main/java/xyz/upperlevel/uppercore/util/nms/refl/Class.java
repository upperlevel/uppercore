package xyz.upperlevel.uppercore.util.nms.refl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.uppercore.util.nms.NmsPacket;
import xyz.upperlevel.uppercore.util.nms.exceptions.UnsupportedVersionException;
import xyz.upperlevel.uppercore.util.nms.refl.field.*;

import static xyz.upperlevel.uppercore.util.nms.Nms.handleException;

@RequiredArgsConstructor
public class Class {
    @Getter
    private final java.lang.Class<?> handle;

    public java.lang.reflect.Field getField0(String path, boolean accessible) {
        try {
            if (accessible) {
                return handle.getField(path);
            } else {
                java.lang.reflect.Field res = handle.getDeclaredField(path);
                res.setAccessible(true);
                return res;
            }
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Field<T> getField(String path, boolean accessible) {
        return Field.of(getField0(path, accessible));
    }

    @SuppressWarnings("unchecked")
    public <T> Field<T> getField(String path) {
        return Field.of(getField0(path, true));
    }

    public BoolField getBoolField(String path, boolean accessible) {
        return BoolField.of(getField0(path, accessible));
    }

    public BoolField getBoolField(String path) {
        return BoolField.of(getField0(path, true));
    }

    public ByteField getByteField(String path, boolean accessible) {
        return ByteField.of(getField0(path, accessible));
    }

    public ByteField getByteField(String path) {
        return ByteField.of(getField0(path, true));
    }

    public CharField getCharField(String path, boolean accessible) {
        return CharField.of(getField0(path, accessible));
    }

    public CharField getCharField(String path) {
        return CharField.of(getField0(path, true));
    }

    public DoubleField getDoubleField(String path, boolean accessible) {
        return DoubleField.of(getField0(path, accessible));
    }

    public DoubleField getDoubleField(String path) {
        return DoubleField.of(getField0(path, true));
    }

    public FloatField getFloatField(String path, boolean accessible) {
        return FloatField.of(getField0(path, accessible));
    }

    public FloatField getFloatField(String path) {
        return FloatField.of(getField0(path, true));
    }

    public IntField getIntField(String path, boolean accessible) {
        return IntField.of(getField0(path, accessible));
    }

    public IntField getIntField(String path) {
        return IntField.of(getField0(path, true));
    }

    public LongField getLongField(String path, boolean accessible) {
        return LongField.of(getField0(path, accessible));
    }

    public LongField getLongField(String path) {
        return LongField.of(getField0(path, true));
    }

    public ShortField getShortField(String path, boolean accessible) {
        return ShortField.of(getField0(path, accessible));
    }

    public ShortField getShortField(String path) {
        return ShortField.of(getField0(path, true));
    }

    public static Class of(java.lang.Class clazz) {
        return new Class(clazz);
    }

    public static Class of(NmsPacket packet, String name) {
        try {
            return new Class(packet.getClass(name));
        } catch (ClassNotFoundException e) {
            throw new UnsupportedVersionException(e);
        }
    }
}
