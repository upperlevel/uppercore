package xyz.upperlevel.uppercore.command;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.command.argument.ArgumentParser;
import xyz.upperlevel.uppercore.command.argument.ArgumentParserSystem;
import xyz.upperlevel.uppercore.command.argument.exceptions.ParseException;
import xyz.upperlevel.uppercore.command.argument.exceptions.UnparsableTypeException;
import xyz.upperlevel.uppercore.util.TextUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static lombok.AccessLevel.NONE;
import static org.bukkit.ChatColor.*;

@Getter
@Setter
public abstract class Command implements CommandExecutor, TabCompleter {

    private NodeCommand parent;

    private final String name;
    private String description;
    private List<String> aliases = new ArrayList<>();
    private Permission permission;

    private Sender sender = Sender.ALL; // non null

    @Getter(NONE)
    private Method executor;

    public Command(String name) {
        this.name = name.toLowerCase();
        addAlias(this.name);
        setup();
    }

    public void setParent(NodeCommand parent) {
        this.parent = parent;
    }

    /**
     * Adds the given alias to this commands.
     *
     * @param alias the alias to add
     */
    public void addAlias(String alias) {
        aliases.add(alias.toLowerCase());
        aliases.sort(String::compareTo);
    }

    /**
     * Adds aliases to this commands.
     *
     * @param aliases the aliases to add
     */
    public void addAliases(String... aliases) {
        for (String alias : aliases)
            addAlias(alias);
    }

    /**
     * Adds the given aliases to this commands.
     *
     * @param aliases the alaises to add
     */
    public void addAliases(List<String> aliases) {
        for (String alias : aliases)
            addAlias(alias);
    }

    public void setup() {
        for (Method method : getClass().getDeclaredMethods()) {
            Executor e = method.getAnnotation(Executor.class);
            if (e != null) {
                if (executor == null) {
                    method.setAccessible(true);
                    executor = method;
                    sender = e.sender();
                }
            }
        }
    }

    private Map<Class<? extends CommandSender>, String> helplines = new HashMap<>();

    public String getHelpline(CommandSender sender, boolean format) {
        return helplines.computeIfAbsent(sender.getClass(), type -> getUsage(sender, format) + " " + (format ? GRAY : "") + description);
    }

    public String getUsage() {
        return getUsage(null, false);
    }

    public String getUsage(boolean format) {
        return getUsage(null, format);
    }

    /**
     * Gets the commands complete usage depending from the given sender.
     *
     * @param sender the sender to build the usage for
     * @param format the usage have to be colored?
     * @return the complete usage
     */
    public String getUsage(CommandSender sender, boolean format) {
        StringJoiner usage = new StringJoiner(" ");
        List<String> relatives = new ArrayList<>();
        NodeCommand high = parent;
        while (high != null) {
            relatives.add(0, high.getName());
            high = high.getParent();
        }
        if (!relatives.isEmpty())
            usage.add(StringUtils.join(relatives, " "));
        usage.add(getHelplineName(sender, format));

        String args = getHelplineArgs(sender, format);
        if (args != null)
            usage.add(args);

        return (format ? AQUA : "") + "/" + usage.toString();
    }

    public String getHelplineName(CommandSender sender, boolean format) {
        StringJoiner usage = new StringJoiner(",");
        for (String alias : aliases)
            usage.add(alias);
        return (!format ? "" : this.sender.isCorrect(sender) ? AQUA : RED) + usage.toString();
    }

    private static boolean isSenderCorrect(Optional optional, CommandSender sender) {
        for (Sender other : optional.sender())
            if (other.isCorrect(sender))
                return true;
        return false;
    }

    private static String getFormattedValues(Optional optional) {
        if (optional == null)
            return null;
        StringJoiner jnr = new StringJoiner(", ");
        for (String value : optional.value())
            jnr.add(value);
        if (jnr.length() == 0)
            return null;
        else if (jnr.length() == 1)
            return jnr.toString();
        else
            return "{" + jnr.toString() + "}";
    }

    public String getHelplineArgs(CommandSender sender, boolean format) {
        if (executor == null)
            return null;
        StringJoiner usage = new StringJoiner(" ");
        for (int i = 1; i < executor.getParameters().length; i++) {
            Parameter parameter = executor.getParameters()[i];
            Optional optional = parameter.getDeclaredAnnotation(Optional.class);

            StringJoiner value;
            if (optional != null && isSenderCorrect(optional, sender))
                value = new StringJoiner(" ", "[", "]");
            else
                value = new StringJoiner(" ", "<", ">");

            Argument argument = parameter.getDeclaredAnnotation(Argument.class);
            StringBuilder arg = new StringBuilder();
            arg.append((argument != null ? argument.value() : parameter.getName()));
            if (optional != null) {
                String values = getFormattedValues(optional);
                arg.append((values != null ? "=" + values : ""));
            }
            value.add(arg);
            usage.add(value.toString());
        }
        if (usage.length() == 0)
            return null;
        else
            return (format ? DARK_AQUA : "") + usage.toString();
    }

