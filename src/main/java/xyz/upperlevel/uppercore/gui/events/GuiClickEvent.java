package xyz.upperlevel.uppercore.gui.events;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.upperlevel.uppercore.gui.Gui;

@Getter
public class GuiClickEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final InventoryClickEvent click;
    private final Player player;
    private final Gui gui;

    @Setter
    private boolean cancelled;

    public GuiClickEvent(InventoryClickEvent click, Player player, Gui gui) {
        this.click = click;
        this.player = player;
        this.gui = gui;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
