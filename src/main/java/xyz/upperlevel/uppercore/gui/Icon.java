package xyz.upperlevel.uppercore.gui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.uppercore.gui.link.Link;

@RequiredArgsConstructor
public class Icon {
    @Getter
    private final ItemStack display;
    @Getter
    private final Link click;

    public static Icon of(ItemStack item, Link click) {
        return new Icon(item, click);
    }
}
