package xyz.upperlevel.uppercore.hotbar;

import lombok.Data;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Registry;

import java.io.File;

import static xyz.upperlevel.uppercore.Uppercore.hotbars;


@Data
public class HotbarRegistry extends Registry<HotbarId> {
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

    @Override
    protected void postLoad(File in, HotbarId out) {
        getLogger().info("Successfully loaded hotbar \"" + out.getId() + "\"");
    }
}