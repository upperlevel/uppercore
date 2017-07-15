package xyz.upperlevel.uppercore.gui.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import xyz.upperlevel.uppercore.gui.Gui;

public class GuiChangeEvent extends Event implements Listener {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    @Setter
    private boolean cancelled = false;
    @Getter
    private final Player player;
    @Getter
    private final Gui gui;
    @Getter
    private final Gui oldGui;

    public GuiChangeEvent(Player player, Gui gui, Gui oldGui) {
        this.player = player;
        this.gui = gui;
        this.oldGui = oldGui;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
