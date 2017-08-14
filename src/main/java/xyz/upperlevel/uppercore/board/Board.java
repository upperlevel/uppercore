package xyz.upperlevel.uppercore.board;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigurationException;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class Board {
    private PlaceholderValue<String> title;
    private List<BoardSection> sections = new LinkedList<>();
    private int updateInterval;
    private PlaceholderRegistry placeholders = PlaceholderRegistry.create();

    /**
     * Init an empty board, with just the title.
     */
    public Board(String title) {
        this.title = PlaceholderValue.stringValue(title);
    }

    public Board(PlaceholderValue<String> title) {
        this.title = title;
    }

    /**
     * Loads the board from a configuration.
     */
    @SuppressWarnings("unchecked")
    public Board(Config config) {
        this.title = config.getMessageStr("title");
        this.updateInterval = config.getInt("update-interval", -1);
        if (config.has("lines"))
            sections.add(new FixBoardSection(config.getMessageStrList("lines")));
    }

    /**
     * Copy constructor.
     */
    public Board(Board other) {
        this.title = other.title;
        this.updateInterval = other.updateInterval;
        this.sections = other.sections;
        this.placeholders = other.placeholders;
    }

    public void setTitle(String title) {
        this.title = PlaceholderValue.stringValue(title);
    }

    /**
     * Appends a board section.
     */
    public void append(BoardSection section) {
        sections.add(section);
    }

    /**
     * Renders all the sections and obtains a list of strings identifying the lines.
     * @param player the player for which the board may be displayed
     */
    public List<String> render(Player player) {
        List<String> result = new ArrayList<>();
        sections.forEach(section -> result.addAll(section.render(player, placeholders)));
        return result;
    }

    public static Board deserialize(Config config) {
        try {
            return new Board(config);
        } catch (InvalidConfigurationException e) {
            e.addLocalizer("in board");
            throw e;
        }
    }

    public static Board.Builder builder(String title) {
        return new Board.Builder(new Board(title));
    }

    @RequiredArgsConstructor
    public static class Builder {
        private final Board handle;

        public Builder title(String title) {
            handle.setTitle(title);
            return this;
        }

        public Builder updateInterval(int updateInterval) {
            handle.setUpdateInterval(updateInterval);
            return this;
        }

        public Builder append(BoardSection section) {
            handle.append(section);
            return this;
        }

        public Board build() {
            return handle;
        }
    }
}
