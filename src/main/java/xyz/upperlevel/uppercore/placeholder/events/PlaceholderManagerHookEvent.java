package xyz.upperlevel.uppercore.placeholder.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.upperlevel.uppercore.placeholder.PlaceholderManager;

public class PlaceholderManagerHookEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    @Setter
    private PlaceholderManager placeholderManager = null;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
