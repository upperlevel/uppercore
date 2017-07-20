package xyz.upperlevel.uppercore.script.commands;

import xyz.upperlevel.uppercore.command.NodeCommand;

public class ScriptCommand extends NodeCommand {

    public ScriptCommand() {
        super("script");
        setDescription("Commands for scripts.");

        register(new ScriptInfoCommand());
        register(new ScriptListCommand());
    }
}
