package xyz.upperlevel.uppercore.command.functional;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.command.functional.parameter.ParameterHandler;
import xyz.upperlevel.uppercore.command.functional.parameter.ParameterParseException;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.message.Message;
import xyz.upperlevel.uppercore.util.Dbg;

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
        function.setAccessible(true);

        AsCommand command = function.getAnnotation(AsCommand.class);
        if (command == null) {
            throw new IllegalArgumentException("@AsCommand not found above function: " + function.getName());
        }
        if (!command.description().isEmpty()) {
            setDescription(command.description());
        }
        setAliases(new HashSet<>(Arrays.asList(command.aliases())));
        setSenderType(command.sender());

        WithPermission wPerm = function.getAnnotation(WithPermission.class);
        if (wPerm == null) { // If permission is null always put the most restrictive permission for security reasons.
            setPermissionPortion(new Permission(getName(), "", PermissionUser.OP.get()));
            setPermissionCompleter(PermissionCompleter.INHERIT);
        } else {
            String permName = wPerm.value().isEmpty() ? getName() : wPerm.value();
            setPermissionPortion(new Permission(permName, wPerm.description(), wPerm.user().get()));
            setPermissionCompleter(wPerm.completer());
        }

        if (function.getParameterCount() == 0) {
            throw new IllegalArgumentException("'" + function.getName() + "' command function has 0 parameters. A command function should have at least one.");
        }

        this.firstParameter = function.getParameters()[0];

        // If the first parameter is Player, forces SenderType check.
        if (firstParameter.getType() == Player.class) {
            setSenderType(SenderType.PLAYER);
        }

        this.parameters = new FunctionalParameter[function.getParameterCount() - 1];
        for (int i = 0; i < function.getParameterCount() - 1; i++) {
            Parameter parameter = function.getParameters()[i + 1];
            this.parameters[i] = new FunctionalParameter(this, parameter, parameter.getType());
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
    protected boolean onCall(CommandSender sender, Queue<String> args) {
        List<Object> objects = new ArrayList<>();

        Class<?> type = firstParameter.getType();
        if (CommandSender.class == type) {
            objects.add(sender);
        } else if (CommandContext.class == type) {
            objects.add(new CommandContext(sender, this));
        } else if (Player.class == type) {
            objects.add(sender);
        } else {
            throw new IllegalArgumentException(
                    function.getName() + " has an unsupported first parameter: " + firstParameter.getType().getSimpleName()
            );
        }

        for (int i = 0; i < parameters.length; i++) {
            FunctionalParameter parameter = parameters[i];
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
                if (args.isEmpty()) { // if we already used all of our arguments
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
                    try {
                        objects.add(ParameterHandler.parse(parameter.getType(), args));
                    } catch (ParameterParseException e) {
                        invalidArgumentTypeMessage.send(sender, PlaceholderRegistry.create()
                                .set("parameter", parameter.getName())
                                .set("parameter_type", parameter.getOriginal().getType().getSimpleName().toLowerCase(Locale.ENGLISH))
                                .set("parameter_index", i)
                                .set("wrong_argument", StringUtils.join(e.getArgs(), " "))
                        );
                        return false;
                    }
                }
            }
        }
        try {
            Dbg.pf("Invoking %s%s", function.getName(), objects.subList(1, objects.size()).toString());
            function.invoke(residence, objects.toArray());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(residence.getClass().getSimpleName() + "." + function.getName() + " isn't reachable.");
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("The command " + getClass().getName() + " thrown an exception", e);
        }
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, Queue<String> args) {
        if (args.isEmpty()) {
            return Collections.emptyList();
        }
        for (FunctionalParameter param : parameters) {
            if (!param.hasPermission(sender)) {
                return Collections.emptyList();
            }
            List<String> suggestions = ParameterHandler.suggest(param.getType(), args);
            if (!suggestions.isEmpty()) {
                return suggestions;
            }
            ParameterHandler.skip(param.getType(), args);
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
