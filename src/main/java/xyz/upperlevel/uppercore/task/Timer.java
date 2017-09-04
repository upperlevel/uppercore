package xyz.upperlevel.uppercore.task;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Data;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class Timer {
    private final Plugin plugin;
    private BukkitRunnable task;

    private final long start, each;
    private long current;
    private final Runnable tick, end;

    public Timer(Plugin plugin, long start, long each, Runnable tick, Runnable end) {
        this.plugin = plugin;
        this.start = start;
        this.each = each;
        this.tick = tick;
        this.end = end;
    }

    public void start() {
        if (task != null)
            stop();
        current = start;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                tick.run();
                if (current > 0)
                    current -= each;
                else {
                    end.run();
                    stop();
                }
            }
        };
        task.runTaskTimer(plugin, 0, each);
    }

    public boolean isStarted() {
        return task != null;
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
            current = 0;
        }
    }

    public String toString(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date(current * 50));
    }
}
