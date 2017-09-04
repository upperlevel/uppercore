package xyz.upperlevel.uppercore.board;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Data;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Identifier;
import xyz.upperlevel.uppercore.config.Config;

@Data
public class BoardId extends Identifier<Board> {
    private String permission;
    private boolean onJoin;

    public BoardId(Plugin plugin, String id, Config config) {
        this(plugin, id, new Board(config));
        permission = config.getString("permission");
        onJoin = config.getBool("on-join");
    }

    public BoardId(Plugin plugin, String id, Board handle) {
        super(plugin, id, handle);
    }
}
