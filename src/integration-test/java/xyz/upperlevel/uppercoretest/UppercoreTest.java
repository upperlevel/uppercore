package xyz.upperlevel.uppercoretest;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.Gui;
import xyz.upperlevel.uppercore.hotbar.Hotbar;
import xyz.upperlevel.uppercore.registry.Registry;
import xyz.upperlevel.uppercore.storage.Database;
import xyz.upperlevel.uppercore.storage.StorageConnector;
import xyz.upperlevel.uppercore.storage.Storage;
import xyz.upperlevel.uppercoretest.command.TestCommand;
import xyz.upperlevel.uppercoretest.command.functional.TestFunctionalNodeCommand;
import xyz.upperlevel.uppercoretest.gui.GuiCommand;
import xyz.upperlevel.uppercoretest.gui.HotbarCommand;
import xyz.upperlevel.uppercoretest.storage.StorageCommands;

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

        new TestCommand().subscribe(this);
        new TestFunctionalNodeCommand().subscribe(this);

        new GuiCommand(root.registerChild("gui", Gui.class)).subscribe(this);

        new HotbarCommand(root.registerChild("hotbar", Hotbar.class)).subscribe(this);

        // Storage
        database = StorageConnector.read(this).database("uppercore_test");
        new StorageCommands().subscribe(this);
    }

    @Override
    public void onDisable() {
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
