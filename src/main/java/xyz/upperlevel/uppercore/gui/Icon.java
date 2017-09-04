package xyz.upperlevel.uppercore.gui;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Data;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.uppercore.gui.link.Link;

@Data
public class Icon {
    private final ItemStack display;
    private final Link click;

    public static Icon of(ItemStack item, Link click) {
        return new Icon(item, click);
    }
}
