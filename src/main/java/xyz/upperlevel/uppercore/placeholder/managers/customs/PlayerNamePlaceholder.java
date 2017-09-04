package xyz.upperlevel.uppercore.placeholder.managers.customs;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.Placeholder;

public class PlayerNamePlaceholder implements Placeholder {

    @Override
    public String getId() {
        return "player_name";
    }

    @Override
    public String resolve(Player player, String id) {
        return player.getName();
    }
}
