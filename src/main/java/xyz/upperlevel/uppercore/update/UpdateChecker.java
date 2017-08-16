package xyz.upperlevel.uppercore.update;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class UpdateChecker {
    @Getter
    private final Plugin plugin;

    @Getter
    private final String spigotId;
    @Getter
    private final URL spigotUrl;

    @Getter
    @Setter
    private VersionComparator comparator = PointDividedVersionComparator.INSTANCE;

    @Getter
    @Setter
    private Logger logger;

    private UpdaterTask updater;
    @Getter
    private int interval;

    @Getter
    protected VersionState lastState = null;

    @Getter
    @Setter
    private Permission permission;

    @Getter
    @Setter
    private BaseComponent[] message;

    public UpdateChecker(Plugin plugin, String spigotFullId) {
        this.plugin = plugin;
        this.spigotId = spigotFullId;
        try {
            this.spigotUrl = new URL("https://www.spigotmc.org/resources/" + spigotId + "/");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid spigot id: " + spigotId);
        }
        this.logger = plugin.getLogger();
        permission = new Permission(plugin.getName() + ".update");
        permission.setDefault(PermissionDefault.OP);
        Bukkit.getPluginManager().addPermission(permission);
        setInterval(20*60*30);//30 minutes
        message = buildMessage();
    }

    public VersionState check() {
        String upVersion;
        try {
            upVersion = fetchVersion();
        } catch (IOException ex) {
            logger.log(Level.WARNING, "[Updater] Error while retrieving version: " + ex.getMessage());
            return lastState = VersionState.ERROR;
        }
        if (upVersion == null) {
            logger.log(Level.WARNING, "[Updater] Error while retrieving version: Resource not found");
            return lastState = VersionState.ERROR;
        }

        VersionComparator.Result comp;
        try {
            comp = comparator.compare(plugin.getDescription().getVersion(), upVersion);
        } catch (Exception e) {
            logger.log(Level.WARNING, "[Updater] Error while comparing version: Unknown version format");
            return lastState = VersionState.ERROR;
        }
        switch (comp) {
            case NEWER:
                return lastState = VersionState.UPDATE_AVAILABLE;
            case SAME:
                return lastState = VersionState.NO_UPDATE;
            case OLDER:
                return lastState = VersionState.VERSION_NEWER;
        }
        return lastState;
    }

    public File createUpdateFile() {
        File dataFolder = plugin.getDataFolder();
        if(!dataFolder.exists())
            if(!dataFolder.mkdirs())
                throw new IllegalStateException("Cannot create " + plugin.getName() + "'s directory");
        File updateFile = new File("__update");
        if(updateFile.exists())
            if(!updateFile.delete())
                throw new IllegalStateException("Cannot remove file " + updateFile);
        return updateFile;
    }

    public abstract String fetchVersion() throws IOException;

    public void setInterval(int interval) {
        if(this.interval == interval)
            return;
        this.interval = interval;
        restartUpdater();
    }

    protected void restartUpdater() {
        if(updater != null) {
            updater.cancel();
            updater = null;
        }
        if(needsRefresh()) {
            updater = new UpdaterTask();
            updater.runTaskTimerAsynchronously(plugin, 1, interval);
        }
    }

    public boolean needsRefresh() {
        return lastState == null || lastState == VersionState.NO_UPDATE;
    }

    public void onUpdateFound() {
        notifyConsole();
        notifyPlayers();
    }

    protected void notifyConsole() {
        logger.info("[Updater] Update found, check " + spigotUrl + " to download the new version!");
    }

    protected void notifyPlayers() {
        for(Player player : Bukkit.getOnlinePlayers())
            if(player.hasPermission(permission))
                player.spigot().sendMessage(message);
    }

    protected BaseComponent[] buildMessage() {
        return new ComponentBuilder("[" + plugin.getDescription().getName() + "] Update available!")
                    .color(ChatColor.GOLD)
                .append("(Click ")
                .append("HERE")
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, spigotUrl.toString()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(getPlugin().getDescription().getName() + "'s Page").create()))
                .append(" to check it)")
                    .bold(true)
                .create();
    }

    public enum VersionState {
        UPDATE_AVAILABLE,
        NO_UPDATE,
        VERSION_NEWER,
        OTHER,
        ERROR
    }

    private class UpdaterTask extends BukkitRunnable {
        @Override
        public void run() {
            if(!needsRefresh()) {
                stop();
                return;
            }
            if(check() == VersionState.UPDATE_AVAILABLE) {
                Bukkit.getScheduler().runTask(plugin, UpdateChecker.this::onUpdateFound);
            }
            if(!needsRefresh()) {
                stop();
            }
        }

        public void stop() {
            cancel();
            updater = null;
        }
    }
}