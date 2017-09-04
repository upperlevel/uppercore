package xyz.upperlevel.uppercore.update.notifier;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import xyz.upperlevel.uppercore.Uppercore;

import java.util.logging.Logger;

public class DefaultDownloadNotifier implements DownloadNotifier {
    @Getter
    private final DownloadSession session;
    @Getter
    private final CommandSender caller;
    @Getter
    private BukkitTask task;
    @Getter
    @Setter
    private int delay = 20;

    @Getter
    @Setter
    private Logger logger = Logger.getGlobal();

    private final boolean isCallerConsole;
    private boolean prematureEnd = false;

    public DefaultDownloadNotifier(DownloadSession session, CommandSender caller) {
        this.session = session;
        this.caller = caller;
        this.isCallerConsole = caller == Bukkit.getConsoleSender();
        start();
    }

    public void start() {
        if(task != null)
            throw new IllegalStateException("Task already initialized");
        notifyStart();

        task = Bukkit.getScheduler().runTaskTimer(
                Uppercore.get(),
                this::tick,
                delay,
                delay
        );
        tick();
    }

    private void notifyStart() {
        if(!isCallerConsole) {
            caller.sendMessage(ChatColor.GREEN + "Download started");
        }
        logger.info("[Updater] Download started");
    }

    public void tick() {
        long amount = session.getAmount();
        float progress = amount/(float)session.getSize();
        int procProgress = (int) (progress * 100f);
        if(procProgress >= 100) {
            procProgress = 100;
            prematureEnd = true;
            task.cancel();
        }
        if (!isCallerConsole) {
            caller.sendMessage(ChatColor.AQUA + "Update progress: " + procProgress + "%");
        }
        logger.info("[Updater] Download: " + procProgress + "%");
    }

    @Override
    public void stop() {
        if(!prematureEnd)
            task.cancel();
        if(!isCallerConsole) {
            caller.sendMessage(ChatColor.GREEN + "Update Downloaded successfully");
        }
        logger.info("[Updater] Update Downloaded successfully");
    }
}
