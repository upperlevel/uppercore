package xyz.upperlevel.uppercore.command;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public enum Sender {

    ALL {
        @Override
        public boolean isCorrect(CommandSender sender) {
            return true;
        }
    },
    CONSOLE {
        @Override
        public boolean isCorrect(CommandSender sender) {
            return sender instanceof ConsoleCommandSender;
        }
    },
    BLOCK {
        @Override
        public boolean isCorrect(CommandSender sender) {
            return sender instanceof BlockCommandSender;
        }
    },
    PLAYER {
        @Override
        public boolean isCorrect(CommandSender sender) {
            return sender instanceof Player;
        }
    };

    public abstract boolean isCorrect(CommandSender sender);
}
