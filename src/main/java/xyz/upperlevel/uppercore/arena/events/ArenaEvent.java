package xyz.upperlevel.uppercore.arena.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.upperlevel.uppercore.arena.Arena;

public class ArenaEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final Arena arena;

    public ArenaEvent(Arena arena) {
        this.arena = arena;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
