package xyz.upperlevel.uppercore.update;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.Executor;
import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.update.method.ReplaceUpdateMethod;
import xyz.upperlevel.uppercore.update.method.UpdateMethod;
import xyz.upperlevel.uppercore.update.notifier.DefaultDownloadNotifier;
import xyz.upperlevel.uppercore.update.notifier.DownloadNotifier;
import xyz.upperlevel.uppercore.update.notifier.DownloadSession;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class DownloadableUpdateChecker extends UpdateChecker {

    @Getter
    @Setter
    private UpdateCommand command;

    @Getter
    @Setter
    private DownloadNotifier.Constructor downloadNotifierConstructor = DefaultDownloadNotifier::new;

    @Getter
    @Setter
    private UpdateMethod method = new ReplaceUpdateMethod();

    public DownloadableUpdateChecker(Plugin plugin, String spigotFullId) {
        super(plugin, spigotFullId);
    }


    protected UpdateCommand buildCommand() {
        return new UpdateCommand();
    }

    @Override
    protected BaseComponent[] buildMessage() {
        if(command == null)
            command = buildCommand();
        return new ComponentBuilder("[" + getPlugin().getDescription().getName() + "] Update available!")
                .color(ChatColor.GOLD)
                .bold(true)
                .append("(Click ")
                .append("HERE")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command.getUsage()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Update " + getPlugin().getDescription().getName()).create()))
                .append(" to update)")
                .create();
    }

    @Override
    protected void notifyConsole() {
        getLogger().info("[Updater] Update found, use the " + command.getUsage() + " command for an automatic update");
    }

    public void update(CommandSender sender) {
        lastState = VersionState.OTHER;
        URLConnection download;
        try {
            download = getDownload();
            if(download == null)
                getLogger().log(Level.SEVERE, "[Updater] Cannot get download link! please download the update manually (resource not found)");
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "[Updater] Cannot get download link! please download the update manually", e);
            download = null;
        }
        if(download == null) {
            sender.spigot().sendMessage(
                    new ComponentBuilder("Cannot get download link! please download the update manually")
                            .color(ChatColor.RED)
                            .append("from HERE")
                            .color(ChatColor.RED)
                            .bold(true)
                            .event(new ClickEvent(ClickEvent.Action.OPEN_URL, getSpigotUrl().toString()))
                            .create()
            );
            return;
        }
        File pluginFile = createUpdateFile();
        tryToDownload(download, pluginFile, this::handleDownloadException, sender);
    }

    protected void handleDownloadException(Exception e) {
        getLogger().log(Level.SEVERE, "Exception occurred during download", e);
        for(Player p : Bukkit.getOnlinePlayers())
            if(p.hasPermission(getPermission()))
                p.sendMessage("Error occurred during update download");
    }

    public abstract URLConnection getDownload() throws IOException;

    public void tryToDownload(URLConnection connection, File file, Consumer<IOException> exceptionHandler, CommandSender caller){
        DownloadSession session;
        try {
            session = new DownloadSession(connection, file);
        } catch (IOException e) {
            exceptionHandler.accept(e);
            return;
        }
        DownloadNotifier notifier = downloadNotifierConstructor.create(session, caller);
        notifier.setLogger(getLogger());
        session.startAsync(() -> {
            notifier.stop();
            onUpdateEnd(file, caller);
        });
    }

    protected void onUpdateEnd(File file, CommandSender caller) {
        try {
            method.update(file, getPlugin());
        } catch (IOException e) {
            getLogger().log(Level.SEVERE,"Error while updating, please update the resource manually", e);
            if(caller != Bukkit.getConsoleSender()) {
                caller.sendMessage(ChatColor.RED + "An error occurred while updating the resource, please update it manually");
            }
            file.delete();
            return;
        }
        getLogger().info("Update succeeded, please restart the server");
        if(caller != Bukkit.getConsoleSender()) {
            caller.sendMessage(ChatColor.GREEN + "Update succeeded, please restart the server");
        }
    }


    public class UpdateCommand extends Command {
        public UpdateCommand() {
            super("update");
            setDescription("Updates the plugin");
        }

        @Executor
        public void run(CommandSender sender) {
            if(getLastState() == VersionState.UPDATE_AVAILABLE || (needsRefresh() && check() == VersionState.UPDATE_AVAILABLE)) {
                update(sender);
            } else {
                sender.sendMessage(ChatColor.RED + "Update not found, nothing to update (state: " + getLastState() + ")");
            }
        }

        @Override
        public void setParent(NodeCommand parent) {
            super.setParent(parent);
            setMessage(buildMessage());
        }
    }
}
