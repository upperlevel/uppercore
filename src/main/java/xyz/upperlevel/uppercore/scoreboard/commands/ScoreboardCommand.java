package xyz.upperlevel.uppercore.scoreboard.commands;

import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.NodeCommand;

public class ScoreboardCommand extends NodeCommand {

    public ScoreboardCommand() {
        super("scoreboard");
        setDescription("Commands for scoreboards.");

        register(new ScoreboardListCommand());
        register(new ScoreboardRemoveCommand());
        register(new ScoreboardSetCommand());
    }
}
