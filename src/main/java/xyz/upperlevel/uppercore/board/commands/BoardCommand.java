package xyz.upperlevel.uppercore.board.commands;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import xyz.upperlevel.uppercore.command.DefaultPermission;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.WithChildPermission;
import xyz.upperlevel.uppercore.command.WithPermission;

@WithPermission(value = "board", def = DefaultPermission.OP)
@WithChildPermission(desc = "Allows you to perform all board-related commands")
public class BoardCommand extends NodeCommand {

    public BoardCommand() {
        super("board");
        setDescription("Commands for scoreboards.");

        register(new BoardListCommand());
        register(new BoardRemoveCommand());
        register(new BoardSetCommand());
    }
}
