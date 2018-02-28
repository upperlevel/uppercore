package xyz.upperlevel.uppercore.util.nms.impl.entity;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.util.nms.NmsPacket;
import xyz.upperlevel.uppercore.util.nms.NmsVersion;
import xyz.upperlevel.uppercore.util.nms.exceptions.UnsupportedVersionException;
import xyz.upperlevel.uppercore.util.nms.impl.MessageNms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

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

    /**
     * Gets the NMS handle from the player.
     *
     * @param player the player
     * @return the player's NMS handle
     */
    public static Object getHandle(Player player) {
        try {
            return getHandle.invoke(player);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    /**
     * Gets the player's connection.
     *
     * @param player the player
     * @return the player's connection
     */
    public static Object getConnection(Player player) {
        try {
            return connection.get(getHandle.invoke(player));
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    /**
     * Sends a packet to the player
     *
     * @param player the player
     * @param packet the packet to send
     */
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

    /**
     * Sends an action bar to the player using {@link BaseComponent}s as content
     *
     * @param player the targeted player
     * @param action the content of the action bar
     */
    public static void sendActionBar(Player player, BaseComponent... action) {
        if (NmsVersion.MINOR > 10 || (NmsVersion.MINOR == 9 && NmsVersion.RELEASE >= 2)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, action);
        } else {
            sendPacket(player, MessageNms.actionBarPacket(action));
        }
    }

    /**
     * Sends an action bar to the player using plain text as context
     *
     * @param player the targeted player
     * @param textActions the plain-text content of the action bar
     */
    public static void sendActionBar(Player player, Collection<String> textActions) {
        sendPacket(player, MessageNms.actionBarPacket(toJson(textActions)));
    }

    /**
     * Translates plain-text lines into readable json
     * @param text plain-text lines
     * @return parsable json
     */
    private static String toJson(Collection<String> text) {
        return "{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', StringUtils.join(text, "\n")) + "\"}";
    }
}
