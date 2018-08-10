package xyz.upperlevel.uppercoretest;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.gui.Gui;
import xyz.upperlevel.uppercore.hotbar.Hotbar;
import xyz.upperlevel.uppercore.registry.Registry;
import xyz.upperlevel.uppercoretest.command.TestCommand;
import xyz.upperlevel.uppercoretest.command.functional.TestFunctionalNodeCommand;
import xyz.upperlevel.uppercoretest.gui.GuiCommand;
import xyz.upperlevel.uppercoretest.gui.HotbarCommand;

import java.util.logging.Logger;

public class UppercoreTest extends JavaPlugin {
    private static UppercoreTest instance;
    private Logger logger;
    private Registry<?> root;

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
}
