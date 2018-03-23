package xyz.upperlevel.uppercore.game.arena;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.game.arena.events.ArenaJoinEvent;
import xyz.upperlevel.uppercore.game.arena.events.ArenaQuitEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Arena {
    @Getter
    private final String id;

    @Getter
    private String displayName;

    @Getter
    private String description = null;

    @Getter
    private PhaseManager phaseManager;

    private Map<UUID, Player> players = new HashMap<>();

    @Getter
    @Setter
    private Location lobby;

    @Getter
    private int minPlayers = 1;

    @Getter
    private int maxPlayers = -1;

    public Arena(String id) {
        this.id = id;
        this.displayName = id;

        this.phaseManager = new PhaseManager();
        phaseManager.next(getLobbyPhase());
    }

    public abstract LobbyPhase getLobbyPhase();

    public int getPlayersCount() {
        return players.size();
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

    public boolean join(Player player) {
        ArenaJoinEvent event = new ArenaJoinEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            players.put(player.getUniqueId(), player);
            return true;
        }
        return false;
    }

    public boolean quit(Player player) {
        if (players.remove(player.getUniqueId()) != null) {
            Bukkit.getPluginManager().callEvent(new ArenaQuitEvent(this, player));
        }
        return true;
    }
}
