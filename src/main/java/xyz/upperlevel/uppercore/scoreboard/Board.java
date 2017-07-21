package xyz.upperlevel.uppercore.scoreboard;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Identifiable;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.InvalidConfigurationException;
import xyz.upperlevel.uppercore.placeholder.Placeholder;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;

import static java.util.Locale.ENGLISH;
import static xyz.upperlevel.uppercore.scoreboard.BoardUtil.MAX_LINES;

@Data
public class Board implements Identifiable {

    private Plugin plugin;
    private String id;

    private PlaceholderValue<String> title;
    private final PlaceholderValue<String>[] lines = new PlaceholderValue[MAX_LINES]; // :(

    public Board() {
    }

    public Board(Plugin plugin, String id) {
        this.plugin = plugin;
        this.id = id.toLowerCase(ENGLISH);
    }

    public Board(Plugin plugin, String id, Config config) {
        this(plugin, id);
        title = PlaceholderValue.stringValue(config.getStringRequired("title"));
        List<String> lines = config.getList("lines");
        if (lines != null) {
            for (Object line : lines) {
                if (line instanceof String) {
                    addLine((String) line);
                } else if (line instanceof Map) {
                    setLine(
                            (int) ((Map) line).get("index"),
                            (String) ((Map) line).get("text")
                    );
                }
            }
        }
    }

    public void setTitle(String title) {
        setTitle(PlaceholderUtil.process(title));
    }

    public void setTitle(PlaceholderValue<String> title) {
        this.title = title;
    }

    public int getNextFree() {
        for (int i = 0; i < lines.length; i++)
            if (lines[i] == null)
                return i;
        return -1;
    }

    public boolean addLine(String line) {
        return addLine(PlaceholderUtil.process(line));
    }

    public boolean addLine(PlaceholderValue<String> line) {
        int i = getNextFree();
        if (i >= 0) {
            lines[i] = line;
            return true;
        }
        return false;
    }

    public void setLine(int index, String line) {
        setLine(index, PlaceholderUtil.process(line));
    }

    public void setLine(int index, PlaceholderValue<String> line) {
        lines[index] = line;
    }

    public PlaceholderValue<String> getLine(int index) {
        return lines[index];
    }

    public BoardView open(Player player) {
        BoardView view = ScoreboardSystem.view(player);
        view.setBoard(this);
        return view;
    }

    public static Board deserialize(Plugin plugin, String id, Config config) {
        try {
            return new Board(plugin, id, config);
        } catch (InvalidConfigurationException e) {
            e.addLocalizer("in scoreboard " + id);
            throw e;
        }
    }
}
