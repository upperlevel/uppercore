package xyz.upperlevel.uppercore.board.commands;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.board.BoardId;
import xyz.upperlevel.uppercore.command.*;

import static org.bukkit.ChatColor.GREEN;

@WithPermission(value = "set", desc = "Allows you to change the current board")
public class BoardSetCommand extends Command {

    public BoardSetCommand() {
        super("set");
        setDescription("Sets board to a player.");
    }

    @Executor
    public void run(CommandSender sender, @Argument("board") BoardId board, @Argument("player") @Optional Player player) {
        if (player == null)
            player = (Player) sender;
        sender.sendMessage(GREEN + "Board \"" + board.getGlobalId() + "\" set to: \"" + player.getName() + "\".");
    }
}
