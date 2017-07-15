package xyz.upperlevel.uppercore;

import org.bukkit.plugin.java.JavaPlugin;

public class Uppercore extends JavaPlugin {

    private static Uppercore instance;

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {
    }

    public static Uppercore get() {
        return instance;
    }
}
