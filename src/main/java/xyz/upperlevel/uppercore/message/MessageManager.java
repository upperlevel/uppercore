package xyz.upperlevel.uppercore.message;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigurationException;
import xyz.upperlevel.uppercore.config.exceptions.RequiredPropertyNotFoundException;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

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
        Object raw = config.get(path);
        if(raw == null)
            throw new IllegalMessageConfigException(this.path, path);
        if(raw instanceof Collection) {
            return new Message(
                    ((Collection<?>) raw)
                    .stream()
                    .map(o -> PlaceholderValue.stringValue(o.toString()))
                    .collect(Collectors.toList()));
        } else return new Message(Collections.singletonList(PlaceholderValue.stringValue(raw.toString())));
    }

    public MessageManager getSection(String path) {
        try {
            return new MessageManager(getPath(path), config.getConfigRequired(path));
        } catch (RequiredPropertyNotFoundException exception) {
            throw new IllegalMessageConfigException(getPath(path));
        } catch (InvalidConfigurationException e) {
            throw new IllegalMessageConfigException(getPath(path), e);
        }
    }

    private String getPath(String other) {
        return path.isEmpty() ? other : path + '.' + other;
    }

    public static MessageManager load(Config config) {
        return new MessageManager(config);
    }

    public static MessageManager load(File file) {
        if(!file.exists())
            throw new IllegalArgumentException("Cannot find file " + file);
        return new MessageManager(Config.wrap(YamlConfiguration.loadConfiguration(file)));
    }

    public static MessageManager load(Plugin plugin, String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if(!file.exists())
            plugin.saveResource(fileName, false);
        return load(file);
    }

    public static MessageManager load(Plugin plugin) {
        return load(plugin, "messages.yml");
    }
}
