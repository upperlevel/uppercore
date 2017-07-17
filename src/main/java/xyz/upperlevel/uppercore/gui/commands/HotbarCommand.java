package xyz.upperlevel.uppercore.gui.commands;

import xyz.upperlevel.uppercore.command.NodeCommand;

public class HotbarCommand extends NodeCommand {

    public HotbarCommand() {
        super("hotbar");
        setDescription("Commands for hotbars.");

        register(new GiveHotbarCommand());
        register(new RemoveHotbarCommand());
        register(new HotbarListCommand());
    }
}
