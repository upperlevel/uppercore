package xyz.upperlevel.uppercore.command;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.util.StringUtil;
import xyz.upperlevel.uppercore.command.functional.FunctionalCommand;
import xyz.upperlevel.uppercore.util.Dbg;

import java.util.*;
import java.util.stream.Collectors;

public class NodeCommand extends Command {
    private final List<Command> commands = new ArrayList<>();
    private final Map<String, Command> commandsByName = new HashMap<>();

    private Permission everyPermissionRaw;

    @Getter
    private Permission everyPermission;

    public NodeCommand(String name) {
        super(name);

        everyPermission("*", PermissionUser.OP);

        FunctionalCommand.inject(this, new HelpCommand());
    }

    // --------------------------------------------------------------------------------- Descriptor

    public NodeCommand description(String description) {
        setDescription(description);
        return this;
    }

    public NodeCommand aliases(Set<String> aliases) {
        setAliases(aliases);
        return this;
    }

    public NodeCommand aliases(String... aliases) {
        setAliases(new HashSet<>(Arrays.asList(aliases)));
        return this;
    }

    // --------------------------------------------------------------------------------- Permission

    public NodeCommand permission(PermissionUser defaultUser) {
        setPermissionPortion(new Permission(getName(), defaultUser.get()));
        return this;
    }

    public NodeCommand permission(String description, PermissionUser defaultUser) {
        setPermissionPortion(new Permission(getName(), description, defaultUser.get()));
        return this;
    }

    public NodeCommand permission(String name, String description, PermissionUser defaultUser) {
        setPermissionPortion(new Permission(name, description, defaultUser.get()));
        return this;
    }

    public NodeCommand permission(String name, String description, PermissionUser defaultUser, PermissionCompleter permissionCompleter) {
        setPermissionPortion(new Permission(name, description, defaultUser.get()));
        setPermissionCompleter(permissionCompleter);
        return this;
    }

    // --------------------------------------------------------------------------------- Every Permission (*)

    public NodeCommand everyPermission(PermissionUser defaultUser) {
        everyPermissionRaw = new Permission("*", defaultUser.get());
        return this;
    }

    public NodeCommand everyPermission(String description, PermissionUser defaultUser) {
        everyPermissionRaw = new Permission("*", description, defaultUser.get());
        return this;
    }

    // --------------------------------------------------------------------------------- Commands

    public void append(Command command) {
        if (command.getParent() != null) {
            throw new IllegalArgumentException("The same instance of " + command.getClass().getSimpleName() + " is registered in more than one NodeCommand");
        }

        Set<String> keys = command.getAliases();
        keys.add(command.getName());
        for (String key : keys) {
            if (commandsByName.containsKey(key)) {
                throw new IllegalArgumentException("Trying to register command's key '" + key + "' that was already registered node: " + getName());
            }
        }

        commands.add(command);
        for (String key : keys) {
            commandsByName.put(key, command);
        }

        command.setParent(this);
    }

    public void append(List<Command> commands) {
        commands.forEach(this::append);
    }

    public void append(Command... commands) {
        append(Arrays.asList(commands));
    }

    public List<Command> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    private Command getCommand(String name) {
        return commandsByName.get(name.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public void completePermission(Permission root) {
        super.completePermission(root);
        // complete sub commands' permissions
        for (Command command : commands) {
            command.completePermission(root);
        }
        // foreach of the sub commands assign the parent to * permission just created
        everyPermission = PermissionCompleter.INHERIT.complete(getPermission(), everyPermissionRaw);
        for (Command command : commands) {
            command.getPermission().addParent(everyPermission, true);
        }
    }

    @Override
    public void registerPermission() {
        super.registerPermission();
        for (Command command : commands) { // registers all sub commands permission
            command.registerPermission();
        }
        Bukkit.getPluginManager().addPermission(everyPermission); // and * permission
    }

    @Override
    public String getUsage(CommandSender sender, boolean colored) {
        return "<command>";
    }

    @Override
    public boolean call(CommandSender sender, Queue<String> args) {
        super.call(sender, args);
        if (args.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments. You must specify the command name.");
            return false;
        }
        String subCmdName = args.poll();
        Command cmd = getCommand(subCmdName);
        if (cmd == null || !sender.hasPermission(cmd.getPermission())) {
            sender.sendMessage(ChatColor.RED + "No command found for: " + subCmdName);
            return false;
        }
        cmd.call(sender, args);
        return true;
    }

    @Override
    protected boolean onCall(CommandSender sender, Queue<String> args) {
        return false;
    }

    @Override
    public List<String> suggest(CommandSender sender, Queue<String> args) {
        Dbg.pf("%s suggesting for: %s", getFullName(), args.toString());
        if (args.size() > 1) {
            Command subCmd = getCommand(args.poll());
            if (subCmd != null) {
                return subCmd.suggest(sender, args);
            }
            return Collections.emptyList();
        } else {
            String arg = args.isEmpty() ? "" : args.poll();
            return commands
                    .stream()
                    .filter(command -> sender.hasPermission(command.getPermission()))
                    .map(Command::getName)
                    .filter(name -> StringUtil.startsWithIgnoreCase(name, arg))
                    .collect(Collectors.toList());
        }
    }
}
