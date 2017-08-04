package xyz.upperlevel.uppercore.util.nms.impl.entity;

import org.bukkit.entity.Entity;
import xyz.upperlevel.uppercore.util.nms.NmsPacket;
import xyz.upperlevel.uppercore.util.nms.exceptions.UnsupportedVersionException;
import xyz.upperlevel.uppercore.util.nms.impl.TagNms;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import static xyz.upperlevel.uppercore.util.nms.Nms.handleException;

public final class EntityNms {
    public static final Class<?> NMS_CLASS;
    public static final Class<?> BUKKIT_CLASS;
    private static final Method getHandle;
    private static final Method getTag;
    private static final Method setTag;

    static {
        try {
            NMS_CLASS = NmsPacket.NMS.getClass("Entity");
            BUKKIT_CLASS = NmsPacket.CRAFT_ENTITY.getClass("CraftEntity");


            getHandle = BUKKIT_CLASS.getDeclaredMethod("getHandle");
            getTag = NMS_CLASS.getDeclaredMethod("c", TagNms.CLASS);
            setTag = NMS_CLASS.getDeclaredMethod("f", TagNms.CLASS);
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }

    public static Object getHandle(Entity entity) {
        try {
            return getHandle.invoke(entity);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public static Object getTag(Entity entity, Object tag) {
        try {
            return getTag.invoke(entity, tag);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public static void setTag(Entity entity, Object tag) {
        try {
            getTag.invoke(entity, tag);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public static void editTag(Entity entity, Consumer<Object> tagConsumer) {
        try {
            Object handle = EntityNms.getHandle(entity);
            Object compound = TagNms.create();
            getTag.invoke(handle, compound);
            tagConsumer.accept(compound);
            setTag.invoke(handle, compound);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public static void setNoAI(Entity entity, boolean value) {
        editTag(entity, tag -> TagNms.set(tag, "NoAI", value));
    }

    public static void setSilent(Entity entity, boolean value) {
        editTag(entity, tag -> TagNms.set(tag, "Silent", value));
    }

    public static void setInvulnerable(Entity entity, boolean value) {
        editTag(entity, tag -> TagNms.set(tag, "Invulnerable", value));
    }

    public static void freeze(Entity entity, boolean value) {
        editTag(entity, tag -> {
            TagNms.set(tag, "NoAI", value);
            TagNms.set(tag, "Silent", value);
            TagNms.set(tag, "Invulnerable", value);
        });
    }

    private EntityNms() {
    }
}
