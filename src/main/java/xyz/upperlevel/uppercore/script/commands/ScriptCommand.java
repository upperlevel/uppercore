package xyz.upperlevel.uppercore.script.commands;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import xyz.upperlevel.uppercore.command.DefaultPermission;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.WithChildPermission;
import xyz.upperlevel.uppercore.command.WithPermission;

@WithPermission(value = "script", def = DefaultPermission.OP)
@WithChildPermission(desc = "Gives access to all script-related commands")
public class ScriptCommand extends NodeCommand {

    public ScriptCommand() {
        super("script");
        setDescription("Commands for scripts.");

        register(new ScriptInfoCommand());
        register(new ScriptListCommand());
    }
}
