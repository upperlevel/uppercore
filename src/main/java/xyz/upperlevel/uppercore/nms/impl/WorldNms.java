package xyz.upperlevel.uppercore.nms.impl;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import xyz.upperlevel.uppercore.nms.NmsPacket;
import xyz.upperlevel.uppercore.nms.exceptions.UnsupportedVersionException;
import xyz.upperlevel.uppercore.nms.impl.entity.EntityNms;

import java.lang.reflect.Method;

import static xyz.upperlevel.uppercore.nms.NmsUtil.handleException;

public final class WorldNms {
    public static final Class<?> NMS_CLASS;
    public static final Class<?> BUKKIT_CLASS;
    private static final Method getHandle;
    private static final Method broadcastEntityEffect;

    static {
        try {
            NMS_CLASS = NmsPacket.NMS.getClass("World");
            BUKKIT_CLASS = NmsPacket.CRAFT.getClass("CraftWorld");


            getHandle = BUKKIT_CLASS.getDeclaredMethod("getHandle");
            broadcastEntityEffect = NMS_CLASS.getMethod("broadcastEntityEffect", EntityNms.NMS_CLASS, Byte.TYPE);
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }

    public static Object getHandle(World world) {
        try {
            return getHandle.invoke(world);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public static void broadcastEntityEffect(Object worldNms, Object entityNms, byte type) {
        try {
            broadcastEntityEffect.invoke(worldNms, entityNms, type);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public static void broadcastEntityEffect(World world, Object entityNms, byte type) {
        try {
            broadcastEntityEffect.invoke(getHandle(world), entityNms, type);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public static void broadcastEntityEffect(World world, Entity entity, byte type) {
        try {
            broadcastEntityEffect.invoke(getHandle(world), EntityNms.getHandle(entity), type);
        } catch (Exception e) {
            handleException(e);
        }
    }

    private WorldNms(){}
}
