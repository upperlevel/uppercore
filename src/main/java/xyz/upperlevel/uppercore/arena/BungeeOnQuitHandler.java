package xyz.upperlevel.uppercore.arena;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BungeeOnQuitHandler implements OnQuitHandler {
    @Getter
    private final Plugin plugin;

    @Getter
    @Setter
    private String serverName;

    public BungeeOnQuitHandler(Plugin plugin, String serverName) {
        this.serverName = serverName;
        this.plugin = plugin;

        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }

    @Override
    public void handle(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(player.getName());
        out.writeUTF(serverName);

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
}
