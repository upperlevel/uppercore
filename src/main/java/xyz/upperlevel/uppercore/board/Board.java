package xyz.upperlevel.uppercore.board;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.InvalidConfigurationException;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lombok.AccessLevel.NONE;

@Data
public class Board {
    private Title title;
    private final TextArea area = new TextArea();
    private int updateInterval;
    private final PlaceholderRegistry placeholders = PlaceholderRegistry.create();

    public Board() {
    }

    @SuppressWarnings("unchecked")
    public Board(Config config) {
        Object title = config.get("title");
        if (title instanceof String)
            this.title = new Title(PlaceholderUtil.process((String) title));
        else if (title instanceof Map)
            this.title = new Title(Config.wrap((Map<String, Object>) title));

        this.updateInterval = config.getInt("update-interval", -1);

        List<Object> lines = config.getList("lines");
        for (Object line : lines) {
            if (line instanceof String)
                area.addLine(new Line(PlaceholderUtil.process((String) line)));
            else if (line instanceof Map)
                area.addLine(new Line(Config.wrap((Map<String, Object>) line)));
        }
    }

    public List<String> render(Player player) {
        List<String> result = new ArrayList<>();
        Area current = area;
        do {
            result.addAll(current.render(player));
            current = area.getNext();
        } while (current != null);
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

    // TITLE
    @Data
    public class Title {
        private PlaceholderValue<String> text;
        private int updateInterval;
        private final PlaceholderRegistry placeholders = PlaceholderRegistry.create(Board.this.placeholders);

        public Title() {
        }

        public Title(PlaceholderValue<String> text) {
            this.text = text;
        }

        public Title(Config config) {
            this.text = config.getMessage("text");
            this.updateInterval = config.getInt("update-interval");
        }

        public String render(Player player) {
            return text.resolve(player, placeholders);
        }
    }

    // LINE
    public class Line {
        private PlaceholderValue<String> text;
        private int updateInterval;
        private final PlaceholderRegistry placeholders = PlaceholderRegistry.create(Board.this.placeholders);

        public Line() {
        }

        public Line(PlaceholderValue<String> text) {
            this.text = text;
        }

        public Line(Config config) {
            this.text = config.getMessage("text");
            this.updateInterval = config.getInt("update-interval");
        }

        public boolean isEmpty() {
            return text == null;
        }

        public String render(Player player) {
            return text.resolve(player, placeholders);
        }
    }

    // AREA
    @Data
    public abstract class Area {
        @Setter(NONE)
        private Area next;
        private int updateInterval;
        private final PlaceholderRegistry placeholders = PlaceholderRegistry.create(Board.this.placeholders);

        public Area append(Area area) {
            next = area;
            return next;
        }

        public abstract void update();

        public abstract List<String> render(Player player);
    }

    // TEXT AREA
    public class TextArea extends Area {
        private final List<Line> lines = new ArrayList<>();

        public void addLine(Line line) {
            lines.add(line);
        }

        @Override
        public void update() {
        }

        @Override
        public List<String> render(Player player) {
            return lines.stream()
                    .filter(Line::isEmpty)
                    .map(line -> line.render(player))
                    .collect(Collectors.toList());
        }
    }
}
