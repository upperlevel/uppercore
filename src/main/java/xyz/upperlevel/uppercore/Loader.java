package xyz.upperlevel.uppercore;

import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.config.Config;

public interface Loader<T> {

    T load(Plugin plugin, String id, Config config);
}
