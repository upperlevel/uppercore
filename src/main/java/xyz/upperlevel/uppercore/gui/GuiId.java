package xyz.upperlevel.uppercore.gui;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Identifier;

public class GuiId extends Identifier<Gui> {
    public GuiId(Plugin plugin, String id, Gui handle) {
        super(plugin, id, handle);
    }
}
