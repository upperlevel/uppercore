package xyz.upperlevel.uppercore.arena.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Sign;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import xyz.upperlevel.uppercore.arena.Arena;

import java.util.Collection;

public class JoinSignUpdateEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    @Getter
    private final Arena arena;

    @Getter
    private final Collection<Sign> joinSigns;

    public JoinSignUpdateEvent(Arena arena, Collection<Sign> joinSigns) {
        this.arena = arena;
        this.joinSigns = joinSigns;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
