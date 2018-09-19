package xyz.upperlevel.uppercore.board;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleConfigBoard implements Board {
    @Getter
    private final PlaceholderValue<String> title;

    @Getter
    private final List<PlaceholderValue<String>> lines;

    @Getter
    private final int autoUpdateInterval;

    public SimpleConfigBoard(
            PlaceholderValue<String> title,
            List<PlaceholderValue<String>> lines,
            int autoUpdateInterval
    ) {
        this.title = title;
        this.lines = lines;
        this.autoUpdateInterval = autoUpdateInterval;
    }

    public SimpleConfigBoard(Config config) {
        this(
                config.getMessageStr("title"),
                config.getMessageStrList("lines"),
                config.getInt("update-interval", -1)
        );
    }

    @Override
    public String getTitle(Player player, PlaceholderRegistry placeholderRegistry) {
        return title.resolve(player, placeholderRegistry);
    }

    @Override
    public List<String> getLines(Player player, PlaceholderRegistry placeholderRegistry) {
        return lines.stream().map(line -> line.resolve(player, placeholderRegistry)).collect(Collectors.toList());
    }

    public static SimpleConfigBoard create(Config config) {
        return new SimpleConfigBoard(config);
    }

    public static SimpleConfigBoard create(File file) {
        return SimpleConfigBoard.create(Config.wrap(YamlConfiguration.loadConfiguration(file)));
    }
}
