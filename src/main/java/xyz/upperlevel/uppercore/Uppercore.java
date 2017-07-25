package xyz.upperlevel.uppercore;

import lombok.Getter;
import org.bstats.Metrics;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.board.BoardManager;
import xyz.upperlevel.uppercore.command.commands.UppercoreCommand;
import xyz.upperlevel.uppercore.economy.EconomyManager;
import xyz.upperlevel.uppercore.gui.GuiManager;
import xyz.upperlevel.uppercore.hotbar.HotbarManager;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.script.ScriptSystem;

import java.io.File;
import java.util.logging.Logger;

@Getter
public class Uppercore extends JavaPlugin {

    public static final String SCRIPT_CONFIG = "script_engine.yml";

    private static Uppercore instance;

    private BoardManager boards;
    private GuiManager guis;
    private HotbarManager hotbars;

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

        boards = new BoardManager();
        guis = new GuiManager();
        hotbars = new HotbarManager();

        //Gui setup
        PluginManager pluginManager = getServer().getPluginManager();
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

    public static BoardManager boards() {
        return instance.boards;
    }

    public static GuiManager guis() {
        return instance.guis;
    }

    public static HotbarManager hotbars() {
        return instance.hotbars;
    }
}
