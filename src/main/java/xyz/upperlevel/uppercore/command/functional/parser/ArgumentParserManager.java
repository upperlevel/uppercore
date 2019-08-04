package xyz.upperlevel.uppercore.command.functional.parser;

import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.arena.parsers.ArenaArgumentParser;
import xyz.upperlevel.uppercore.command.functional.parser.def.*;

import java.util.*;

public final class ArgumentParserManager {
    private static Map<Class<?>, ArgumentParser> parsers = new HashMap<>();

    /**
     * Registers some default argument parsers.
     */
    public static void init() {
        register(new ArenaArgumentParser());
        register(FunctionalArgumentParser.load(new PrimitiveArgumentParsers()));
        register(Arrays.asList(
                new ColorArgumentParser(),
                new EnchantmentArgumentParser(),
                new MaterialArgumentParser(),
                new SoundArgumentParser(),
                new VectorArgumentParser()
        ));
        Uppercore.logger().info("Argument parsers registered!");
    }

    /**
     * Registers a new argument parser.
     */
    public static void register(ArgumentParser parser) {
        for (Class<?> type : parser.getParsableTypes()) {
            parsers.put(type, parser);
        }
    }

    /**
     * Registers a list of argument parsers.
     */
    public static void register(List<ArgumentParser> parsers) {
        parsers.forEach(ArgumentParserManager::register);
    }

    /**
     * Gets a registered argument parsers able to parse the given type.
     */
    public static ArgumentParser get(Class<?> type) {
        return parsers.get(type);
    }

    private ArgumentParserManager() {
    }
}
