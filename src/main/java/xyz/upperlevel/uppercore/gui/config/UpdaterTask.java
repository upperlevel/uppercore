package xyz.upperlevel.uppercore.gui.config;

import org.bukkit.scheduler.BukkitRunnable;
import xyz.upperlevel.uppercore.Uppercore;

public class UpdaterTask extends BukkitRunnable {

    private int interval;
    private Runnable task;

    public UpdaterTask(Runnable task) {
        this.task = task;
    }

    public UpdaterTask(int interval, Runnable task) {
        this.interval = interval;
        this.task = task;
    }

    public void start() {
        runTaskTimer(Uppercore.get(), 0, interval);
    }

    public void stop() {
        cancel();
    }

    @Override
    public void run() {
        task.run();
    }
}
