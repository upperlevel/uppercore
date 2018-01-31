package xyz.upperlevel.uppercore.script.commands;

import xyz.upperlevel.uppercore.command.DefaultPermissionUser;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.function.WithPermission;

@WithPermission(value = "script", defaultUser = DefaultPermissionUser.OP)
public class ScriptCommand extends NodeCommand {

    public ScriptCommand() {
        super("script");
        setDescription("Commands for scripts.");

        register(new ScriptInfoCommand());
        register(new ScriptListCommand());
    }
}
