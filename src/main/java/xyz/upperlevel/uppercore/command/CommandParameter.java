package xyz.upperlevel.uppercore.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

public class CommandParameter {
    @Getter
    private final Command command;

    /**
     * The name of the parameter, not always defined.
     * The only way to know it is using @WithName annotation.
     */
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private boolean optional;

    @Getter
    @Setter
    private Object defaultValue;

    /**
     * The permission portion for the parameter, could be null.
     */
    @Getter
    @Setter
    private Permission permissionPortion = null;

    @Getter
    @Setter
    @NonNull
    private PermissionCompleter permissionCompleter = PermissionCompleter.INHERIT;

    /**
     * The permission to use the parameter.
     * If parameter's permission portion is null, then it'll be too and won't be checked.
     */
    @Getter
    @Setter
    private Permission permission = null;

    @Getter
    @Setter
    private SenderType senderType;

    public CommandParameter(Command command) {
        this.command = command;
    }

    public void completePermission() {
        if (permissionPortion != null) {
            permissionCompleter.complete(command.getPermission(), permissionPortion);
        }
    }

    public void registerPermission(PluginManager pluginManager) {
        if (permission != null) {
            pluginManager.addPermission(permission);
        }
    }

    public boolean hasPermission(CommandSender player) {
        return permission == null || player.hasPermission(permission);
    }
}
