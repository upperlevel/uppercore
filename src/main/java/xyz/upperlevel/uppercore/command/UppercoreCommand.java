package xyz.upperlevel.uppercore.command;

import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.commands.GuiCommand;
import xyz.upperlevel.uppercore.gui.commands.HotbarCommand;

public class UppercoreCommand extends NodeCommand {

    public UppercoreCommand() {
        super("uppercore");
        addAlias("ucore");
        setDescription("Commands for " + Uppercore.get().getName() + ".");

        register(new GuiCommand());
        register(new HotbarCommand());
    }
}
