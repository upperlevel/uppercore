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
            WithCommand command = method.getAnnotation(WithCommand.class);
            if (command != null) {
                commands.add(new FunctionalCommand(method.getName(), method));
            }
        }
        return commands;
    }
}
