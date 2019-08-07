package xyz.upperlevel.uppercore.test;

import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.functional.FunctionalCommand;

public class UppercoreTestCommand extends NodeCommand {
    public UppercoreTestCommand() {
        super("utest");

        FunctionalCommand.inject(this, new TestHotbar());
    }
}
