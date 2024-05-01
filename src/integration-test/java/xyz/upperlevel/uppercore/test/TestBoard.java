package xyz.upperlevel.uppercore.test;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.upperlevel.uppercore.board.Board;
import xyz.upperlevel.uppercore.board.BoardModel;
import xyz.upperlevel.uppercore.board.SimpleBoardModel;
import xyz.upperlevel.uppercore.command.functional.AsCommand;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

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
        public void apply(Board board, Player player, PlaceholderRegistry<?> placeholders) {
            int startAtSlice = (int) ((System.currentTimeMillis() / 1000) % RAINBOW.length);
            board.setTitle(LIGHT_PURPLE + "Rainbow");
            board.setLines(generateRainbow(startAtSlice));
        }
    }

    @Override
    public void apply(Board board, Player player, PlaceholderRegistry<?> placeholders) {
        board.setTitle(YELLOW + "Test board");
        board.setLines(Arrays.asList(
                "",
                BLUE + player.getName(),
                RED + "" + player.getHealth(),
                "",
                // 168 characters line, the maximum reachable.
                YELLOW + "===============================================================|======================" +
                        "=================|===============================================================|",
                ""
        ));
    }

    @AsCommand
    public void board(Player player) {
        this.hook(new Board()).open(player, PlaceholderRegistry.def());
        player.sendMessage(GREEN + "Board opened.");
    }

    @AsCommand
    public void configBoard(Player player) {
        this.hook(new Board()).open(player, PlaceholderRegistry.def());
        player.sendMessage(GREEN + "Config board opened.");
    }

    @AsCommand
    public void rainbowBoard(Player player) {
        BoardModel.Hook hooked = this.hook(new Board());
        hooked.open(player, PlaceholderRegistry.def());
        new BukkitRunnable() {
            @Override
            public void run() {
                hooked.render(player, PlaceholderRegistry.def());
            }
        }.runTaskTimer(UppercoreTest.get(), 1, 20);

        player.sendMessage(GREEN + "Board opened! Have fun! A never ending task has been created.");
    }

    public static void loadConfig() {
        CONFIG_BOARD = Config.from(UppercoreTest.get().getConfig()).get("board", SimpleBoardModel.class);
    }
}
