package xyz.upperlevel.uppercore.gui;

import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Identifier;

public class GuiId extends Identifier<Gui> {
    
    public GuiId(Plugin plugin, String id, Gui handle) {
        super(plugin, id, handle);
    }
}
