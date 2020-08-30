package xyz.upperlevel.uppercore.arena;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.util.Dbg;
import xyz.upperlevel.uppercore.util.LocUtil;

import java.io.*;

public interface OnQuitHandler {
    void handle(Player player);

    class Local implements OnQuitHandler {
        private static File hubFile;
        private static Location hub;

        @Override
        public void handle(Player player) {
            Dbg.pf("Teleporting %s to hub location", player.getName());
            player.teleport(hub);
        }

        public static void setHub(Location hub) {
            Local.hub = hub;
            try {
                new Yaml().dump(Local.hub, new FileWriter(hubFile));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        public static void loadConfig() {
            hubFile = new File(Uppercore.getPlugin().getDataFolder(), "hub.yml");
            hub = LocUtil.deserialize(Config.fromYaml(hubFile));
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
