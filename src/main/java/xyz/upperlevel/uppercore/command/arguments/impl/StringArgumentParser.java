package xyz.upperlevel.uppercore.command.arguments.impl;

import xyz.upperlevel.uppercore.command.arguments.ArgumentParser;
import xyz.upperlevel.uppercore.command.arguments.exceptions.ParseException;

import java.util.List;

import static java.util.Collections.singletonList;

public class StringArgumentParser implements ArgumentParser {

    @Override
    public List<Class<?>> getParsable() {
        return singletonList(String.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(Class<?> type, List<String> args) throws ParseException {
        return args.get(0);
    }
}
