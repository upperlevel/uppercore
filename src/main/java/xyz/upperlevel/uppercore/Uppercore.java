package xyz.upperlevel.uppercore;

import lombok.Getter;
import org.bstats.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.command.commands.UppercoreCommand;
import xyz.upperlevel.uppercore.script.ScriptSystem;

import java.io.File;
import java.util.logging.Logger;

@Getter
public class Uppercore extends JavaPlugin {

    public static final String SCRIPT_CONFIG = "script_engine.yml";

    private static Uppercore instance;

    private Metrics metrics;

    @Override
    public void onEnable() {
        instance = this;

        //Metrics setup
        metrics = new Metrics(this);

        //ScriptSystem setup
        File scriptsConfigFile = new File(getDataFolder(), SCRIPT_CONFIG);
        if (!scriptsConfigFile.exists())
            saveResource(SCRIPT_CONFIG, false);
        ScriptSystem.load(new File(getDataFolder(), "engines"), scriptsConfigFile);

        //Metrics custom data setup
        ScriptSystem.setupMetrics(metrics);

        //Command setup
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
