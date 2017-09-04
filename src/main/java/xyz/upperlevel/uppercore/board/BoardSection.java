package xyz.upperlevel.uppercore.board;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

import java.util.List;

public interface BoardSection {
    List<String> render(Player player, PlaceholderRegistry placeholders);
}
