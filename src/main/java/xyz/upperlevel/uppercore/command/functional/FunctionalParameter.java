package xyz.upperlevel.uppercore.command.functional;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import xyz.upperlevel.uppercore.command.PermissionCompleter;
import xyz.upperlevel.uppercore.command.SenderType;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParseException;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParser;

import java.lang.reflect.Parameter;
import java.util.Arrays;

public class FunctionalParameter {
    @Getter
    @NonNull
    private final FunctionalCommand command;

    @Getter
    @NonNull
    private final Parameter original;

    @Getter
    @NonNull
    private final ArgumentParser parser;

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

    public FunctionalParameter(FunctionalCommand command, Parameter original, ArgumentParser parser) {
        this.command = command;
        this.original = original;
        this.parser = parser;

        this.name = original.getName().toLowerCase();

        load(original);
    }

    private void load(Parameter parameter) {
        WithName name = parameter.getAnnotation(WithName.class);
        if (name != null) {
            this.name = name.value();
        }

        WithOptional optional = parameter.getAnnotation(WithOptional.class);
        if (optional != null) {
            this.optional = true;
            if (optional.value().length >= parser.getConsumedCount()) {
                try {
                    this.defaultValue = parser.parse(Arrays.asList(optional.value()));
                } catch (ArgumentParseException e) {
                    throw new IllegalArgumentException("Optional's default value must be parsable to the parameter type. Invalid '" + Arrays.toString(optional.value()) + "' for type: " + original.getType().getSimpleName(), e);
                }
            }
        }

        WithPermission permission = parameter.getAnnotation(WithPermission.class);
        if (permission != null) {
            this.permissionPortion = new Permission(permission.value(), permission.description(), permission.defaultUser().get(command));
            this.permissionCompleter = permission.completer();
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
                tmp += hasPermission(sender) ? ChatColor.GREEN : ChatColor.RED;
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
