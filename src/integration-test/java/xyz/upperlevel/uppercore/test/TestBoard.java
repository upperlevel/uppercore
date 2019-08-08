package xyz.upperlevel.uppercore.test;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.board.Board;
import xyz.upperlevel.uppercore.board.BoardHolder;
import xyz.upperlevel.uppercore.board.BoardModel;
import xyz.upperlevel.uppercore.board.SimpleBoardModel;
import xyz.upperlevel.uppercore.command.functional.AsCommand;
import xyz.upperlevel.uppercore.config.Config;

import java.util.Arrays;

import static org.bukkit.ChatColor.*;

public class TestBoard implements BoardModel {
    public static BoardModel CONFIG_BOARD;

    @Override
    public void apply(Board board, Player reference) {
        board.setTitle(YELLOW + "Test board");
        board.setLines(Arrays.asList(
                "",
                BLUE + reference.getName(),
                RED + "" + reference.getHealth(),
                "",
                // 168 characters line, the maximum reachable.
                YELLOW + "===============================================================|======================" +
                        "=================|===============================================================|",
                ""
        ));
    }

    @AsCommand
    public void board(Player player) {
        new BoardHolder(player, this).open();
        player.sendMessage(GREEN + "Board opened.");
    }

    @AsCommand
    public void configBoard(Player player) {
        new BoardHolder(player, CONFIG_BOARD).open();
        player.sendMessage(GREEN + "Config board opened.");
    }

    public static void loadConfig() {
        CONFIG_BOARD = Config.from(UppercoreTest.get().getConfig()).get("board", SimpleBoardModel.class);
    }
}
