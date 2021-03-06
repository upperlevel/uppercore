package xyz.upperlevel.uppercore.command;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public enum SenderType {
    ALL {
        @Override
        public boolean match(CommandSender sender) {
            return true;
        }
    },
    CONSOLE {
        @Override
        public boolean match(CommandSender sender) {
            return sender instanceof ConsoleCommandSender;
        }
    },
    BLOCK {
        @Override
        public boolean match(CommandSender sender) {
            return sender instanceof BlockCommandSender;
        }
    },
    PLAYER {
        @Override
        public boolean match(CommandSender sender) {
            return sender instanceof Player;
        }
    };

    public abstract boolean match(CommandSender sender);
}
