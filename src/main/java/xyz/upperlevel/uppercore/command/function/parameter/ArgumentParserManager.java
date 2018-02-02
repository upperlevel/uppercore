package xyz.upperlevel.uppercore.command.function.parameter;

import java.util.HashMap;
import java.util.Map;

public class ArgumentParserManager {
    private Map<Class<?>, ArgumentParser> parsers = new HashMap<>();

    public void addParser(ArgumentParser parser) {
        for (Class<?> type : parser.getParsableTypes()) {
            parsers.put(type, parser);
        }
    }

    public ArgumentParser getParser(Class<?> type) {
        return parsers.get(type);
    }
}
