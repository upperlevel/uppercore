package xyz.upperlevel.uppercore.command.functional;

import com.google.common.base.Defaults;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.command.PermissionCompleter;
import xyz.upperlevel.uppercore.command.SenderType;
import xyz.upperlevel.uppercore.command.functional.parameter.ParameterHandler;
import xyz.upperlevel.uppercore.command.functional.parameter.ParameterParseException;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FunctionalParameter {
    @Getter
    @NonNull
    private final FunctionalCommand command;

    @Getter
    @NonNull
    private final Parameter original;

    @Getter
    @NonNull
    private final Class<?> type;

    /**
     * The name of the parameter, won't be null.
     * By default is initialized with the lower-cased parameter's class name.
     */
    @Getter
    @Setter
    @NonNull
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

    public FunctionalParameter(FunctionalCommand command, Parameter original, Class<?> type) {
        this.command = command;
        this.original = original;
        this.type = type;

        this.name = original.getName();

        load(original);
    }

    private void load(Parameter parameter) {
        WithName nameAnn = parameter.getAnnotation(WithName.class);
        if (nameAnn != null) {
            name = nameAnn.value();
        }

        WithOptional wOptional = parameter.getAnnotation(WithOptional.class);
        if (wOptional != null) {
            optional = true;

            List<String> defValArgs = Arrays.asList(wOptional.value());
            try {
                defaultValue = ParameterHandler.parse(type, new LinkedList<>(defValArgs));
            } catch (ParameterParseException e) {
                if (!defValArgs.isEmpty())
                    Uppercore.logger().severe(String.format(
                            "%s default value failed to parse for command: %s",
                            getUsage(null, false),
                            command.getFullName()
                    ));
                defaultValue = Defaults.defaultValue(type);
            }
        }

        WithPermission permissionAnn = parameter.getAnnotation(WithPermission.class);
        if (permissionAnn != null) {
            permissionPortion = new Permission(permissionAnn.value(), permissionAnn.description(), permissionAnn.user().get());
            permissionCompleter = permissionAnn.completer();
        }
    }

    /* Permission */

    public void completePermission() {
        if (permissionPortion != null) {
            permission = permissionCompleter.complete(command.getPermission(), permissionPortion);
        }
    }

    public void registerPermission() {
        if (permission != null) {
            Bukkit.getPluginManager().addPermission(permission);
        }
    }

    public boolean hasPermission(CommandSender sender) {
        return permission == null || sender.hasPermission(permission);
    }

    /* Usage */

    public String getUsage(CommandSender sender, boolean colored) {
        // [arg0=value] optional with default value
        // <arg0> non-optional
        if (optional) {
            String tmp = "";
            if (colored) {
                tmp += (sender != null && hasPermission(sender)) ? ChatColor.GREEN : ChatColor.RED;
            }
            tmp += "[" + name;
            if (defaultValue != null) {
                tmp += "=" + defaultValue.toString();
            }
            return tmp + "]";
        } else {
            return "<" + name + ">";
        }
    }
}
