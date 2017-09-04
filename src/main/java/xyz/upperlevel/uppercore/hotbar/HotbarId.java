package xyz.upperlevel.uppercore.hotbar;

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
public class HotbarId extends Identifier<Hotbar> {
    private String permission;
    private boolean onJoin;

    public HotbarId(Plugin plugin, String id, Config config) {
        this(plugin, id, new Hotbar(plugin, config));
        permission = config.getString("permission");
        onJoin = config.getBool("on-join");
    }

    public HotbarId(Plugin plugin, String id, Hotbar handle) {
        super(plugin, id, handle);
    }
}
