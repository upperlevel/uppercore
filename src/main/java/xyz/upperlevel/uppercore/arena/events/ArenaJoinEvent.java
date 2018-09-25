package xyz.upperlevel.uppercore.arena.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import xyz.upperlevel.uppercore.arena.Arena;

public class ArenaJoinEvent extends ArenaEvent implements Cancellable {
    @Getter
    private final Player player;

    @Getter
    @Setter
    private boolean cancelled;

    public ArenaJoinEvent(Arena arena, Player player) {
        super(arena);
        this.player = player;
    }
}
