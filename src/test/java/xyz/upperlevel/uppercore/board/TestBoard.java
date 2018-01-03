package xyz.upperlevel.uppercore.board;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

public class TestBoard implements Board {
    @Override
    public String getTitle(Player holder) {
        return BLUE + holder.getName();
    }

    @Override
    public List<String> getLines(Player holder) {
        List<String> result = new ArrayList<>();
        result.add("Name: " + RED + holder.getName());
        result.add("");
        result.add("Another line!");
        result.add("");
        result.add("Health: " + RED + holder.getHealth());
        result.add("Food Level: " + RED + holder.getFoodLevel());
        result.add("");
        result.add(YELLOW + " LAST LINE!");
        result.add("");
        result.add("Seconds passed: " + TestBoardPlugin.getInstance().getSecondsSinceStart());
        result.add("");
        return result;
    }
}
