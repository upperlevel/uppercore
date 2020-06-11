package xyz.upperlevel.uppercore.command;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.Nullable;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.message.Message;
import xyz.upperlevel.uppercore.util.Dbg;

import java.util.*;

public abstract class Command {
    @Getter
    NodeCommand parent;

    @Getter
    private final String name;

    @Setter
    private Set<String> aliases = new HashSet<>();

    @Getter
    @Setter
    private String description = "No description";

    @Getter
    @Setter
    @NonNull
    private SenderType senderType = SenderType.ALL; // non null

    /**
     * The relative permission of this command (or absolute if PermissionCompleter.NONE).
     * By default is the same as the name of the command. Cannot be null.
     */
    @Getter
    @Setter
    @NonNull
    private Permission permissionPortion;

    @Getter
    @Setter
    @NonNull
    private PermissionCompleter permissionCompleter = PermissionCompleter.INHERIT;

    @Getter
    @Setter
    @NonNull
    private Permission permission;

    /* Configuration */

    private static Message noPermissionMessage;
    private static Message wrongSenderTypeMessage;

    public Command(String name) {
        this.name = name.toLowerCase(Locale.ENGLISH);
        permissionPortion = new Permission(this.name, PermissionDefault.TRUE);
    }

    protected void setParent(NodeCommand parent) {
        this.parent = parent;
    }

    /* Permission */

    public void completePermission(Permission root) {
        if (parent != null) {
            permission = permissionCompleter.complete(parent.getPermission(), permissionPortion);
        } else {
            permission = permissionCompleter.complete(root, permissionPortion);
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

    /* Descriptor */

    public void addAlias(String alias) {
        aliases.add(alias.toLowerCase(Locale.ENGLISH));
    }

    public void addAliases(String... aliases) {
        for (String alias : aliases) {
            this.aliases.add(alias.toLowerCase(Locale.ENGLISH));
        }
    }

    /**
     * Gets a copy of the aliases set.
     */
    public Set<String> getAliases() {
        return Sets.newHashSet(aliases);
    }

    /**
     * Gets the usage for this command.
     * The usage must contain only command's parameters (without its name).
     * Usage format should respect: {@code <arg1> <arg2> [optional=value]}.
     * The colored variable changes the usage color based on the sender.
     */
    public abstract String getUsage(@Nullable CommandSender sender, boolean colored);

    public String getFullName() {
        StringBuilder path = new StringBuilder();
        NodeCommand higher = parent;
        while (higher != null) {
            path.insert(0, higher.getName() + " ");
            higher = higher.getParent();
        }
        path.append(name);
        return path.toString();
    }

    /**
     * Gets the helpline for this command.
     * The helpline is the path of the command (a chain of parents' commands) followed by command's name and usage.
     * An example: {@code parent1 parent2 command <arg1> <arg2> [optional=value]}.
     * The colored parameter changes the helpline color based on the sender.
     */
    public String getHelpline(CommandSender sender, boolean colored) {
        String fullName = getFullName();
        String usage = getUsage(sender, colored);
        if (usage.length() > 0) {
            fullName += " " + usage;
        }
        if (colored) {
            fullName = (hasPermission(sender) ? ChatColor.GREEN : ChatColor.RED) + fullName;
        }
        return fullName;
    }

    public boolean call(CommandSender sender, Queue<String> args) {
        if (!hasPermission(sender)) {
            noPermissionMessage.send(sender, PlaceholderRegistry.create()
                    .set("permission", permission.getName())
            );
            return false;
        }
        if (!senderType.match(sender)) {
            wrongSenderTypeMessage.send(sender, PlaceholderRegistry.create()
                    .set("sender", senderType.name().toLowerCase(Locale.ENGLISH))
            );
            return false;
        }
        onCall(sender, args);
        return true;
    }

    protected abstract boolean onCall(CommandSender sender, Queue<String> args);

    /**
     * Given a list of the inserted args, that goes from the root comamnd through all sub-commands,
     * returns a list of the suggested strings that can complete the very last arg inserted.
     *
     * Example:
     * - args: ["q", "join", ""]
     * - returns: ["arena1", "arena2", "arena3"]
     *
     * - args: ["q", "join"]
     * - returns: []
     *
     * - args: ["q", "j"]
     * - returns: ["join"]
     *
     * @param sender The sender requesting the suggestion.
     * @param args   The args query inserted until now.
     * @return       The suggestions.
     */
    public List<String> suggest(CommandSender sender, Queue<String> args) {
        //Dbg.pf("Requesting suggestion at %s for: %s", name, args.toString());

        // Can't suggest anything since we don't have a concrete structure of how args fit into parameters.
        return Collections.emptyList();
    }

    public static void configure(Config cfg) {
        noPermissionMessage = cfg.getMessage("no-permission");
        wrongSenderTypeMessage = cfg.getMessage("wrong-sender-type");
    }
}
