package xyz.upperlevel.uppercore.command;

import java.util.List;

public interface CommandLoader<T> {

    List<Command> load(T something);
}
