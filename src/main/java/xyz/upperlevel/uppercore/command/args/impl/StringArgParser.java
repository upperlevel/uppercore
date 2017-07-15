package xyz.upperlevel.uppercore.command.args.impl;

import xyz.upperlevel.uppercore.command.args.ArgumentParser;
import xyz.upperlevel.uppercore.command.args.ArgumentParserManager;
import xyz.upperlevel.uppercore.command.args.exceptions.ParseException;

import java.util.Iterator;
import java.util.List;

import static java.util.Collections.singletonList;

public class StringArgParser implements ArgumentParser {

    @Override
    public List<Class<?>> getParsable() {
        return singletonList(String.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(ArgumentParserManager handle, Class<?> type, List<String> args) throws ParseException {
        return args.get(0);
    }
}
