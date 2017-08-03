package xyz.upperlevel.uppercore.board;

import lombok.Data;
import lombok.Getter;
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
    private final PlaceholderValue<String> title;
    private final List<Area> areas = new LinkedList<>();
    private int updateInterval;
    private PlaceholderRegistry placeholders = PlaceholderRegistry.create();

    public Board(PlaceholderValue<String> title) {
        this.title = title;
    }

    @SuppressWarnings("unchecked")
    public Board(Config config) {
        this.title = config.getMessageStr("title");
        this.updateInterval = config.getInt("update-interval", -1);
        if (config.has("lines")) {
            TextArea area = new TextArea();
            area.add(config.getMessageStrList("lines"));
            areas.add(area);
        }
    }

    public Board(Board other) {
        title = other.title;
        updateInterval = other.updateInterval;
        for(Area area : other.areas)
            areas.add(area.copy());
        placeholders = other.placeholders;
    }

    public void add(Area area) {
        areas.add(area);
    }

    public List<String> render(Player player) {
        List<String> result = new ArrayList<>();
        areas.forEach(area -> result.addAll(area.render(player, placeholders)));
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

    // AREA
    public interface Area {
        void update();

        List<String> render(Player player, PlaceholderRegistry placeholders);

        Area copy();
    }

    // TEXT AREA
    @Data
    public class TextArea implements Area {
        private List<PlaceholderValue<String>> lines = new ArrayList<>();

        public TextArea() {
        }

        public TextArea(List<PlaceholderValue<String>> lines) {
            this.lines = lines;
        }

        public void add(PlaceholderValue<String> line) {
            lines.add(line);
        }

        public void add(List<PlaceholderValue<String>> lines) {
            this.lines.addAll(lines);
        }

        @Override
        public void update() {
        }

        @Override
        public List<String> render(Player player, PlaceholderRegistry placeholders) {
            return lines.stream()
                    .map(line -> line.resolve(player, placeholders))
                    .collect(Collectors.toList());
        }

        @Override
        public TextArea copy() {
            return new TextArea(new ArrayList<>(lines));
        }
    }
}
