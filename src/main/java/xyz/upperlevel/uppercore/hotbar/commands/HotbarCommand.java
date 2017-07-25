package xyz.upperlevel.uppercore.gui.hotbar.commands;

import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.gui.hotbar.commands.GiveHotbarCommand;
import xyz.upperlevel.uppercore.gui.hotbar.commands.HotbarListCommand;
import xyz.upperlevel.uppercore.gui.hotbar.commands.RemoveHotbarCommand;

public class HotbarCommand extends NodeCommand {

    public HotbarCommand() {
        super("hotbar");
        setDescription("Commands for hotbars.");

        register(new GiveHotbarCommand());
        register(new RemoveHotbarCommand());
        register(new HotbarListCommand());
    }
}
