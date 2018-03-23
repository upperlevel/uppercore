package xyz.upperlevel.uppercore.game.modality;

import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.game.arena.Arena;
import xyz.upperlevel.uppercore.game.arena.ArenaManager;

public class BungeeModality implements Modality, Listener {
    @Getter
    private Arena arena;

    public BungeeModality(ArenaManager arenaManager, Config config) {
        arena = arenaManager.get(config.getString("arena"));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        arena.join(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        arena.quit(e.getPlayer());
    }

    public static class Factory implements ModalityFactory {
        @Override
        public String getId() {
            return "bungee";
        }

        @Override
        public BungeeModality load(ArenaManager arenaManager, Config config) {
            return new BungeeModality(arenaManager, config);
        }
    }

    public static Factory factory() {
        return new Factory();
    }
}
