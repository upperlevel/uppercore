package xyz.upperlevel.uppercore.gui;

import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Identifier;
import xyz.upperlevel.uppercore.Registry;

public class GuiRegistry extends Registry<Gui> {
    public GuiRegistry(Plugin plugin) {
        super(plugin, "guis");
        GuiSystem.register(plugin, this);
    }

    @Override
    public GuiRegistry register(String id, Gui gui) {
        super.register(id, gui);
        GuiSystem.register(getPlugin(), id, gui);
        return this;
    }

    @Override
    public Identifier<Gui> unregister(String id) {
        GuiSystem.unregister(getPlugin(), id);
        return super.unregister(id);
    }
}