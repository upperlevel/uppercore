package xyz.upperlevel.uppercore;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.gui.script.ScriptSystem;

import java.util.logging.Logger;

@Getter
public class Uppercore extends JavaPlugin {

    private static Uppercore instance;

    private ScriptSystem scriptSystem;

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

    public static Logger logger() {
        return instance.getLogger();
    }
}
