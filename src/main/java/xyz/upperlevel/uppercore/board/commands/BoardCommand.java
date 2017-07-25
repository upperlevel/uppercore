package xyz.upperlevel.uppercore.board.commands;

import xyz.upperlevel.uppercore.command.NodeCommand;

public class BoardCommand extends NodeCommand {

    public BoardCommand() {
        super("board");
        setDescription("Commands for scoreboards.");

        register(new BoardListCommand());
        register(new ScoreboardRemoveCommand());
        register(new ScoreboardSetCommand());
    }
}
