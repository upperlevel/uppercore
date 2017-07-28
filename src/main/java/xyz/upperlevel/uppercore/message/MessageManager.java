package xyz.upperlevel.uppercore.message;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MessageManager {
    private final Config config;

    public Message get(String path) {
        Object raw = config.getRequired(path);
        if(raw instanceof Collection) {
            return new Message(((Collection<?>) raw)
                    .stream()
                    .map(o -> PlaceholderValue.stringValue(o.toString()))
                    .collect(Collectors.toList()));
        } else return new Message(Collections.singletonList(PlaceholderValue.stringValue(raw.toString())));
    }

    public MessageManager getSection(String path) {
        return new MessageManager(config.getConfigRequired(path));
    }

    public static MessageManager load(Config config) {
        return new MessageManager(config);
    }

    public static MessageManager load(File file) {
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
