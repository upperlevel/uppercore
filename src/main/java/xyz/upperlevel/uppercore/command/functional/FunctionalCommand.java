package xyz.upperlevel.uppercore.command.functional;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.functional.parameter.ArgumentParseException;
import xyz.upperlevel.uppercore.command.functional.parameter.ArgumentParser;
import xyz.upperlevel.uppercore.command.functional.parameter.ArgumentParserManager;
import xyz.upperlevel.uppercore.command.functional.parameter.FunctionalParameter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import static xyz.upperlevel.uppercore.Uppercore.logger;

public class FunctionalCommand extends Command {
    @Getter
    private Object residence; // where the command function is at

    @Getter
    private Method function;

    @Getter
    private FunctionalParameter[] parameters;

    public FunctionalCommand(String name, Object residence, Method function, ArgumentParserManager parserManager) {
        super(name);
        this.residence = residence;
        this.function = function;

        AsCommand command = function.getAnnotation(AsCommand.class);
        if (command == null) {
            throw new IllegalArgumentException("@FunctionalCommand not found above function: " + function.getName());
        }
        WithPermission permission = function.getAnnotation(WithPermission.class);
        if (permission != null) {
            setPermissionPortion(new Permission(permission.value(), permission.description(), permission.defaultUser().get(this)));
            setPermissionCompleter(permission.completer());
        }
        this.parameters = new FunctionalParameter[function.getParameterCount() - 1];
        for (int i = 0; i < function.getParameterCount() - 1; i++) {
            Parameter parameter = function.getParameters()[i + 1];
            ArgumentParser parser = parserManager.getParser(parameter.getType());
            if (parser != null) {
                this.parameters[i] = new FunctionalParameter(parameter, parser, this);
            } else {
                throw new IllegalArgumentException("No ArgumentParser found for parameter type: " + parameter.getType().getName());
            }
        }
    }

    @Override
    public void completePermission() {
        super.completePermission();
        for (FunctionalParameter parameter : parameters) {
            parameter.completePermission();
        }
    }

    @Override
    public void registerPermission(PluginManager pluginManager) {
        super.registerPermission(pluginManager);
        for (FunctionalParameter parameter : parameters) {
            parameter.registerPermission(pluginManager);
        }
    }

    @Override
    public String getUsage(CommandSender sender) {
        StringJoiner result = new StringJoiner(" ");
        for (FunctionalParameter parameter : parameters) {
            result.add(parameter.getName());
        }
        return result.toString();
    }

    @Override
    protected boolean onCall(CommandSender sender, List<String> args) {
        List<Object> objects = new ArrayList<>();
        objects.add(sender); // The first parameter MUST be always the CommandSender
        int currArgIndex = 0;
        for (FunctionalParameter parameter : parameters) {
            ArgumentParser parser = parameter.getParser();
            if (!parameter.hasPermission(sender)) {
                if (parameter.isOptional()) { // if the parameter is optional we add its default value
                    objects.add(parameter.getDefaultValue());
                } else { // no, the parameter is not optional we need to throw an error
                    throw new IllegalStateException("You do not have enough permissions to use the parameter: " + parameter);
                }
            } else { // if the sender has enough permissions to execute this parameter
                if (currArgIndex >= args.size()) { // if we already used all of our arguments
                    if (parameter.isOptional()) { // if the next parameter is optional
                        objects.add(parameter.getDefaultValue()); // We use its default value
                    } else {
                        // otherwise we throw an illegal arguments count exception
                        throw new IllegalArgumentException("Not enough arguments typed to run this command!"); // TODO
                    }
                } else { // if we have other arguments to use
                    int consumed = parser.getConsumedCount();
                    if (consumed < 0) {
                        consumed = args.size() - currArgIndex;
                    }
                    try {
                        objects.add(parser.parse(args.subList(currArgIndex, currArgIndex + consumed)));
                    } catch (ArgumentParseException exception) {
                        throw new IllegalArgumentException("An argument mismatch its original type: ", exception);
                    }
                    currArgIndex += consumed;
                }
            }
        }
        try {
            function.invoke(this, objects);
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
            if (!sender.hasPermission(parameter.getPermission())) { // if the sender has not enough permissions for this parameter
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

    /**
     * Loads a list of command from the class they are in.
     *
     * @param residence             the instance of the class from which it loads the command functions
     * @param argumentParserManager the object that associates an argument parser to each parameter
     * @return the list of commands loaded
     */
    public static List<Command> load(Object residence, ArgumentParserManager argumentParserManager) {
        List<Command> result = new ArrayList<>();
        for (Method function : residence.getClass().getMethods()) {
            AsCommand annotation = function.getAnnotation(AsCommand.class);
            if (annotation != null) { // if it is a command function
                result.add(new FunctionalCommand(
                        function.getName(), // todo at the moment the command name is the name of the function
                        residence,
                        function,
                        argumentParserManager // we need also this to load function parameters
                ));
            }
        }
        return result;
    }
}
