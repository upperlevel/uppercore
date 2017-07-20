package xyz.upperlevel.uppercore.scoreboard;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Identifiable;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Map;

import static java.util.Locale.ENGLISH;

@Data
public class Scoreboard implements Identifiable {

    public static final int MAX_LINES = 15;

    private Plugin plugin;
    private String id;

    private PlaceholderValue<String> title;
    private final PlaceholderValue<String>[] lines = new PlaceholderValue[MAX_LINES]; // :(

    public Scoreboard(Plugin plugin, String id) {
        this.plugin = plugin;
        this.id = id.toLowerCase(ENGLISH);
    }

    // SCOREBOARD

    public void setTitle(String title) {
        setTitle(PlaceholderValue.stringValue(title));
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

    public ScoreboardView open(Player player) {
        ScoreboardView view = ScoreboardSystem.view(player);
        view.setScoreboard(this);
        return view;
    }

    // SERIALIZATION

    public static Scoreboard deserialize(Plugin plugin, String id, Config config) {
        Scoreboard result = new Scoreboard(plugin, id);
        result.setTitle(config.getStringRequired("title"));
        for (Object line : config.getListRequired("lines")) {
            if (line instanceof String) {
                result.addLine((String) line);
            } else if (line instanceof Map) {
                result.setLine(
                        (int) ((Map) line).get("index"),
                        (String) ((Map) line).get("text")
                );
            }
        }
        return result;
    }
}
