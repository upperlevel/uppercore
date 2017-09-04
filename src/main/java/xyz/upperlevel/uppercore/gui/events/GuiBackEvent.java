package xyz.upperlevel.uppercore.gui.events;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.upperlevel.uppercore.gui.Gui;

@Getter
public class GuiBackEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Gui gui;
    private final Gui oldGui;

    @Setter
    private boolean cancelled = false;

    public GuiBackEvent(Player player, Gui gui, Gui oldGui) {
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
