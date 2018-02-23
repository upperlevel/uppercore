package xyz.upperlevel.uppercore.game.mode;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.upperlevel.uppercore.config.Config;

public class MultiArenaMode implements GameMode, Listener {
    public MultiArenaMode(Config settings) {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        // Todo: move the player to the lobby where he can choose what arena to join
        // Todo: or click on an auto-join sign
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        // Todo if the player is in-game remove the player from the arena
    }
}
