package xyz.upperlevel.uppercore.game.arena;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import xyz.upperlevel.uppercore.game.arena.events.ArenaJoinEvent;
import xyz.upperlevel.uppercore.game.arena.events.ArenaQuitEvent;

import java.util.logging.Level;

public abstract class LobbyPhase implements Phase {
    @Getter
    private final Arena arena;

    public LobbyPhase(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void onEnable(Phase previous) {
    }

    @Override
    public void onDisable(Phase next) {
    }

    public void checkCountdownStart() {
        // Todo: start timer if needed
    }

    public void checkCountdownStop() {
        // Todo: stop timer if needed
    }

    @EventHandler
    public void onJoin(ArenaJoinEvent e) {
        if (arena.getPlayersCount() == arena.getMaxPlayers()) {
            e.setCancelled(true);
            Bukkit.getLogger().log(Level.INFO, e.getPlayer().getName() + " attempted to join but there are too many players");
            return;
        }
        checkCountdownStart();
    }

    @EventHandler
    public void onQuit(ArenaQuitEvent e) {
        checkCountdownStop();
    }
}
