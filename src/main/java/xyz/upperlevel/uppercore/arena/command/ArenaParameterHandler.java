package xyz.upperlevel.uppercore.arena.command;

import xyz.upperlevel.uppercore.arena.Arena;
import xyz.upperlevel.uppercore.arena.ArenaManager;
import xyz.upperlevel.uppercore.command.functional.parameter.ParameterHandler;

import java.util.Collections;
import java.util.stream.Collectors;

public final class ArenaParameterHandler {
    private ArenaParameterHandler() {
    }

    public static void register() {
        ParameterHandler.register(
                Collections.singletonList(Arena.class),
                args -> {
                    Arena arena = ArenaManager.get().get(args.take());
                    if (arena == null) {
                        throw args.areWrong();
                    }
                    return arena;
                },
                args -> {
                    if (args.remaining() > 1)
                        return Collections.emptyList();
                    String arenaName = args.take();
                    return ArenaManager.get().getArenas()
                            .stream()
                            .filter(arena -> arena.getId().startsWith(arenaName))
                            .map(Arena::getId)
                            .collect(Collectors.toList());
                });
    }
}
