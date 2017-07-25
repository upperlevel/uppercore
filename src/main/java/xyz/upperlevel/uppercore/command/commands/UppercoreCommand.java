package xyz.upperlevel.uppercore.command.commands;

import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.gui.commands.GuiCommand;
import xyz.upperlevel.uppercore.hotbar.commands.HotbarCommand;
import xyz.upperlevel.uppercore.board.commands.BoardCommand;
import xyz.upperlevel.uppercore.script.commands.ScriptCommand;
import xyz.upperlevel.uppercore.sound.command.PlaySoundCommand;

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
    }
}
