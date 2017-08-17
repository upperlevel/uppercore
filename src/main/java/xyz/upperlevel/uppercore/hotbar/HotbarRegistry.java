package xyz.upperlevel.uppercore.hotbar;

import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Loader;
import xyz.upperlevel.uppercore.Registry;

import java.io.File;

import static xyz.upperlevel.uppercore.Uppercore.hotbars;

public class HotbarRegistry extends Registry<HotbarId> {
    public static final Loader<HotbarId> LOADER = Loader.of(HotbarId::new);

    public HotbarRegistry(Plugin plugin) {
        super(plugin, "hotbars");
        hotbars().register(this);
    }

    @Override
    public void register(HotbarId id) {
        super.register(id);
        hotbars().register(id);
    }

    @Override
    public HotbarId unregister(String id) {
        HotbarId result = super.unregister(id);
        if (result != null)
            hotbars().unregister(result);
        return result;
    }

    public void loadFolder(File folder) {
        loadFolder(folder, LOADER);
    }

    @Override
    protected void postLoad(File in, HotbarId out) {
        getLogger().info("Successfully loaded hotbar \"" + out.getId() + "\"");
    }
}