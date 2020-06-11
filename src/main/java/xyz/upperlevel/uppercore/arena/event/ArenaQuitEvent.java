package xyz.upperlevel.uppercore.arena.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.upperlevel.uppercore.arena.Arena;

public class ArenaQuitEvent extends Event implements Cancellable {
    private static HandlerList handlers = new HandlerList();

    @Getter
    @Setter
    private boolean cancelled;

    @Getter
    private Player player;

    @Getter
    private Arena arena;

    @Getter
    private ArenaQuitReason reason;

    public ArenaQuitEvent(Player player, Arena arena, ArenaQuitReason reason) {
        this.player = player;
        this.arena = arena;
        this.reason = reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum ArenaQuitReason {
        GAME_QUIT, COMMAND, ARENA_END, ARENA_ABORT,
    }
}
