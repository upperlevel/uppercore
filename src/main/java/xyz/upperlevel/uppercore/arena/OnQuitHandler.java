package xyz.upperlevel.uppercore.arena;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.util.Dbg;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface OnQuitHandler {
    void handle(Player player);

    class Local implements OnQuitHandler {
        public static Location hub;

        @Override
        public void handle(Player player) {
            Dbg.pf("Teleporting %s to hub location", player.getName());
            player.teleport(hub);
        }

        public static void loadConfig(Config cfg) {
            hub = cfg.getLocation("hub-location");
        }
    }

    class Bungee implements OnQuitHandler {
        public static String hubServerName;

        private final Plugin plugin;

        public Bungee() {
            this.plugin = Uppercore.getPlugin();
            plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord"); // If wasn't already registered.
        }

        @Override
        public void handle(Player player) {
            try {
                Dbg.pf("Connecting %s back to the hub server: %s", player.getName(), hubServerName);

                ByteArrayOutputStream handle = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(handle);

                out.writeUTF("Connect");
                out.writeUTF(hubServerName);

                Dbg.pf("Sending payload of: %d bytes", handle.toByteArray().length);
                player.sendPluginMessage(plugin, "BungeeCord", handle.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void loadConfig(Config cfg) {
            hubServerName = cfg.getString("hub-server");
        }
    }
}
