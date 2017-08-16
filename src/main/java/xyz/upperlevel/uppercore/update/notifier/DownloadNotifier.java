package xyz.upperlevel.uppercore.update.notifier;

import org.bukkit.command.CommandSender;

import java.util.logging.Logger;

public interface DownloadNotifier {
    Logger getLogger();

    void setLogger(Logger logger);

    void stop();

    interface Constructor<T extends DownloadNotifier> {
        T create(DownloadSession session, CommandSender caller);
    }
}
