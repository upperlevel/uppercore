package xyz.upperlevel.uppercore.game.arena.commands;

import xyz.upperlevel.uppercore.command.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ArenaCommandSuite {
    private ArenaCommandSuite() {
    }

    public static ArenaCreateCommand create() {
        return new ArenaCreateCommand();
    }

    public static ArenaDestroyCommand destroy() {
        return new ArenaDestroyCommand();
    }

    public static ArenaJoinCommand join() {
        return new ArenaJoinCommand();
    }

    public static ArenaLeaveCommand leave() {
        return new ArenaLeaveCommand();
    }

    public static ArenaAboutCommand about() {
        return new ArenaAboutCommand();
    }

    public static ArenaListCommand list() {
        return new ArenaListCommand();
    }

    public static List<Command> all() {
        /*
        List<Command> result = new ArrayList<>();
        result.add(create());
        result.add(destroy());
        result.add(join());
        result.add()
        */
        return Collections.emptyList(); // Todo!!!!
    }
}
