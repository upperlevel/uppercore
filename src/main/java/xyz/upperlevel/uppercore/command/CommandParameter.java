package xyz.upperlevel.uppercore.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

public class CommandParameter {
    @Getter
    private final Command parent;

    @Getter
    @Setter
    private String name = "par"; // Default parameter name

    @Getter
    @Setter
    private boolean optional;

    @Getter
    @Setter
    private String[] defaultValue;

    @Getter
    @Setter
    private Permission permission;

    @Getter
    @Setter
    private SenderType senderType;

    public CommandParameter(Command parent) {
        this.parent = parent;
    }

    /**
     * Registers only this permission.
     * May be overridden to register other permissions.
     */
    public void registerPermission(PluginManager pluginManager) {
        if (permission != null) {
            pluginManager.addPermission(permission);
        }
    }

}
