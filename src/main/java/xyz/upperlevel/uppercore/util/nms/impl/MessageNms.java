package xyz.upperlevel.uppercore.util.nms.impl;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.util.nms.NmsPacket;
import xyz.upperlevel.uppercore.util.nms.NmsVersion;
import xyz.upperlevel.uppercore.util.nms.exceptions.UnsupportedVersionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static xyz.upperlevel.uppercore.util.nms.NmsUtil.handleException;
import static xyz.upperlevel.uppercore.util.nms.impl.entity.PlayerNms.sendPacket;

public class MessageNms {
    //-----------------json messages
    private static Method getNmsComponent;
    private static Constructor packetPlayOutChatConstructor;
    private static Field packetPlayOutChatComponents;

    static {
        try {
            Class<?> chatSerialzer;

            if(NmsVersion.MINOR > 8 || (NmsVersion.MINOR == 8 && NmsVersion.RELEASE != 1))
                chatSerialzer = NmsPacket.NMS.getClass("IChatBaseComponent$ChatSerializer");
            else
                chatSerialzer = NmsPacket.NMS.getClass("ChatSerializer");

            getNmsComponent = chatSerialzer.getMethod("a", String.class);
            Class<?> packetChatClazz = NmsPacket.NMS.getClass("PacketPlayOutChat");
            packetPlayOutChatConstructor = packetChatClazz.getConstructor(NmsPacket.NMS.getClass("IChatBaseComponent"));
            packetPlayOutChatComponents = packetChatClazz.getField("components");
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }

    public static void sendJson(Player player, String json) {
        sendPacket(player, jsonPacket(json));
    }

    public static void sendJson(Player player, BaseComponent... json) {
        sendPacket(player, jsonPacket(json));
    }

    public static Object jsonPacket(String json) {
        /*
        IChatBaseComponent msg = ChatSerializer.a(JSON);
        PacketPlayOutChat packet = new PacketPlayOutChat(msg);
         */
        try {
            Object msg = getNmsComponent.invoke(null, json);
            return packetPlayOutChatConstructor.newInstance(msg);
        }  catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            handleException(e);
            return null;
        }
    }

    public static Object jsonPacket(BaseComponent[] json) {
        /*
        IChatBaseComponent msg = ChatSerializer.a(JSON);
        PacketPlayOutChat packet = new PacketPlayOutChat(msg);
         */
        try {
            Object packet = packetPlayOutChatConstructor.newInstance((Object) null);
            packetPlayOutChatComponents.set(packet, json);
            return packet;
        }  catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            handleException(e);
            return null;
        }
    }
}
