package xyz.upperlevel.uppercore.command.arguments.impl;

import xyz.upperlevel.uppercore.command.arguments.ArgumentParser;
import xyz.upperlevel.uppercore.command.arguments.ArgumentParserManager;
import xyz.upperlevel.uppercore.command.arguments.exceptions.ParseException;
import xyz.upperlevel.uppercore.command.arguments.exceptions.UnparsableTypeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.reflect.Array.newInstance;

public class Array1dArgumentParser implements ArgumentParser {

    @Override
    public List<Class<?>> getParsable() {
        return Collections.singletonList(String[].class);
    }

    @Override
    public int getArgumentsCount() {
        return -1;
    }

    @Override
    public Object parse(Class<?> type, List<String> args) throws ParseException {
        Class<?> comp = type.getComponentType();

        if (comp == null || comp.getComponentType() != null)
            throw new UnparsableTypeException("Unparsable type \"" + type.getName() + "\"");

        List<Object> result = new ArrayList<>();
        for (int i = 0; i < args.size();) {
            int used = ArgumentParserManager.getArgumentsCount(comp);
            result.add(ArgumentParserManager.parse(comp, args.subList(i, i + used)));
            i += used;
        }
        return result.toArray((Object[]) newInstance(comp, result.size()));
    }
}
