package xyz.upperlevel.uppercore.command.function;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import xyz.upperlevel.uppercore.command.Command;
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
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;

public class FunctionCommand extends Command {
    @Getter
    private Method function;

    public FunctionCommand(String name, Method function) {
        super(name);
        WithCommand annotation = function.getAnnotation(WithCommand.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@FunctionalCommand not found above function: " + function.getName());
        }
        this.function = function;
    }

    @Override
    public String getUsage(CommandSender sender) {
        StringJoiner usage = new StringJoiner(" ");
        for (Parameter parameter : function.getParameters()) {
            usage.add(parameter.getName());
        }
        return usage.toString();
    }

    @Override
    public boolean call(CommandSender sender, List<String> arguments) {
        return false;
    }

    @Override
    public List<String> suggest(CommandSender sender, List<String> arguments) {
        return null;
    }

    public void calculatePermissions() {
        WithPermission perm = getClass().getAnnotation(WithPermission.class);
        if (perm != null) {
            String path;
            if (parent != null) {
                if (parent.getPermission() == null)
                    return;
                path = parent.getPermission().getName() + '.' + perm.value();
            } else path = perm.value();
            permission = new Permission(path, perm.value(), perm.defaultPermission().get(this));
            if (parent != null)
                permission.addParent(parent.getAnyPerm(), true);
        }
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

                if (used < 0 || used >= args.size() + arg)
                    return parser.onTabCompletion(sender, type, args.subList(arg, args.size()));
                else
                    arg += used;
            }
        }
        return emptyList();
    }

    public boolean canExecute(CommandSender sender) {
        return permission == null || sender.hasPermission(permission);
    }

    private boolean isOptional(CommandSender sender, Parameter parameter) {
        WithOptional opt = parameter.getDeclaredAnnotation(WithOptional.class);
        return opt != null && isSenderCorrect(opt, sender);
    }

    private Object processOptional(Parameter parameter) throws ParseException {
        WithOptional opt = parameter.getDeclaredAnnotation(WithOptional.class);
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
}
