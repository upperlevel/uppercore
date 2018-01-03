package xyz.upperlevel.uppercore.board;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

import java.util.List;

public interface ConfigBoardSection {
    List<String> solve(Player player, PlaceholderRegistry placeholderRegistry);
}
