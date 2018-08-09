package xyz.upperlevel.uppercore.command.functional.parser;

import java.util.HashMap;
import java.util.Map;

public class ArgumentParserManager {
    private Map<Class<?>, ArgumentParser> parsers = new HashMap<>();

    public ArgumentParserManager() {
        for (ArgumentParser parser : FunctionalArgumentParser.load(new PrimitiveArgumentParsers())) {
            addParser(parser);
        }
    }

    public void addParser(ArgumentParser parser) {
        for (Class<?> type : parser.getParsableTypes()) {
            parsers.put(type, parser);
        }
    }

    public ArgumentParser getParser(Class<?> type) {
        return parsers.get(type);
    }
}
