package xyz.upperlevel.uppercore.hotbar;

import lombok.Data;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Registry;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.InvalidConfigurationException;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.gui.GuiId;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

import static xyz.upperlevel.uppercore.Uppercore.guis;
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
}