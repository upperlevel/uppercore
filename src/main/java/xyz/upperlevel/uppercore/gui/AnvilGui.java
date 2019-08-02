package xyz.upperlevel.uppercore.gui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.upperlevel.uppercore.Uppercore;

@RequiredArgsConstructor
public class AnvilGui implements Gui {
    @Getter
    @Setter
    private String message;
    @Getter
    @Setter
    private AnvilGUI.ClickHandler listener = (player, input) -> "Not implemented!";

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(false);
    }

    @Override
    public void show(Player player) {
        new AnvilGUI(
                Uppercore.plugin(),
                player,
                message,
                this::onAnvilClick
        );
    }

    private String onAnvilClick(Player player, String input) {
        //We don't want the GUI to close:
        //Once we return null the AnvilGui will close the GUI and the GuiManager will listen that close event
        //all AFTER that the onClick is executed
        final String str = listener.onClick(player, input);
        return str == null ? "" : str;
    }

    @Override
    public void onOpen(Player player) {}

    @Override
    public void onClose(Player player) {}
}
