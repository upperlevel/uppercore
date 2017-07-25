package xyz.upperlevel.uppercore.board;

import lombok.Data;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.InvalidConfigurationException;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.PlaceholderSession;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.lang.reflect.Array;
import java.util.*;

import static xyz.upperlevel.uppercore.board.BoardUtil.MAX_LINES;

@Data
public class Board {
    private Title title;
    private final List<Zone> zones = new ArrayList<>();
    private int updateInterval;

    private final PlaceholderSession placeholders = new PlaceholderSession();

    public Board() {
    }

    @SuppressWarnings("unchecked")
    public Board(Config config) {
        Object title = config.get("title");
        if (title instanceof String)
            this.title = new Title(PlaceholderUtil.process((String) title));
        else if (title instanceof Map)
            this.title = Title.deserialize(Config.wrap((Map<String, Object>) title));

        this.updateInterval = config.getInt("update-interval", -1);

        List<Object> lines = config.getList("lines");
        for (Object line : lines) {
            if (line instanceof String) {
                addLine(new Line(PlaceholderUtil.process((String) line)));
            } else if (line instanceof Map) {
                Config sub = Config.wrap((Map) line);
                if (sub.has("position"))
                    setLine(sub.getIntRequired("position"), Line.deserialize(Config.wrap((Map<String, Object>) line)));
                else
                    addLine(new Line(Config.wrap((Map<String, Object>) line)));
            }
        }
    }

    public static Board deserialize(Config config) {
        try {
            return new Board(config);
        } catch (InvalidConfigurationException e) {
            e.addLocalizer("in board");
            throw e;
        }
    }

    // TITLE
    @Data
    public static class Title {
        private PlaceholderValue<String> text;
        private int updateInterval;
        private final PlaceholderSession placeholders = new PlaceholderSession();

        public Title() {
        }

        public Title(PlaceholderValue<String> text) {
            this.text = text;
        }

        public Title(Config config) {
            this.text = config.getMessage("text");
            this.updateInterval = config.getInt("update-interval");
        }

        public boolean isEmpty() {
            return text == null;
        }

        public static Title deserialize(Config config) {
            return new Title(config);
        }
    }

    @Data
    public static class Zone {

    }

    public static class Ranking extends Zone {
    }
}
