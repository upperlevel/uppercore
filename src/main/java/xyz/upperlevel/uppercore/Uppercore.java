package xyz.upperlevel.uppercore;

import lombok.Getter;
import org.bstats.Metrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.arena.ArenaManager;
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

public class Uppercore {
    private static Uppercore instance;

    private Plugin plugin;

    @Getter
    private Logger coreLogger;

    private RegistryRoot registryRoot = new RegistryRoot();
    private GuiManager guis;
    private HotbarManager hotbars;
    private ScriptManager scripts;
    private StorageManager storages;

    private ConfigParserRegistry parsers = ConfigParserRegistry.createStandard();

    private Metrics metrics;

    private Uppercore(JavaPlugin plugin) {
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

        /* TODO Arena API is late to be defined...
        plugin.saveResource("game.yml", false);
        cfg = Config.fromYaml(new File(plugin.getDataFolder(), "game.yml"));
        Arena.configure(cfg);
        */

        // Managers
        guis = new GuiManager();
        hotbars = new HotbarManager();
        scripts = new ScriptManager();
        storages = new StorageManager();

        // ScriptManager setup
        scripts.load(new File(plugin.getDataFolder(), "engines"), cfg.getConfigRequired("scripts"));

        // Metrics custom data setup
        // TODO: custom data
        // TODO: check what plugins use uppercore?
    }

    /**
     * Unloads Uppercore and destroys the current instance.
     */
    public void destroy() {
        // TODO remove garbage
        instance = null;
    }

    /**
     * Returns an instance of Uppercore to use within the given plugin.
     *
     * @param plugin the target plugin.
     * @return an instance of Uppercore.
     */
    public static Uppercore hook(JavaPlugin plugin) {
        return new Uppercore(plugin);
    }

    public static Uppercore get() {
        return instance;
    }

    public static Plugin getPlugin() {
        return instance.plugin;
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
