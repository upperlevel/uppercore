package xyz.upperlevel.uppercore.task;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.upperlevel.uppercore.Uppercore;

@Getter
public class UpdaterTask {
    private final Runnable action;

    @Setter
    private int interval;
    private BukkitRunnable task;

    public UpdaterTask(Runnable action) {
        this.action = action;
    }

    public UpdaterTask(int interval, Runnable action) {
        this.action = action;
        this.interval = interval;
    }

    public void start() {
        start(true);
    }

    public void start(boolean now) {
        if (task != null)
            stop();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                action.run();
            }
        };
        task.runTaskTimer(Uppercore.get(), now ? 0 : interval, interval);
    }

    public boolean isStarted() {
        return task != null;
    }

    public boolean stop() {
        if (task != null) {
            task.cancel();
            task = null;
            return true;
        }
        return false;
    }
}
