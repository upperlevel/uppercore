package xyz.upperlevel.uppercore.nms.impl;

import xyz.upperlevel.uppercore.nms.NmsPacket;
import xyz.upperlevel.uppercore.nms.exceptions.UnsupportedVersionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static xyz.upperlevel.uppercore.nms.NmsUtil.handleException;

public final class TagNms {
    public static final Class<?> CLASS;
    private static final Constructor<?> constructor;
    private static final Method set;
    private static final Method setByte;
    private static final Method setShort;
    private static final Method setInt;
    private static final Method setLong;

    static {
        try {
            CLASS = NmsPacket.NMS.getClass("NBTTagCompound");
            constructor = CLASS.getConstructor();

            set = CLASS.getDeclaredMethod("set", String.class, NmsPacket.NMS.getClass("NBTBase"));
            setByte = CLASS.getDeclaredMethod("setByte", String.class, byte.class);
            setShort = CLASS.getDeclaredMethod("setShort", String.class, short.class);
            setInt = CLASS.getDeclaredMethod("setShort", String.class, int.class);
            setLong = CLASS.getDeclaredMethod("setShort", String.class, long.class);
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }

    public static void set(Object tag, String name, Object tagBase) {
        try {
            set.invoke(tag, name, tagBase);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public static void setBoolean(Object tag, String name, boolean value) {
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

    public static void setShort(Object tag, String name, short value) {
        try {
            setShort.invoke(tag, name, value);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public static void setInt(Object tag, String name, int value) {
        try {
            setInt.invoke(tag, name, value);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public static void setLong(Object tag, String name, long value) {
        try {
            setLong.invoke(tag, name, value);
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
