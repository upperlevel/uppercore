package xyz.upperlevel.uppercore.placeholder.message;

import lombok.Getter;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.util.CollectionUtil;

import java.io.File;
import java.util.Map;

import static com.google.common.collect.Maps.immutableEntry;

public class MessageManager {
    @Getter
    private final String path;
    @Getter
    private final Config config;

    public MessageManager(String path, Config config) {
        this.path = path;
        this.config = config;
    }

    public MessageManager(Config config) {
        this("", config);
    }

    public Message get(String path) {
        return config.get(path, Message.class, null);
    }

    public MessageManager getSection(String path) {
        return new MessageManager(getPath(path), config.getConfigRequired(path));
    }

    private String getPath(String other) {
        return path.isEmpty() ? other : path + '.' + other;
    }

    public Map<String, Message> load(String key) {
        Map<String, Object> msg = config.getMap(key);
        if (msg != null) {
            return msg.entrySet()
                    .stream()
                    .map(e -> immutableEntry(e.getKey(), get(key + "." + e.getKey())))
                    .collect(CollectionUtil.toMap());
        } else
            return null;
    }

    public static MessageManager load(Config config) {
        return new MessageManager(config);
    }

    public static MessageManager load(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("Cannot find file " + file);
        }
        return new MessageManager(Config.fromYaml(file));
    }

    public static MessageManager load(Plugin plugin, String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
        return load(file);
    }

    public static MessageManager load(Plugin plugin) {
        return load(plugin, "messages.yml");
    }
}