    private boolean isOptional(CommandSender sender, Parameter parameter) {
        Optional opt = parameter.getDeclaredAnnotation(Optional.class);
        return opt != null && isSenderCorrect(opt, sender);
    }

    private Object processOptional(Parameter parameter) throws ParseException {
        Optional opt = parameter.getDeclaredAnnotation(Optional.class);
        int needed = ArgumentParserSystem.getArgumentsCount(parameter.getType());
        if (opt.value().length < needed)
            return null;
        return ArgumentParserSystem.parse(parameter.getType(), Arrays.asList(opt.value()));
    }

    public void execute(CommandSender sender, List<String> args) {
        if (executor == null || !canExecute(sender))
            return;
        List<Object> result = new ArrayList<>();
        result.add(sender);
        int arg = 0;
        for (int i = 1; i < executor.getParameterCount(); i++) {
            Parameter parameter = executor.getParameters()[i];
            Class<?> type = parameter.getType();
            if (arg >= args.size()) {
                if (isOptional(sender, parameter)) {
                    try {
                        result.add(processOptional(parameter));
                    } catch (ParseException e) {
                        sender.sendMessage(e.getMessageFormatted());
                        return;
                    }
                } else {
                    TextUtil.sendMessages(sender, asList(
                            RED + "Command syntax exception. " + GOLD + "You may use the commands like this:",
                            getUsage(sender, true)
                    ));
                    return;
                }
            } else {
                ArgumentParser parser = ArgumentParserSystem.getParser(type);
                if (parser == null)
                    throw new UnparsableTypeException("Unparsable type \"" + type.getName() + "\" in commands \"" + getName() + "\"");

                int used = parser.getArgumentsCount();
                if (used < 0)
                    used = args.size() - arg;
                try {
                    result.add(parser.parse(type, args.subList(arg, arg + used)));
                } catch (ParseException e) {
                    sender.sendMessage(e.getMessageFormatted());
                    return;
                }
                arg += used;
            }
        }
        try {
            executor.invoke(this, result.toArray());
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            sender.sendMessage(RED + "An error occurred during the execution of this commands.");
        }
    }

    public boolean canExecute(CommandSender sender) {
        return permission == null || sender.hasPermission(permission);
    }

    public List<String> tabComplete(CommandSender sender, List<String> args) {
        int arg = 0;
        for (int i = 1; i < executor.getParameterCount(); i++) {
            Parameter parameter = executor.getParameters()[i];
            Class<?> type = parameter.getType();
            ArgumentParser parser = ArgumentParserSystem.getParser(type);
            if (parser == null)
                throw new UnparsableTypeException("Unparsable type \"" + type.getName() + "\" in commands \"" + getName() + "\"");
            if (arg >= args.size()) {
                return parser.onTabCompletion(sender, type, Collections.emptyList());
            } else {
                int used = parser.getArgumentsCount();

                if(used < 0 || used >= args.size() + arg)
                    return parser.onTabCompletion(sender, type, args.subList(arg, args.size()));
                else
                    arg += used;
            }
        }
        return emptyList();
    }

    /**
     * Subscribes the commands to Bukkit commands list.
     * The commands must be registered in plugin.yml by its name.
     */
    public void subscribe() {
        PluginCommand cmd = Bukkit.getPluginCommand(getName());
        if (cmd == null) {
            Uppercore.logger().severe("Command not found in plugin.yml: \"" + getName() + "\"");
            return;
        }
        setDescription(cmd.getDescription());
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
        calcPermissions();
        registerPermissions(Bukkit.getPluginManager());
    }

    public void registerPermissions(PluginManager manager) {
        if(permission != null)
            manager.addPermission(permission);
    }

    public void calcPermissions() {
        WithPermission perm = getClass().getAnnotation(WithPermission.class);
        if(perm != null) {
            String path;
            if(parent != null) {
                if(parent.getPermission() == null)
                    return;
                path = parent.getPermission().getName() + '.' + perm.value();
            } else path = perm.value();
            permission = new Permission(path, perm.value(), perm.def().get(this));
            if(parent != null)
                permission.addParent(parent.getAnyPerm(), true);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        execute(sender, Arrays.asList(args));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        return tabComplete(sender, Arrays.asList(args));
    }
}
