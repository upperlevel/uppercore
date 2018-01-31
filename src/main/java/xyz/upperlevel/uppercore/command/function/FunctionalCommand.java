package xyz.upperlevel.uppercore.command.function;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.function.parameter.FunctionalParameter;
import xyz.upperlevel.uppercore.command.function.parameter.ParameterParseException;
import xyz.upperlevel.uppercore.command.function.parameter.ParameterAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class FunctionalCommand extends Command {
    @Getter
    private Method function;

    @Getter
    private FunctionalParameter[] parameters;

    public FunctionalCommand(String name, Method function) {
        super(name);
        WithCommand command = function.getAnnotation(WithCommand.class);
        if (command == null) {
            throw new IllegalArgumentException("@FunctionalCommand not found above function: " + function.getName());
        }
        WithPermission permission = function.getAnnotation(WithPermission.class);
        if (permission != null) {
            setPermissionPortion(new Permission(permission.value(), permission.description(), permission.defaultUser().get(this)));
            setPermissionCompleter(permission.completer());
        }
        this.function = function;
        this.parameters = new FunctionalParameter[function.getParameterCount() - 1];
        for (int i = 0; i < function.getParameterCount() - 1; i++) {
            this.parameters[i] = new FunctionalParameter(function.getParameters()[i], , this);
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
    protected boolean onCall(CommandSender sender, List<String> arguments) {
        List<Object> objects = new ArrayList<>();
        objects.add(sender); // The first parameter MUST be always the CommandSender
        int currArgIndex = 0;
        for (FunctionalParameter parameter : parameters) {
            ParameterAdapter solver = parameter.getSolver();
            if (!sender.hasPermission(parameter.getPermission())) {
                if (parameter.isOptional()) { // if the parameter is optional we add its default value
                    objects.add(parameter.getDefaultValue());
                } else { // no, the parameter is not optional we need to throw an error
                    throw new IllegalStateException("You do not have enough permissions to use the parameter " + parameter.getName());
                }
            } else { // if the sender has enough permissions to execute this parameter
                if (currArgIndex >= arguments.size()) { // if we already used all of our arguments
                    if (parameter.isOptional()) { // if the next parameter is optional
                        objects.add(parameter.getDefaultValue()); // We use its default value
                    } else {
                        // otherwise we throw an illegal arguments count exception
                        throw new IllegalArgumentException("Not enough arguments typed to run this command!"); // TODO
                    }
                } else { // if we have other arguments to use
                    int consumed = solver.getConsumeCount();
                    if (consumed < 0) {
                        consumed = arguments.size() - currArgIndex;
                    }
                    try {
                        objects.add(solver.adapt(arguments.subList(currArgIndex, currArgIndex + consumed)));
                    } catch (ParameterParseException exception) {
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
            ParameterAdapter solver = parameter.getSolver();
            if (start >= arguments.size()) {
                return solver.suggest(Collections.emptyList());
            } else {
                int consumed = solver.getConsumeCount();
                if (consumed < 0 || consumed >= arguments.size() + start) { // if the current consumed count is unlimited or exceed all of our arguments
                    return solver.suggest(arguments.subList(start, arguments.size())); // we are ready to suggest!
                } else {
                    start += consumed; // otherwise we go on until we reach the last parameter
                }
            }
        }
        return Collections.emptyList();
    }
}
