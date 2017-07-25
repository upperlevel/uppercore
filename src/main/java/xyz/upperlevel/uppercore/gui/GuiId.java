package xyz.upperlevel.uppercore.gui;

import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Registrable;

public class RegistrableGui extends Registrable<Gui> {
    
    public RegistrableGui(Plugin plugin, String id, Gui handle) {
        super(plugin, id, handle);
    }
}
