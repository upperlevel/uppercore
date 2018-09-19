package xyz.upperlevel.uppercore.command;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.util.StringUtil;
import xyz.upperlevel.uppercore.command.functional.FunctionalCommand;

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

        everyPermission("*", PermissionUser.AVAILABLE);

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
    public boolean call(CommandSender sender, List<String> args) {
        super.call(sender, args);
        if (args.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments. You must specify the command name.");
            return false;
        }
        Command cmd = getCommand(args.get(0));
        if (cmd == null || !sender.hasPermission(cmd.getPermission())) {
            sender.sendMessage(ChatColor.RED + "No command found for: " + args.get(0));
            return false;
        }
        cmd.call(sender, args.subList(1, args.size()));
        return true;
    }

    @Override
    protected boolean onCall(CommandSender sender, List<String> args) {
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, List<String> arguments) {
        if (arguments.isEmpty()) { // if there is no argument we list all runnable commands
            return commands
                    .stream()
                    .filter(command -> sender.hasPermission(command.getPermission()))
                    .map(Command::getName)
                    .collect(Collectors.toList());
        } else if (arguments.size() > 1) { // if there are more than one argument we get sub command and ask a suggestion
            Command command = getCommand(arguments.get(0));
            if (command != null) {
                return command.suggest(sender, arguments.subList(1, arguments.size()));
            }
            return Collections.emptyList();
        } else { // if there is just one argument we need to get the commands that starts with it
            String argument = arguments.get(0);
            return commands
                    .stream()
                    .filter(command -> sender.hasPermission(command.getPermission()))
                    .map(Command::getName)
                    .filter(name -> StringUtil.startsWithIgnoreCase(name, argument))
                    .collect(Collectors.toList());
        }
    }
}
