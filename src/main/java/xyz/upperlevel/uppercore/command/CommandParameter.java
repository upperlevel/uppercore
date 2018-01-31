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
    private Object defaultValue;

    @Getter
    @Setter
    private Permission relativePermission;

    @Getter
    @Setter
    private PermissionCompleter permissionCompleter;

    @Getter
    @Setter
    private Permission permission; // The resulting permission

    @Getter
    @Setter
    private SenderType senderType;

    public CommandParameter(Command parent) {
        this.parent = parent;
    }

    public void completePermission() {
        Permission parentPermission = null;
        if (parent != null) {
            parentPermission = parent.getPermission();
        }
        permission = permissionCompleter.complete(parentPermission, relativePermission);
    }

    public void registerPermission(PluginManager pluginManager) {
        if (permission != null) {
            pluginManager.addPermission(permission);
        }
    }

}
