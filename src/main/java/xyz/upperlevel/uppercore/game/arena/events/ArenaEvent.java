package xyz.upperlevel.uppercore.game.arena.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.upperlevel.uppercore.game.arena.Arena;

public class ArenaEvent extends Event {
    @Getter
    private final Arena arena;

    public ArenaEvent(Arena arena) {
        this.arena = arena;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }
}
