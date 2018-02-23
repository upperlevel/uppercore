package xyz.upperlevel.uppercore.game.mode;

import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.game.arena.Arena;
import xyz.upperlevel.uppercore.game.arena.ArenaManager;

public class BungeeMode implements GameMode, Listener {
    @Getter
    private Arena arena;

    public BungeeMode(ArenaManager arenaManager, Config settings) {
        arena = arenaManager.getArena(settings.getString("arena"));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        arena.join(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        arena.quit(e.getPlayer());
    }
}
