package xyz.upperlevel.uppercore.scoreboard;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Identifiable;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;
import xyz.upperlevel.uppercore.util.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class Scoreboard implements Identifiable {

    private Plugin plugin;
    private String id;

    private PlaceholderValue<String> title;
    private final List<PlaceholderValue<String>> lines = new ArrayList<>();

    public Scoreboard() {
    }

    public Scoreboard(Plugin plugin, String id) {
        this.plugin = plugin;
        this.id = id;
    }

    // SCOREBOARD

    public void setTitle(String title) {
        setTitle(PlaceholderValue.stringValue(title));
    }

    public void setTitle(PlaceholderValue<String> title) {
        this.title = title;
    }

    public void addLine(String line) {
        addLine(PlaceholderValue.stringValue(line));
    }

    public void addLine(PlaceholderValue<String> line) {
        lines.add(line);
    }

    public void setLine(int index, String line) {
        setLine(index, PlaceholderValue.stringValue(line));
    }

    public void setLine(int index, PlaceholderValue<String> line) {
        lines.set(index, line);
    }

    public PlaceholderValue<String> getLine(int index) {
        return lines.get(index);
    }

    public ScoreboardView open(Player player) {
        ScoreboardView view = ScoreboardSystem.getView(player);
        view.setScoreboard(this);
        return view;
    }

    // SERIALIZATION

    public static Scoreboard deserialize(Plugin plugin, String id, Config config) {
        Scoreboard result = new Scoreboard(plugin, id);
        result.setTitle(config.getString("title"));
        for (Object line : config.getList("lines")) {
            if (line instanceof String)
                result.addLine((String) line);
            else if (line instanceof Map) {
                result.setLine(
                        (int) ((Map) line).get("index"),
                        (String) ((Map) line).get("text")
                );
            }
        }
        return result;
    }
}
