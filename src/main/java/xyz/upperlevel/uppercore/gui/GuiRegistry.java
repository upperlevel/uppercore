package xyz.upperlevel.uppercore.gui;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Loader;
import xyz.upperlevel.uppercore.Registry;

import java.io.File;

import static xyz.upperlevel.uppercore.Uppercore.guis;

public class GuiRegistry extends Registry<GuiId> {
    public static Loader<GuiId> LOADER = Loader.of((plugin, id, config) -> new GuiId(plugin, id, new ChestGui(plugin, config)));

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

    @Override
    protected void postLoad(File in, GuiId out) {
        getLogger().info("Successfully loaded gui \"" + out.getId() + "\"");
    }

    public GuiId loadFile(File file) {
        return loadFile(file, LOADER);
    }

    public void loadFolder(File file) {
        loadFolder(file, LOADER);
    }
}