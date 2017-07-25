package xyz.upperlevel.uppercore.hotbar;

import lombok.Data;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Registrable;
import xyz.upperlevel.uppercore.config.Config;

@Data
public class RegistrableHotbar extends Registrable<Hotbar> {
    private String permission;
    private boolean onJoin;

    public RegistrableHotbar(Plugin plugin, String id, Config config) {
        this(plugin, id, new Hotbar(plugin, config));
        permission = config.getString("permission");
        onJoin = config.getBool("on-join");
    }

    public RegistrableHotbar(Plugin plugin, String id, Hotbar handle) {
        super(plugin, id, handle);
    }
}
