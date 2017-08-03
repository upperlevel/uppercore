package xyz.upperlevel.uppercore.util.nms.impl;

import xyz.upperlevel.uppercore.util.nms.NmsPacket;
import xyz.upperlevel.uppercore.util.nms.exceptions.UnsupportedVersionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static xyz.upperlevel.uppercore.util.nms.Nms.handleException;

public final class TagNms {
    public static final Class<?> CLASS;
    private static final Constructor<?> constructor;
    private static final Method setByte;

    static {
        try {
            CLASS = NmsPacket.NMS.getClass("NBTTagCompound");
            constructor = CLASS.getConstructor();
            setByte = CLASS.getDeclaredMethod("setByte", String.class, byte.class);
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }

    public static void setBool(Object tag, String name, boolean value) {
        try {
            setByte.invoke(tag, name, (byte)(value ? 1 : 0));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public static void setByte(Object tag, String name, byte value) {
        try {
            setByte.invoke(tag, name, value);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public static void set(Object tag, String name, Object value) {
        try {
            setByte.invoke(tag, name, value);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public static Object create() {
        try {
            return constructor.newInstance();
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }
}
