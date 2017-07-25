package xyz.upperlevel.uppercore.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.link.Link;

import static xyz.upperlevel.uppercore.Uppercore.guis;

public interface Gui extends Link {

    /**
     * Called when a player clicks on the inventory
     * The event is cancelled before the call so if someone for some reason wants to re-enable it (idk why) he/she could
     * @param event the click event
     */
    void onClick(InventoryClickEvent event);

    /**
     * Shows the gui to the player that views it
     * @param player the player that is viewing it
     */
    void show(Player player);

    /**
     * Called when a player opens this gui
     * @param player the player that is opening the gui
     */
    void onOpen(Player player);

    /**
     * Called when a player closes this gui (either by force or normally)
     * @param player the player that closed the door
     */
    void onClose(Player player);


    /**
     * The action that this does when called as an action (from links)
     * @param player the player that executes this link
     */
    @Override
    default void run(Player player) {
        guis().open(player, this);
    }
}
