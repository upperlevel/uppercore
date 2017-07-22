package xyz.upperlevel.uppercore;

import lombok.Getter;
import org.bstats.Metrics;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.command.argument.ArgumentParserSystem;
import xyz.upperlevel.uppercore.command.commands.UppercoreCommand;
import xyz.upperlevel.uppercore.gui.GuiEventListener;
import xyz.upperlevel.uppercore.gui.GuiListener;
import xyz.upperlevel.uppercore.economy.EconomyManager;
import xyz.upperlevel.uppercore.gui.hotbar.HotbarSystem;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.scoreboard.ScoreboardSystem;
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

        PlaceholderUtil.tryHook();
        EconomyManager.enable();

        //ScriptSystem setup
        File scriptsConfigFile = new File(getDataFolder(), SCRIPT_CONFIG);
        if (!scriptsConfigFile.exists())
            saveResource(SCRIPT_CONFIG, false);
        ScriptSystem.load(new File(getDataFolder(), "engines"), scriptsConfigFile);

        //Metrics custom data setup
        ScriptSystem.setupMetrics(metrics);

        //Command setup
        ArgumentParserSystem.initialize();
        HotbarSystem.initialize();
        ScoreboardSystem.initialize();

        //Gui setup
        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new GuiEventListener(), this);
        pluginManager.registerEvents(new GuiListener(), this);

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
