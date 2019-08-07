package xyz.upperlevel.uppercore.test;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.storage.Database;

import java.util.logging.Logger;

public class UppercoreTest extends JavaPlugin {
    private static UppercoreTest instance;

    @Override
    public void onEnable() {
        instance = this;

        Uppercore.hook(this);
    }

    @Override
    public void onDisable() {
        Uppercore.destroy();
    }

    public static UppercoreTest get() {
        return instance;
    }
}
