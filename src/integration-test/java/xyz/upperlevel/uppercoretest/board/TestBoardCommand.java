package xyz.upperlevel.uppercoretest.board;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.board.Board;
import xyz.upperlevel.uppercore.board.BoardManager;
import xyz.upperlevel.uppercore.board.SimpleConfigBoard;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.functional.AsCommand;
import xyz.upperlevel.uppercore.command.functional.FunctionalCommand;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

import java.io.InputStreamReader;
import java.util.Arrays;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.YELLOW;

public class TestBoardCommand extends NodeCommand {
    private static Board SIMPLE_BOARD;
    private static Board DYNAMIC_BOARD;
    private static Board CONFIG_SIMPLE_BOARD;
    private static Board CONFIG_COMPLEX_BOARD;

    public TestBoardCommand() {
        super("testboard");
        append(FunctionalCommand.load(this));
    }

    @AsCommand(aliases = "s")
    public void simple(CommandSender sender) {
        BoardManager.open((Player) sender, SIMPLE_BOARD, PlaceholderRegistry.create());
        sender.sendMessage("Simple board opened");
    }

    @AsCommand(aliases = {"dyn", "d"})
    public void dynamic(CommandSender sender) {
        BoardManager.open((Player) sender, DYNAMIC_BOARD, PlaceholderRegistry.create());
        sender.sendMessage("Dynamic board opened");
    }

    @AsCommand(aliases = {"configs", "cfgs"})
    public void configSimple(CommandSender sender) {
        BoardManager.open((Player) sender, CONFIG_SIMPLE_BOARD, PlaceholderRegistry.create());
        sender.sendMessage("Config simple board opened");
    }

    @AsCommand(aliases = {"configc", "cfgc"})
    public void configComplex(CommandSender sender) {
        BoardManager.open((Player) sender, CONFIG_COMPLEX_BOARD, PlaceholderRegistry.create());
        sender.sendMessage("Config complex board opened");
    }

    public static void loadBoards() {
        SIMPLE_BOARD =
                Board.simple(
                        YELLOW + "" + BOLD + "Simple Board",
                        Arrays.asList(
                                "",
                                "A simple board",
                                "will never change.",
                                ""
                        ));

        DYNAMIC_BOARD =
                new DynamicBoardExample();

        CONFIG_SIMPLE_BOARD =
                SimpleConfigBoard.create(Config.wrap(YamlConfiguration.loadConfiguration(new InputStreamReader(
                        TestBoardCommand.class.getResourceAsStream("/boards/simple.yml"))
                )));

        CONFIG_COMPLEX_BOARD = new ComplexConfigBoardExample(
                Config.wrap(YamlConfiguration.loadConfiguration(new InputStreamReader(
                        TestBoardCommand.class.getResourceAsStream("/boards/complex.yml"))
                )));
    }
}
