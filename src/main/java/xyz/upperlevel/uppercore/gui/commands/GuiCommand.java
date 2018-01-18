package xyz.upperlevel.uppercore.gui.commands;

import xyz.upperlevel.uppercore.command.DefaultPermission;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.function.WithPermission;

@WithPermission(value = "gui", defaultPermission = DefaultPermission.OP)
public class GuiCommand extends NodeCommand {

    public GuiCommand() {
        super("gui");
        setDescription("Commands for guis.");

        register(new OpenGuiCommand());
        register(new CloseGuiCommand());
        register(new GuiListCommand());
    }
}
