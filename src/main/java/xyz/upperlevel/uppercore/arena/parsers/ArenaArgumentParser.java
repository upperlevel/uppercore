package xyz.upperlevel.uppercore.arena.parsers;

import xyz.upperlevel.uppercore.arena.Arena;
import xyz.upperlevel.uppercore.arena.ArenaManager;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParseException;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParser;

import java.util.List;
import java.util.stream.Collectors;

public class ArenaArgumentParser implements ArgumentParser {
    @Override
    public Class<?>[] getParsableTypes() {
        return new Class[]{ Arena.class };
    }

    @Override
    public int getConsumedCount() {
        return 1;
    }

    @Override
    public Object parse(List<String> args) throws ArgumentParseException {
        Arena arena = ArenaManager.get().get(args.get(0));
        if (arena == null) {
            throw new ArgumentParseException(Arena.class, args);
        }
        return arena;
    }

    @Override
    public List<String> suggest(List<String> args) {
        return ArenaManager.get().getArenas()
                .stream()
                .filter(arena -> arena.getId().startsWith(args.get(0)))
                .map(Arena::getId)
                .collect(Collectors.toList());
    }
}
