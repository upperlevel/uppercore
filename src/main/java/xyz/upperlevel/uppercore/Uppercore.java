package xyz.upperlevel.uppercore;

import lombok.Getter;
import org.bstats.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.HelpCommand;
import xyz.upperlevel.uppercore.command.functional.FunctionalCommand;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.ConfigUtil;
import xyz.upperlevel.uppercore.config.parser.ConfigParserRegistry;
import xyz.upperlevel.uppercore.storage.StorageManager;
import xyz.upperlevel.uppercore.economy.EconomyManager;
import xyz.upperlevel.uppercore.gui.GuiManager;
import xyz.upperlevel.uppercore.hotbar.HotbarManager;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.placeholder.message.MessageManager;
import xyz.upperlevel.uppercore.registry.RegistryRoot;
import xyz.upperlevel.uppercore.script.ScriptManager;
import xyz.upperlevel.uppercore.update.DownloadableUpdateChecker;
import xyz.upperlevel.uppercore.update.SpigetUpdateChecker;
import xyz.upperlevel.uppercore.util.CrashUtil;

import java.io.File;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Getter
public class Uppercore extends JavaPlugin {
    public static final String SCRIPT_CONFIG = "script_engine.yml";
    public static final String SPIGOT_ID = "uppercore.45866";
    public static final long SPIGET_ID = 45866;

    private static Uppercore instance;

    private RegistryRoot registryRoot = new RegistryRoot();
    private GuiManager guis;
    private HotbarManager hotbars;
    private ScriptManager scripts;
    private StorageManager storages;
    private MessageManager messages;
    private DownloadableUpdateChecker updater;
    private ConfigParserRegistry parsers = ConfigParserRegistry.createStandard();

    private Metrics metrics;

    @Override
    public void onEnable() {
        instance = this;

        try {
            // Metrics setup
            metrics = new Metrics(this);
            // UpdateChecker setup
            updater = new SpigetUpdateChecker(this, SPIGOT_ID, SPIGET_ID);

            PlaceholderUtil.tryHook();
            EconomyManager.enable();

            /* Command configuration */
            saveResource("command.yml", false);
            Config cfg = Config.wrap(ConfigUtil.loadConfig(Uppercore.get(), "command.yml"));
            Command.configure(cfg);
            HelpCommand.configure(cfg);
            FunctionalCommand.configure(cfg);

            // Managers
            guis = new GuiManager();
            hotbars = new HotbarManager();
            scripts = new ScriptManager();
            storages = new StorageManager();

            // ScriptManager setup
            File scriptsConfigFile = new File(getDataFolder(), SCRIPT_CONFIG);
            if (!scriptsConfigFile.exists())
                saveResource(SCRIPT_CONFIG, false);
            scripts.load(new File(getDataFolder(), "engines"), scriptsConfigFile);

            // Metrics custom data setup
            scripts.setupMetrics(metrics);
        } catch (Throwable t) {
            CrashUtil.saveCrash(this, t);
            setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
    }

    public File getFile() {
        return super.getFile();
    }

    public static Uppercore get() {
        return instance;
    }

    public static Logger logger() {
        return instance.getLogger();
    }

    public static Logger logger(String name) {
        return LogManager.getLogManager().getLogger(logger().getName() + "." + name);
    }

    public static RegistryRoot registry() {
        return instance.registryRoot;
    }

    public static GuiManager guis() {
        return instance.guis;
    }

    public static HotbarManager hotbars() {
        return instance.hotbars;
    }

    public static ScriptManager scripts() {
        return instance.scripts;
    }

    public static MessageManager messages() {
        return instance.messages;
    }

    public static StorageManager storages() {
        return instance.storages;
    }

    public static ConfigParserRegistry parsers() {
        return instance.parsers;
    }
}
