package xyz.upperlevel.uppercore.command.functional.parser;

import xyz.upperlevel.uppercore.command.functional.parser.def.*;

import java.util.*;

public class ArgumentParserManager {
    public static List<ArgumentParser> defParsers = new ArrayList<>(Arrays.asList(
            new ColorArgumentParser(),
            new EnchantmentArgumentParser(),
            new MaterialArgumentParser(),
            new SoundArgumentParser(),
            new VectorArgumentParser()
    ));

    private Map<Class<?>, ArgumentParser> parsers = new HashMap<>();

    static {
        defParsers.addAll(FunctionalArgumentParser.load(new PrimitiveArgumentParsers()));
    }

    public ArgumentParserManager() {
        for (ArgumentParser parser : defParsers) {
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
