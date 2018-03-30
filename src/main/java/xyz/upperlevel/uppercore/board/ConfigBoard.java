package xyz.upperlevel.uppercore.board;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link Board} that takes both title and lines from config.
 */
public class ConfigBoard implements Board {
    private PlaceholderValue<String> title;
    private PlaceholderValue<String>[] lines;

    @SuppressWarnings("unchecked")
    public ConfigBoard(Config config) {
        title = config.getMessageStrRequired("title");
        lines = config.getMessageStrList("lines", Collections.emptyList()).toArray(new PlaceholderValue[0]);
    }

    public ConfigBoard(PlaceholderValue<String> title, PlaceholderValue<String>[] lines) {
        this.title = title;
        this.lines = lines;
    }

    /**
     * Gets the title of the board based on the player and placeholders.
     *
     * @param holder              the player
     * @param placeholderRegistry the placeholders
     * @return the title
     */
    public String getTitle(Player holder, PlaceholderRegistry placeholderRegistry) {
        return title.resolve(holder, placeholderRegistry);
    }

    /**
     * Gets the lines of the board based on the player and placeholders.
     *
     * @param holder              the player
     * @param placeholderRegistry the placeholders
     * @return the title
     */
    public List<String> getLines(Player holder, PlaceholderRegistry placeholderRegistry) {
        int length = lines.length;
        String[] realLines = new String[length];
        for (int i = 0; i < length; i++) {
            realLines[i] = lines[i].resolve(holder, placeholderRegistry);
        }
        return Arrays.asList(realLines);
    }
}
