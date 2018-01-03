package xyz.upperlevel.uppercore.board;

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

@Getter
@Setter
public class ConfigBoard implements Board {
    private PlaceholderValue<String> title;
    private int updateInterval;
    private PlaceholderRegistry placeholderRegistry = PlaceholderRegistry.create();
    private final List<ConfigBoardSection> sections = new LinkedList<>();

    private ConfigBoard() {
    }

    @Override
    public String getTitle(Player player) {
        return title.resolve(player);
    }

    public void addSection(ConfigBoardSection section) {
        if (section == null) {
            throw new NullPointerException("section");
        }
        sections.add(section);
    }

    @Override
    public List<String> solve(Player player) {
        List<String> res = new ArrayList<>();
        for (ConfigBoardSection sec : sections) {
            sec.solve(player, placeholderRegistry);
        }
        return res;
    }

    public static ConfigBoard deserialize(Config config) {
        ConfigBoard res = new ConfigBoard();
        try {
            res.setTitle(config.getMessageStr("title"));
            res.setUpdateInterval(config.getInt("updateInterval", -1));
            if (config.has("lines")) {
                res.addSection(new ConfigBoardSection.Fixed(config.getMessageStrList("lines")));
            }
        } catch (InvalidConfigurationException e) {
            e.addLocation("board");
            throw e;
        }
        return res;
    }
}
