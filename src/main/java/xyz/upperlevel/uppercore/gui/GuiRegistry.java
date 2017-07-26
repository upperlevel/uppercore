package xyz.upperlevel.uppercore.gui;

import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Loader;
import xyz.upperlevel.uppercore.Registry;

import java.io.File;

import static xyz.upperlevel.uppercore.Uppercore.guis;

public class GuiRegistry extends Registry<GuiId> {
    public static Loader<GuiId> LOADER = (plugin, id, config) -> new GuiId(plugin, id, new ChestGui(plugin, config));

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
        load(file, LOADER);
    }

    public void loadFile(File file) {
        loadFile(file, LOADER);
    }

    public void loadFolder(File file) {
        loadFolder(file, LOADER);
    }
}