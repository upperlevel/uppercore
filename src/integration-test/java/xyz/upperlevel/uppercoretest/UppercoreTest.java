package xyz.upperlevel.uppercoretest;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.command.NativeCommandUtil;
import xyz.upperlevel.uppercoretest.command.TestCommand;
import xyz.upperlevel.uppercoretest.command.TestNodeCommand;
import xyz.upperlevel.uppercoretest.command.functional.TestFunctionalNodeCommand;

public class UppercoreTest extends JavaPlugin {
    @Override
    public void onEnable() {
        NativeCommandUtil.register(new TestCommand());
        NativeCommandUtil.register(new TestNodeCommand());
        NativeCommandUtil.register(new TestFunctionalNodeCommand());

    }

    @Override
    public void onDisable() {
    }
}
