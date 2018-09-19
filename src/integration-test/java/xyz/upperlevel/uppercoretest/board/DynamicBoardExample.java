package xyz.upperlevel.uppercoretest.board;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.board.Board;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

import java.util.Arrays;
import java.util.List;

import static org.bukkit.ChatColor.*;


public class DynamicBoardExample implements Board {
    @Override
    public String getTitle(Player player, PlaceholderRegistry placeholderRegistry) {
        return BLUE + "" + BOLD + "Dynamic Board";
    }

    @Override
    public List<String> getLines(Player player, PlaceholderRegistry placeholderRegistry) {
        return Arrays.asList(
                "",
                "Update time (in ms):",
                YELLOW + "" + System.currentTimeMillis(),
                ""
        );
    }

    @Override
    public int getAutoUpdateInterval() {
        return 20 * 5; // update each 5 seconds
    }
}
