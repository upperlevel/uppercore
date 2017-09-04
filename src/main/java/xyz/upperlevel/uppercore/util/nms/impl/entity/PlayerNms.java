package xyz.upperlevel.uppercore.util.nms.impl.entity;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.util.nms.NmsPacket;
import xyz.upperlevel.uppercore.util.nms.NmsVersion;
import xyz.upperlevel.uppercore.util.nms.exceptions.UnsupportedVersionException;
import xyz.upperlevel.uppercore.util.nms.impl.MessageNms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static xyz.upperlevel.uppercore.util.nms.NmsUtil.handleException;

public class PlayerNms {
    public static final Class<?> CLASS;
    private static final Method getHandle;
    private static final Field connection;
    private static final Method connectionSendPacket;

    static {
        try {
            CLASS = NmsPacket.CRAFT_ENTITY.getClass("CraftPlayer");
            getHandle = CLASS.getMethod("getHandle");
            connection = NmsPacket.NMS.getClass("EntityPlayer").getField("playerConnection");
            connectionSendPacket = NmsPacket.NMS.getClass("PlayerConnection").getMethod("sendPacket", NmsPacket.NMS.getClass("Packet"));
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }

    public static Object getHandle(Player player) {
        try {
            return getHandle.invoke(player);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public static Object getConnection(Player player) {
        try {
            return connection.get(getHandle.invoke(player));
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public static void sendPacket(Player player, Object packet) {
        /*
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        nmsPlayer.playerConnection.sendPacket(packet);
         */
        try {
            Object rawPlayer = getHandle.invoke(player);
            connectionSendPacket.invoke(connection.get(rawPlayer), packet);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public static void sendActionBar(Player player, BaseComponent... action) {
        if(NmsVersion.MINOR > 10 || (NmsVersion.MINOR == 9 && NmsVersion.RELEASE >= 2)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, action);
        } else {
            sendPacket(player, MessageNms.actionBarPacket(action));
        }
    }
}
