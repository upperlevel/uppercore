package xyz.upperlevel.uppercore.hotbar;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Identifier;
import xyz.upperlevel.uppercore.config.Config;

public class HotbarId extends Identifier<Hotbar> {
    @Getter
    @Setter
    private String permission;
    @Getter
    @Setter
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
