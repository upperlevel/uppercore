package xyz.upperlevel.uppercore.gui.link.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.gui.link.Link;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

public class ConsoleCommandLink implements Link {

    private final PlaceholderValue<String> command;

    public ConsoleCommandLink(PlaceholderValue<String> command) {
        this.command = command;
    }

    public ConsoleCommandLink(String command) {
        this(PlaceholderValue.stringValue(command));
    }

    @Override
    public void run(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.resolve(player));
    }
}
