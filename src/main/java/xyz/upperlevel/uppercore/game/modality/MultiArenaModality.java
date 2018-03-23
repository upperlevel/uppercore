package xyz.upperlevel.uppercore.game.modality;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.game.arena.ArenaManager;

public class MultiArenaModality implements Modality, Listener {
    public MultiArenaModality() {
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

    public static class Factory implements ModalityFactory {
        @Override
        public String getId() {
            return "multi-arena";
        }

        @Override
        public MultiArenaModality load(ArenaManager arenaManager, Config config) {
            return null;
        }
    }

    public static Factory factory() {
        return new Factory();
    }
}
