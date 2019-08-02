package xyz.upperlevel.uppercore;

import lombok.Getter;
import org.bstats.Metrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.arena.Arena;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.HelpCommand;
import xyz.upperlevel.uppercore.command.functional.FunctionalCommand;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.parser.ConfigParserRegistry;
import xyz.upperlevel.uppercore.gui.GuiManager;
import xyz.upperlevel.uppercore.hotbar.HotbarManager;
import xyz.upperlevel.uppercore.economy.EconomyManager;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.registry.RegistryRoot;
import xyz.upperlevel.uppercore.script.ScriptManager;
import xyz.upperlevel.uppercore.storage.StorageManager;

import java.io.File;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Getter
public class Uppercore {
    public static final String SCRIPT_CONFIG = "script_engine.yml";

    private static Uppercore instance;

    private Plugin plugin;
    private Logger coreLogger;

    private RegistryRoot registryRoot = new RegistryRoot();
    private GuiManager guis;
    private HotbarManager hotbars;
    private ScriptManager scripts;
    private StorageManager storages;
    private ConfigParserRegistry parsers = ConfigParserRegistry.createStandard();

    private Metrics metrics;

    public void onEnable(JavaPlugin plugin) {
        if (instance != null) {
            if (instance.plugin == plugin) {
                throw new RuntimeException("Creating two instances of UpperCore!");
            } else {
                throw new RuntimeException("Two different plugins are trying to use UpperCore, did you forget to relocate?");
            }
        }
        instance = this;

        this.plugin = plugin;
        coreLogger = LogManager.getLogManager().getLogger(plugin.getLogger().getName() + ".ucore");
        coreLogger.info("Loading UpperCore version: " + UppercoreInfo.VERSION);

        // Metrics setup
        metrics = new Metrics(plugin);
        // UpdateChecker setup

        PlaceholderUtil.tryHook();
        EconomyManager.enable();

        // Command configuration
        plugin.saveResource("uppercore.yml", false);
        Config cfg = Config.fromYaml(new File(plugin.getDataFolder(), "uppercore.yml"));
        Config commandConfig = cfg.getConfigRequired("commands");
        Command.configure(commandConfig);
        HelpCommand.configure(commandConfig);
        FunctionalCommand.configure(commandConfig);

        // Game configuration
        plugin.saveResource("game.yml", false);
        cfg = Config.fromYaml(new File(plugin.getDataFolder(), "game.yml"));
        Arena.configure(cfg);

        // Managers
        guis = new GuiManager();
        hotbars = new HotbarManager();
        scripts = new ScriptManager();
        storages = new StorageManager();

        // ScriptManager setup
        File scriptsConfigFile = new File(plugin.getDataFolder(), SCRIPT_CONFIG);
        if (!scriptsConfigFile.exists()) {
            plugin.saveResource(SCRIPT_CONFIG, false);
        }
        scripts.load(new File(plugin.getDataFolder(), "engines"), scriptsConfigFile);

        // Metrics custom data setup
        // TODO: custom data
        // TODO: check what plugins use uppercore?
    }

    public void onDisable() {
    }

    public static Uppercore get() {
        return instance;
    }

    public static Logger logger() {
        return get().coreLogger;
    }

    public static Logger logger(String name) {
        return LogManager.getLogManager().getLogger(logger().getName() + "." + name);
    }

    public static Plugin plugin() {
        return get().plugin;
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

    public static StorageManager storages() {
        return instance.storages;
    }

    public static ConfigParserRegistry parsers() {
        return instance.parsers;
    }
}
