package xyz.upperlevel.uppercore.nms.impl;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.nms.NmsPacket;
import xyz.upperlevel.uppercore.nms.NmsVersion;
import xyz.upperlevel.uppercore.nms.exceptions.UnsupportedVersionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static xyz.upperlevel.uppercore.nms.NmsUtil.handleException;
import static xyz.upperlevel.uppercore.nms.impl.entity.PlayerNms.sendPacket;

public class MessageNms {
    //-----------------json messages
    private static final Method getNmsComponent;
    private static final Constructor<?> packetPlayOutChatConstructor;

    private static final Field packetPlayOutChatComponents;
    //-----------------actionbar messages
    private static final Constructor<?> packetPlayOutChatConstructorType;
    private static final Object[] chatMessageTypes;
    private static final byte MSG_CHAT = 0;
    private static final byte MSG_SYSTEM = 1;
    private static final byte MSG_GAME_INFO = 2;


    static {
        try {
            Class<?> chatSerialzer;

            if (NmsVersion.MINOR > 8 || (NmsVersion.MINOR == 8 && NmsVersion.RELEASE != 1)) {
                chatSerialzer = NmsPacket.NMS.getClass("IChatBaseComponent$ChatSerializer");
            } else {
                chatSerialzer = NmsPacket.NMS.getClass("ChatSerializer");
            }

            getNmsComponent = chatSerialzer.getMethod("a", String.class);
            Class<?> packetChatClazz = NmsPacket.NMS.getClass("PacketPlayOutChat");
            Class<?> iChatBaseComponentClass = NmsPacket.NMS.getClass("IChatBaseComponent");
            packetPlayOutChatConstructor = packetChatClazz.getConstructor(iChatBaseComponentClass);
            packetPlayOutChatComponents = packetChatClazz.getField("components");

            if (NmsVersion.MINOR >= 12) {
                Class<?> chatMessageType = NmsPacket.NMS.getClass("ChatMessageType");
                chatMessageTypes = chatMessageType.getEnumConstants();
                packetPlayOutChatConstructorType = packetChatClazz.getConstructor(iChatBaseComponentClass, chatMessageType);
            } else {
                chatMessageTypes = null;
                packetPlayOutChatConstructorType = packetChatClazz.getConstructor(iChatBaseComponentClass, Byte.TYPE);
            }
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }

    /**
     * Sends raw json to Player.
     *
     * @param player the receiver
     * @param json raw json to send
     */
    public static void sendJson(Player player, String json) {
        sendPacket(player, jsonPacket(json));
    }

    /**
     * Sends raw json to Player.
     *
     * @param player the receiver
     * @param json json to send
     */
    public static void sendJson(Player player, BaseComponent... json) {
        sendPacket(player, jsonPacket(json));
    }

    /**
     * Creates PlayOutChat packet from raw json.
     *
     * @param json raw json to wrap
     */
    public static Object jsonPacket(String json) {
        /*
        IChatBaseComponent msg = ChatSerializer.a(JSON);
        PacketPlayOutChat packet = new PacketPlayOutChat(msg);
        return packet;
         */
        try {
            Object msg = getNmsComponent.invoke(null, json);
            return packetPlayOutChatConstructor.newInstance(msg);
        }  catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            handleException(e);
            return null;
        }
    }

    /**
     * Creates PlayOutChat packet from components.
     *
     * @param components components to include
     */
    public static Object jsonPacket(BaseComponent... components) {
        /*
        PacketPlayOutChat packet = new PacketPlayOutChat(null);
        packet.components = components;
        return packet;
         */
        try {
            Object packet = packetPlayOutChatConstructor.newInstance((Object) null);
            packetPlayOutChatComponents.set(packet, components);
            return packet;
        }  catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            handleException(e);
            return null;
        }
    }

    public static Object actionBarPacket(Object ichatComponent) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return packetPlayOutChatConstructorType.newInstance(ichatComponent, NmsVersion.MINOR >= 12 ? chatMessageTypes[MSG_GAME_INFO] : MSG_GAME_INFO);
    }

    /**
     * Creates a Action bar packet from raw json.
     *
     * @param json the raw json
     * @return the action bar packet
     */
    public static Object actionBarPacket(String json) {
        /*
        IChatBaseComponent msg = ChatSerializer.a(JSON);
        PacketPlayOutChat packet = new PacketPlayOutChat(msg, GAME_INFO);
        return packet;
         */
        try {
            Object msg = getNmsComponent.invoke(null, json);
            return actionBarPacket(msg);
        }  catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            handleException(e);
            return null;
        }
    }

    /**
     * Creates a Action bar packet from json components.
     *
     * @param actionMessage the components to include
     * @return the action bar packet
     */
    public static Object actionBarPacket(BaseComponent... actionMessage) {
        /*
        PacketPlayOutChat packet = new PacketPlayOutChat(null, GAME_INFO);
        packet.components = components;
        return packet;
         */
        try {
            Object packet = actionBarPacket((Object)null);
            packetPlayOutChatComponents.set(packet, actionMessage);
            return packet;
        }  catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            handleException(e);
            return null;
        }
    }
}
