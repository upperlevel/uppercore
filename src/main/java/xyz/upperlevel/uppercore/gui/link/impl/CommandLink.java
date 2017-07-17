package xyz.upperlevel.uppercore.gui.link.impl;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.gui.link.Link;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

public class CommandLink implements Link {

    private final PlaceholderValue<String> command;

    public CommandLink(PlaceholderValue<String> command) {
        this.command = command;
    }

    public CommandLink(String command) {
        this(PlaceholderValue.stringValue(command));
    }

    @Override
    public void run(Player player) {
        player.performCommand(command.get(player));
    }
}
