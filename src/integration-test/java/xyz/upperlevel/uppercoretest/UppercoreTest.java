package xyz.upperlevel.uppercoretest;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.command.CommandRegistry;
import xyz.upperlevel.uppercoretest.command.TestCommand;
import xyz.upperlevel.uppercoretest.command.TestNodeCommand;
import xyz.upperlevel.uppercoretest.command.functional.TestFunctionalNodeCommand;

public class UppercoreTest extends JavaPlugin {
    @Override
    public void onEnable() {
        CommandRegistry.register(new TestCommand());
        CommandRegistry.register(new TestNodeCommand());
        CommandRegistry.register(new TestFunctionalNodeCommand());

    }

    @Override
    public void onDisable() {
    }
}
