package xyz.upperlevel.uppercore;

import lombok.Getter;
import org.bstats.Metrics;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.board.BoardManager;
import xyz.upperlevel.uppercore.command.argument.ArgumentParserSystem;
import xyz.upperlevel.uppercore.command.commands.UppercoreCommand;
import xyz.upperlevel.uppercore.economy.EconomyManager;
import xyz.upperlevel.uppercore.gui.GuiManager;
import xyz.upperlevel.uppercore.hotbar.HotbarManager;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.script.ScriptManager;

import java.io.File;
import java.util.logging.Logger;

@Getter
public class Uppercore extends JavaPlugin {

    public static final String SCRIPT_CONFIG = "script_engine.yml";

    private static Uppercore instance;

    private BoardManager boards;
    private GuiManager guis;
    private HotbarManager hotbars;
    private ScriptManager scripts;

    private Metrics metrics;

    @Override
    public void onEnable() {
        instance = this;

        //Metrics setup
        metrics = new Metrics(this);

        PlaceholderUtil.tryHook();
        EconomyManager.enable();

        //Command setup
        ArgumentParserSystem.initialize();

        // MANAGER
        boards = new BoardManager();
        guis = new GuiManager();
        hotbars = new HotbarManager();
        scripts = new ScriptManager();

        //ScriptManager setup
        File scriptsConfigFile = new File(getDataFolder(), SCRIPT_CONFIG);
        if (!scriptsConfigFile.exists())
            saveResource(SCRIPT_CONFIG, false);
        scripts.load(new File(getDataFolder(), "engines"), scriptsConfigFile);

        //Metrics custom data setup
        scripts.setupMetrics(metrics);


        //Gui setup
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

    public static ScriptManager scripts() {
        return instance.scripts;
    }

    public static HotbarManager hotbars() {
        return instance.hotbars;
    }
}
