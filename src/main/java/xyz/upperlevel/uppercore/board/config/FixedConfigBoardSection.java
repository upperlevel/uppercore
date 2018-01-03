package xyz.upperlevel.uppercore.board;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.ArrayList;
import java.util.List;

public class FixedConfigBoardSection implements ConfigBoardSection {
    private final List<PlaceholderValue<String>> lines;

    public FixedConfigBoardSection(List<PlaceholderValue<String>> lines) {
        this.lines = lines;
    }

    @Override
    public List<String> solve(Player player, PlaceholderRegistry placeholderRegistry) {
        List<String> res = new ArrayList<>();
        for (PlaceholderValue<String> phLine : lines) {
            res.add(phLine.resolve(player, placeholderRegistry));
        }
        return res;
    }
}
