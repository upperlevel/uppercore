package xyz.upperlevel.uppercore.command.functional.parser;

import java.util.List;

public interface ArgumentParser {
    Class<?>[] getParsableTypes();

    int getConsumedCount();

    Object parse(List<String> arguments) throws ArgumentParseException;

    List<String> suggest(List<String> arguments);
}
