package xyz.upperlevel.uppercore.command.argument;

import xyz.upperlevel.uppercore.command.argument.exceptions.ParseException;

import java.util.List;

public interface ArgumentParser {

    List<Class<?>> getParsable();

    default boolean isParsable(Class<?> type) {
        return getParsable().contains(type);
    }

    int getArgumentsCount();

    Object parse(Class<?> type, List<String> args) throws ParseException;
}
