package xyz.upperlevel.uppercore;

import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.arena.Arena;
import xyz.upperlevel.uppercore.arena.OnQuitHandler;
import xyz.upperlevel.uppercore.arena.command.ArenaParameterHandler;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.HelpCommand;
import xyz.upperlevel.uppercore.command.functional.FunctionalCommand;
import xyz.upperlevel.uppercore.command.functional.parameter.BukkitParameterHandler;
import xyz.upperlevel.uppercore.command.functional.parameter.PrimitiveParameterHandler;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.parser.ConfigParserRegistry;
import xyz.upperlevel.uppercore.economy.EconomyManager;
import xyz.upperlevel.uppercore.gui.GuiManager;
import xyz.upperlevel.uppercore.hotbar.HotbarManager;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.registry.Registry;
import xyz.upperlevel.uppercore.script.ScriptManager;
import xyz.upperlevel.uppercore.storage.StorageManager;

import java.io.File;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Uppercore {
    private static Uppercore instance;

    private Plugin plugin;

    @Getter
    private Logger coreLogger;

    private Registry registryRoot = Registry.root();
    private GuiManager guis;
    private HotbarManager hotbars;
    private ScriptManager scripts;
    private StorageManager storages;

    private ConfigParserRegistry parsers = ConfigParserRegistry.createStandard();

    private Metrics metrics;

    private boolean debugMode = false;

    private static void setupCommands() {
        PrimitiveParameterHandler.register();
        BukkitParameterHandler.register();
        ArenaParameterHandler.register();
    }

    private Uppercore(JavaPlugin plugin, int bStatsId) {
        if (instance != null) {
            if (instance.plugin == plugin) {
                throw new RuntimeException("Creating two instances of UpperCore!");
            } else {
                throw new RuntimeException("Two different plugins are trying to use UpperCore, did you forget to relocate?");
            }
        }
        instance = this;
        this.plugin = plugin;

        this.coreLogger = Logger.getLogger(plugin.getLogger().getName() + ".ucore");
        this.coreLogger.info("Loading UpperCore version: " + UppercoreInfo.VERSION);

        // Metrics setup
        if (bStatsId != 0) {
            metrics = new Metrics(plugin, bStatsId);
        }
        // UpdateChecker setup

        PlaceholderUtil.tryHook();
        EconomyManager.enable();

        // uppercore.yml
        plugin.saveResource("uppercore.yml", false);
        Config cfg = Config.fromYaml(new File(plugin.getDataFolder(), "uppercore.yml"));

        Config arenaCfg = cfg.getConfigRequired("arenas");
        Arena.loadConfig(arenaCfg);
        OnQuitHandler.Local.loadConfig(arenaCfg);
        OnQuitHandler.Bungee.loadConfig(arenaCfg);

        Config commandConfig = cfg.getConfigRequired("commands");
        Command.configure(commandConfig);
        HelpCommand.configure(commandConfig);
        FunctionalCommand.configure(commandConfig);

        // Commands
        setupCommands();

        // Managers
        guis = new GuiManager();
        hotbars = new HotbarManager();
        scripts = new ScriptManager();
        storages = new StorageManager();

        // ScriptManager setup
        scripts.load(new File(plugin.getDataFolder(), "engines"), cfg.getConfigRequired("scripts"));

        this.debugMode = cfg.getBool("debug-mode");

        // Metrics custom data setup
        // TODO: custom data
        // TODO: check what plugins use uppercore?
    }

    /**
     * Returns an instance of Uppercore to use within the given plugin.
     *
     * @param plugin the target plugin.
     * @param bStatsId the bStats id (if you want metrics to be collected), or 0 to disable metrics
     * @return an instance of Uppercore.
     */
    public static Uppercore hook(JavaPlugin plugin, int bStatsId) {
        return new Uppercore(plugin, bStatsId);
    }

    /**
     * Returns an instance of Uppercore to use within the given plugin.
     *
     * @param plugin the target plugin.
     * @return an instance of Uppercore.
     */
    public static Uppercore hook(JavaPlugin plugin) {
        return new Uppercore(plugin, 0);
    }

    public static void destroy() {
        if (instance == null) {
            throw new IllegalStateException("No Uppercore instance created.");
        }
        // TODO disable other stuff
        instance = null;
    }

    public static Uppercore get() {
        return instance;
    }

    public static boolean isDebugMode() {
        return instance.debugMode;
    }

    public static void overrideInstance(Uppercore instance) {
        Uppercore.instance = instance;
    }

    public static Plugin getPlugin() {
        return instance.plugin;
    }

    public static Logger logger() {
        return instance.plugin.getLogger();
    }

    public static Logger logger(String name) {
        return LogManager.getLogManager().getLogger(logger().getName() + "." + name);
    }

    public static Plugin plugin() {
        return get().plugin;
    }

    public static Registry registry() {
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
