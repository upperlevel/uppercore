package xyz.upperlevel.uppercore;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.config.Config;

public interface ConfigLoader<T> {
    T load(Plugin plugin, String id, Config config);

    default Loader<T> normalize() {
        return (p, i, f) -> load(p, i, Config.wrap(YamlConfiguration.loadConfiguration(f)));
    }
}
