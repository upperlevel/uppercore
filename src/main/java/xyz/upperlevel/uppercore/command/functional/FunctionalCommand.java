package xyz.upperlevel.uppercore.command.functional;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.CommandContext;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParseException;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParser;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParserManager;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.message.Message;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class FunctionalCommand extends Command {
    /**
     * The object that holds the command function.
     */
    @Getter
    private Object residence;

    @Getter
    private Method function;

    /**
     * First parameter can be a CommandSender or a CommandContext.
     */
    @Getter
    private Parameter firstParameter;

    @Getter
    private FunctionalParameter[] parameters;

    /* Configuration */

    private static Message noPermissionOnParameterMessage;
    private static Message invalidUsageMessage;
    private static Message invalidArgumentTypeMessage;

    public FunctionalCommand(String name, Object residence, Method function) {
        super(name.toLowerCase(Locale.ENGLISH));
        this.residence = residence;
        this.function = function;

        AsCommand command = function.getAnnotation(AsCommand.class);
        if (command == null) {
            throw new IllegalArgumentException("@AsCommand not found above function: " + function.getName());
        }
        if (!command.description().isEmpty()) {
            setDescription(command.description());
        }
        setAliases(new HashSet<>(Arrays.asList(command.aliases())));
        setSenderType(command.sender());

        WithPermission permission = function.getAnnotation(WithPermission.class);
        if (permission != null) {
            setPermissionPortion(new Permission(
                    permission.value().isEmpty() ? getName() : permission.value(),
                    permission.description(),
                    permission.user().get())
            );
            setPermissionCompleter(permission.completer());
        }

        if (function.getParameterCount() == 0) {
            throw new IllegalArgumentException("'" + function.getName() + "' command function has 0 parameters. A command function should have at least one.");
        }
        firstParameter = function.getParameters()[0];
        this.parameters = new FunctionalParameter[function.getParameterCount() - 1];
        for (int i = 0; i < function.getParameterCount() - 1; i++) {
            Parameter parameter = function.getParameters()[i + 1];
            ArgumentParser parser = ArgumentParserManager.get(parameter.getType());
            if (parser != null) {
                this.parameters[i] = new FunctionalParameter(this, parameter, parser);
            } else {
                throw new IllegalArgumentException("'" + function.getName() + "' command function unparsable type: " + parameter.getType().getName());
            }
        }
    }

    /* Permission */

    @Override
    public void completePermission(Permission root) {
        super.completePermission(root);
        for (FunctionalParameter parameter : parameters) {
            parameter.completePermission();
        }
    }

    @Override
    public void registerPermission() {
        super.registerPermission();
        for (FunctionalParameter parameter : parameters) {
            parameter.registerPermission();
        }
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        if (!super.hasPermission(sender)) {
            return false;
        }
        for (FunctionalParameter parameter : parameters) {
            if (!parameter.isOptional() && !parameter.hasPermission(sender)) {
                return false;
            }
        }
        return true;
    }

    /* Usage */

    @Override
    public String getUsage(CommandSender sender, boolean colored) {
        StringJoiner result = new StringJoiner(" ");
        for (FunctionalParameter parameter : parameters) {
            result.add(parameter.getUsage(sender, colored));
        }
        return result.toString();
    }

    @Override
    protected boolean onCall(CommandSender sender, List<String> args) {
        List<Object> objects = new ArrayList<>();

        // As first parameters are supported CommandSender or CommandContext
        if (firstParameter.getType() == CommandSender.class) {
            objects.add(sender);
        } else if (firstParameter.getType() == CommandContext.class) {
            objects.add(new CommandContext(sender, this));
        } else {
            throw new IllegalArgumentException("'" + function.getName() + "' command function has a wrong first parameter type.");
        }

        int currArgIndex = 0;
        for (int i = 0; i < parameters.length; i++) {
            FunctionalParameter parameter = parameters[i];
            ArgumentParser parser = parameter.getParser();
            if (!parameter.hasPermission(sender)) {
                if (parameter.isOptional()) { // if the parameter is optional we add its default value
                    objects.add(parameter.getDefaultValue());
                } else { // no, the parameter is not optional we need to throw an error
                    noPermissionOnParameterMessage.send(sender, PlaceholderRegistry.create()
                            .set("permission", parameter.getPermission().getName())
                            .set("parameter", parameter.getName())
                            .set("parameter_index", i)
                    );
                    return false;
                }
            } else { // if the sender has enough permissions to execute this parameter
                if (currArgIndex >= args.size()) { // if we already used all of our arguments
                    if (parameter.isOptional()) {
                        // if the parameter is optional
                        objects.add(parameter.getDefaultValue()); // we can use its default value
                    } else {
                        // otherwise we throw an illegal arguments count exception (or invalid usage)
                        invalidUsageMessage.send(sender, PlaceholderRegistry.create()
                                .set("usage", getHelpline(sender, false))
                        );
                        return false;
                    }
                } else { // otherwise, if we have other arguments to use
                    int consumed = parser.getConsumedCount();
                    if (consumed < 0) {
                        consumed = args.size() - currArgIndex;
                    }
                    try {
                        objects.add(parser.parse(args.subList(currArgIndex, currArgIndex + consumed)));
                    } catch (ArgumentParseException e) {
                        invalidArgumentTypeMessage.send(sender, PlaceholderRegistry.create()
                                .set("parameter", parameter.getName())
                                .set("parameter_type", parameter.getOriginal().getType().getSimpleName().toLowerCase(Locale.ENGLISH))
                                .set("parameter_index", i)
                                .set("wrong_argument", StringUtils.join(e.getArguments(), " "))
                        );
                        return false;
                    }
                    currArgIndex += consumed;
                }
            }
        }
        try {
            function.invoke(residence, objects.toArray());
        } catch (IllegalAccessException ignored) { // Should not be called
            throw new IllegalStateException("What the hell have you done to reach this exception?");
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("The command " + getClass().getName() + " thrown an exception", e);
        }
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, List<String> arguments) {
        int start = 0;
        for (FunctionalParameter parameter : parameters) {
            if (!parameter.hasPermission(sender)) {// if the sender has not enough permissions for this parameter
                return Collections.emptyList(); // we does not suggest nothing to him
            }
            ArgumentParser solver = parameter.getParser();
            if (start >= arguments.size()) {
                return solver.suggest(Collections.emptyList());
            } else {
                int consumed = solver.getConsumedCount();
                if (consumed < 0 || consumed >= arguments.size() + start) { // if the current consumed count is unlimited or exceed all of our arguments
                    return solver.suggest(arguments.subList(start, arguments.size())); // we are ready to suggest!
                } else {
                    start += consumed; // otherwise we go on until we reach the last parameter
                }
            }
        }
        return Collections.emptyList();
    }

    public static List<Command> load(Object residence) {
        List<Command> result = new ArrayList<>();

        for (Class<?> inner : residence.getClass().getDeclaredClasses()) {
            if (Command.class.isAssignableFrom(inner)) {
                try {
                    Constructor<?> ctr = inner.getDeclaredConstructor();
                    ctr.setAccessible(true);
                    result.add((Command) ctr.newInstance());
                } catch (Exception ignored) {
                }
            }
        }

        for (Method function : residence.getClass().getDeclaredMethods()) {
            AsCommand annotation = function.getDeclaredAnnotation(AsCommand.class);
            if (annotation != null) { // if it is a command function
                result.add(new FunctionalCommand(
                        function.getName(), // todo at the moment the command name is the name of the function
                        residence,
                        function // we need also this to load function parameters
                ));
            }
        }
        return result;
    }

    public static void inject(NodeCommand node, Object residence) {
        node.append(load(residence));
    }

    public static void configure(Config cfg) {
        noPermissionOnParameterMessage = cfg.getMessage("no-permission-on-parameter");
        invalidUsageMessage = cfg.getMessage("invalid-usage");
        invalidArgumentTypeMessage = cfg.getMessage("invalid-argument-type");
    }
}
