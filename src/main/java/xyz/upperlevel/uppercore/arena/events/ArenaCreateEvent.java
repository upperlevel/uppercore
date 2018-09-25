package xyz.upperlevel.uppercore.arena.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import xyz.upperlevel.uppercore.arena.Arena;

public class ArenaCreateEvent extends ArenaEvent implements Cancellable {
    @Getter
    @Setter
    private boolean cancelled;

    public ArenaCreateEvent(Arena arena) {
        super(arena);
    }
}
