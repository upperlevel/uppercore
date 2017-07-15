package xyz.upperlevel.uppercore.command.args;

import xyz.upperlevel.uppercore.command.args.exceptions.ParseException;

import java.util.List;

public interface ArgumentParser {

    List<Class<?>> getParsable();

    default boolean isParsable(Class<?> type) {
        return getParsable().contains(type);
    }

    int getArgumentsCount();

    Object parse(ArgumentParserManager handle, Class<?> type, List<String> args) throws ParseException;
}
