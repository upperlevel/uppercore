package xyz.upperlevel.uppercore;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.command.UppercoreCommand;
import xyz.upperlevel.uppercore.script.ScriptSystem;

import java.io.File;
import java.util.logging.Logger;

@Getter
public class Uppercore extends JavaPlugin {

    public static final String SCRIPT_CONFIG = "script_engine.yml";

    private static Uppercore instance;

    private ScriptSystem scriptSystem;

    @Override
    public void onEnable() {
        instance = this;

        File scriptsConfigFile = new File(getDataFolder(), SCRIPT_CONFIG);
        if (!scriptsConfigFile.exists())
            saveResource(SCRIPT_CONFIG, false);
        scriptSystem = new ScriptSystem(new File(getDataFolder(), "engines"), scriptsConfigFile);

        new UppercoreCommand().subscribe();
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
