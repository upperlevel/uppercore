package xyz.upperlevel.uppercore.test;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.command.CommandRegistry;

public class UppercoreTest extends JavaPlugin {
    private static UppercoreTest instance;

    @Override
    public void onEnable() {
        instance = this;

        // Must always be the first instruction to execute.
        Uppercore.hook(this);

        saveDefaultConfig();

        TestHotbar.loadConfig();
        TestBoard.loadConfig();

        CommandRegistry.register(new UppercoreTestCommand());
    }

    @Override
    public void onDisable() {
        Uppercore.destroy();
    }

    public static UppercoreTest get() {
        return instance;
    }
}
