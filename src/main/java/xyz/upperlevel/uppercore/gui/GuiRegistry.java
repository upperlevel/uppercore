package xyz.upperlevel.uppercore.gui;

import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Registry;

import java.io.File;

import static xyz.upperlevel.uppercore.Uppercore.guis;

public class GuiRegistry extends Registry<GuiId> {
    public GuiRegistry(Plugin plugin) {
        super(plugin, "guis");
        guis().register(this);
    }

    @Override
    public void register(GuiId id) {
        super.register(id);
        guis().register(id);
    }

    @Override
    public GuiId unregister(String id) {
        GuiId result = super.unregister(id);
        if (result != null)
            guis().unregister(result);
        return result;
    }

    public void load(File file) {
        load(file, (plugin, id, config) -> new GuiId(plugin, id, new ChestGui(plugin, config)));
    }
}