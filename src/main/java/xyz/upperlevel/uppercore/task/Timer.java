package xyz.upperlevel.uppercore.task;

import lombok.Data;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public abstract class Timer {
    private final Plugin plugin;
    private BukkitRunnable task;

    private final long start, each;
    private long current;

    public Timer(Plugin plugin, long start, long each) {
        this.plugin = plugin;
        this.start = start;
        this.each = each;
    }

    public abstract void tick();

    public abstract void end();

    public void start() {
        if (task != null)
            stop();
        current = start;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
                if (current > 0)
                    current -= each;
                else {
                    end();
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
