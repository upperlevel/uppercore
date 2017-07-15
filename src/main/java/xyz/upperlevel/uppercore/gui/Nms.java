package xyz.upperlevel.uppercore.gui;

import net.wesjd.anvilgui.version.impl.FallbackWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Nms {
    private static final String version;

    private static final Method getNmsComponent;
    private static final Constructor packetPlayOutChatConstructor;
    private static final Method craftPlayerGetHandle;
    private static final Field craftPlayerPlayerConnection;
    private static final Method playerConnectionSendPacket;


    static {
        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        try {
            getNmsComponent = getNMSClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class);
            final Class<?> packetChatClazz = getNMSClass("PacketPlayOutChat");
            packetPlayOutChatConstructor = packetChatClazz.getConstructor(getNMSClass("IChatBaseComponent"));
            final Class<?> craftPlayerClazz = getCraftClass("entity.CraftPlayer");
            craftPlayerGetHandle = craftPlayerClazz.getMethod("getHandle");
            craftPlayerPlayerConnection = getNMSClass("EntityPlayer").getField("playerConnection");
            playerConnectionSendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));

        } catch (NoSuchMethodException | NoSuchFieldException e) {
            throw new UnsupportedVersionException(version, e);
        }
    }

    public static void sendJson(Player player, String json) {
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

    public static void sendPacket(Player player, Object packet) {
        /*
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        nmsPlayer.playerConnection.sendPacket(packet);
         */
        try {
            Object rawPlayer = craftPlayerGetHandle.invoke(player);
            playerConnectionSendPacket.invoke(craftPlayerPlayerConnection.get(rawPlayer), packet);
        } catch (IllegalAccessException | InvocationTargetException e) {
            handleException(e);
        }
    }



    protected static void handleException(Exception e) {
        throw new FallbackWrapper.UnsupportedVersionException(version, e);
    }


    private static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Class<?> getCraftClass(String path) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + "." + path);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class UnsupportedVersionException extends RuntimeException {
        private final String version;

        public UnsupportedVersionException(String version, Exception e) {
            super("Unsupported version \"" + version + "\", report this to the developers", e);
            this.version = version;
        }

        public String getVersion() {
            return version;
        }
    }


    private Nms(){}
}
