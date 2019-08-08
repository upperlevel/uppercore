package xyz.upperlevel.uppercore.test;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.upperlevel.uppercore.board.Board;
import xyz.upperlevel.uppercore.board.BoardContainer;
import xyz.upperlevel.uppercore.board.BoardModel;
import xyz.upperlevel.uppercore.board.SimpleBoardModel;
import xyz.upperlevel.uppercore.command.functional.AsCommand;
import xyz.upperlevel.uppercore.config.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.ChatColor.*;

public class TestBoard implements BoardModel {
    public static BoardModel CONFIG_BOARD;

    public static class RainbowBoard implements BoardModel {
        private static final int RAINBOW_LINE_CHARACTERS = 70;
        private static final int RAINBOW_LINES = 5;

        private static final ChatColor[] RAINBOW = {
                RED,
                DARK_RED,
                GOLD,
                YELLOW,
                GREEN,
                DARK_GREEN,
                AQUA,
                DARK_AQUA,
                BLUE,
                DARK_BLUE,
                LIGHT_PURPLE,
                DARK_PURPLE
        };

        private List<String> generateRainbow(int startAtSlice) {
            int charsPerSlice = RAINBOW_LINE_CHARACTERS / RAINBOW.length;

            List<String> lines = new ArrayList<>();
            for (int j = 0; j < RAINBOW_LINES; j++) {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < RAINBOW.length; i++) {
                    ChatColor color = RAINBOW[(startAtSlice + i) % RAINBOW.length];
                    String slice = StringUtils.repeat("=", charsPerSlice);
                    line.append(color).append(slice);
                }
                lines.add(line.toString());
            }

            return lines;
        }

        @Override
        public void apply(Board board, Player reference) {
            int startAtSlice = (int) ((System.currentTimeMillis() / 1000) % RAINBOW.length);
            board.setTitle(LIGHT_PURPLE + "Rainbow");
            board.setLines(generateRainbow(startAtSlice));
        }
    }

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
        new BoardContainer(this).open(player);
        player.sendMessage(GREEN + "Board opened.");
    }

    @AsCommand
    public void configBoard(Player player) {
        new BoardContainer(CONFIG_BOARD).open(player);
        player.sendMessage(GREEN + "Config board opened.");
    }

    @AsCommand
    public void rainbowBoard(Player player) {
        BoardContainer board = new BoardContainer(new RainbowBoard());
        board.open(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                board.update(player);
            }
        }.runTaskTimer(UppercoreTest.get(), 1, 20);

        player.sendMessage(GREEN + "Board opened! Have fun! A never ending task has been created.");
    }

    public static void loadConfig() {
        CONFIG_BOARD = Config.from(UppercoreTest.get().getConfig()).get("board", SimpleBoardModel.class);
    }
}
