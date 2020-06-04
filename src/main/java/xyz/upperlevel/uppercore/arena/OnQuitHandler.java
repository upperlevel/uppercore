package xyz.upperlevel.uppercore.arena;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.Config;

public interface OnQuitHandler {
    void handle(Player player);

    class Local implements OnQuitHandler {
        public static Location hub;

        @Override
        public void handle(Player player) {
            player.teleport(hub);
        }

        public static void loadConfig(Config cfg) {
            hub = cfg.getLocation("hub-location");
        }
    }

    class Bungee implements OnQuitHandler {
        public static String serverName;

        private final Plugin plugin;

        public Bungee() {
            this.plugin = Uppercore.getPlugin();
            plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        }

        @Override
        public void handle(Player player) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(player.getName());
            out.writeUTF(serverName);

            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        }

        public static void loadConfig(Config cfg) {
            serverName = cfg.getString("hub-server");
        }
    }
}
