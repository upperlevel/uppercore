package xyz.upperlevel.uppercore.hotbar.commands;

import xyz.upperlevel.uppercore.command.DefaultPermission;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.function.WithPermission;

@WithPermission(value = "hotbar", defaultPermission = DefaultPermission.OP)
public class HotbarCommand extends NodeCommand {

    public HotbarCommand() {
        super("hotbar");
        setDescription("Commands for hotbars.");

        register(new GiveHotbarCommand());
        register(new RemoveHotbarCommand());
        register(new HotbarListCommand());
    }
}
