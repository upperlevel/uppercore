package xyz.upperlevel.uppercore.game.arena.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.game.arena.Arena;

public class ArenaQuitEvent extends ArenaEvent {
    @Getter
    private final Player player;

    public ArenaQuitEvent(Arena arena, Player player) {
        super(arena);
        this.player = player;
    }
}
