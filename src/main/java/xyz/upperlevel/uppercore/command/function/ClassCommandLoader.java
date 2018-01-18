package xyz.upperlevel.uppercore.command.function;

import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.CommandLoader;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ClassCommandLoader implements CommandLoader<Class<?>> {
    @Override
    public List<Command> load(Class<?> something) {
        List<Command> commands = new ArrayList<>();
        for (Method method : something.getMethods()) {
            WithCommand annotation = method.getAnnotation(WithCommand.class);
            if (annotation != null) {
                // The it is a command method
                commands.add(new FunctionCommand(method.getName(), method));
            }
        }
        return commands;
    }
}
