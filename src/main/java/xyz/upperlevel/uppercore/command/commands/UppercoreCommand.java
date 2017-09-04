package xyz.upperlevel.uppercore.command.commands;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.WithChildPermission;
import xyz.upperlevel.uppercore.command.WithPermission;
import xyz.upperlevel.uppercore.gui.commands.GuiCommand;
import xyz.upperlevel.uppercore.hotbar.commands.HotbarCommand;
import xyz.upperlevel.uppercore.board.commands.BoardCommand;
import xyz.upperlevel.uppercore.script.commands.ScriptCommand;
import xyz.upperlevel.uppercore.sound.command.PlaySoundCommand;

@WithPermission("uppercore")
@WithChildPermission(desc = "Gives access to all uppercore commands")
public class UppercoreCommand extends NodeCommand {

    public UppercoreCommand() {
        super("uppercore");
        addAlias("ucore");
        setDescription("Commands for " + Uppercore.get().getName() + ".");

        register(new GuiCommand());
        register(new HotbarCommand());
        register(new BoardCommand());
        register(new ScriptCommand());

        register(new PlaySoundCommand());

        register(Uppercore.get().getUpdater().getCommand());
    }
}
