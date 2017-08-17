package xyz.upperlevel.uppercore.hotbar.commands;

import xyz.upperlevel.uppercore.command.DefaultPermission;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.WithChildPermission;
import xyz.upperlevel.uppercore.command.WithPermission;
import xyz.upperlevel.uppercore.hotbar.commands.GiveHotbarCommand;
import xyz.upperlevel.uppercore.hotbar.commands.HotbarListCommand;
import xyz.upperlevel.uppercore.hotbar.commands.RemoveHotbarCommand;

@WithPermission(value = "hotbar", def = DefaultPermission.OP)
@WithChildPermission(desc = "Allows you to perform all hotbar-related commands")
public class HotbarCommand extends NodeCommand {

    public HotbarCommand() {
        super("hotbar");
        setDescription("Commands for hotbars.");

        register(new GiveHotbarCommand());
        register(new RemoveHotbarCommand());
        register(new HotbarListCommand());
    }
}
