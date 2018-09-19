package xyz.upperlevel.uppercoretest;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.command.CommandRegistry;
import xyz.upperlevel.uppercore.gui.Gui;
import xyz.upperlevel.uppercore.hotbar.Hotbar;
import xyz.upperlevel.uppercore.registry.Registry;
import xyz.upperlevel.uppercore.storage.Database;
import xyz.upperlevel.uppercore.storage.StorageConnector;
import xyz.upperlevel.uppercoretest.board.TestBoardCommand;
import xyz.upperlevel.uppercoretest.command.TestCommand;
import xyz.upperlevel.uppercoretest.command.functional.TestFunctionalNodeCommand;
import xyz.upperlevel.uppercoretest.gui.GuiCommand;
import xyz.upperlevel.uppercoretest.gui.HotbarCommand;

import java.util.logging.Logger;

public class UppercoreTest extends JavaPlugin {
    private static UppercoreTest instance;
    private Logger logger;
    private Registry<?> root;

    @Getter
    private Database database;

    public UppercoreTest() {
        instance = this;
    }

    @Override
    public void onEnable() {
        logger = getLogger();
        root = Uppercore.registry().register(this);

        CommandRegistry commandRegistry = CommandRegistry.create(this);

        // Board
        TestBoardCommand.loadBoards();
        commandRegistry.register(new TestBoardCommand());

        // Commands
        commandRegistry.register(new TestCommand());
        commandRegistry.register(new TestFunctionalNodeCommand());
        commandRegistry.register(new GuiCommand(root.registerChild("gui", Gui.class)));
        commandRegistry.register(new HotbarCommand(root.registerChild("hotbar", Hotbar.class)));
        commandRegistry.printMarkdown();
        getLogger().info("Commands registered. Markdown printed.");

        // Storage
        database = StorageConnector.read(this).database("uppercore_test");
    }

    @Override
    public void onDisable() {
    }

    public static UppercoreTest get() {
        return instance;
    }

    public static Logger logger() {
        return instance.logger;
    }

    public static Logger logger(String name) {
        return Logger.getLogger(instance.logger.getName() + "." + name);
    }

    public static Database database() {
        return instance.database;
    }
}
