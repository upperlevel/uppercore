package xyz.upperlevel.uppercoretest;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercoretest.command.TestCommand;
import xyz.upperlevel.uppercoretest.command.functional.TestFunctionalNodeCommand;

public class UppercoreTest extends JavaPlugin {
    @Override
    public void onEnable() {
        new TestCommand().subscribe(this);
        new TestFunctionalNodeCommand().subscribe(this);
    }

    @Override
    public void onDisable() {
    }
}
