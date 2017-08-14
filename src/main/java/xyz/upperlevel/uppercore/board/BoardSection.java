package xyz.upperlevel.uppercore.board;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

import java.util.List;

public interface BoardSection {
    List<String> render(Player player, PlaceholderRegistry placeholders);
}
