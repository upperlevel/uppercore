package xyz.upperlevel.uppercore.task;

import lombok.Data;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public abstract class Timer extends BukkitRunnable {
    private final Plugin plugin;
    private final long start, each;
    private long current;

    public Timer(Plugin plugin, long start, long each) {
        this.plugin = plugin;
        this.start = start;
        this.each = each;
    }

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

    public abstract void tick();

    public abstract void end();

    public void start() {
        current = start;
        runTaskTimer(plugin, 0, each);
    }

    public boolean isStarted() {
        return current > 0;
    }

    public void stop() {
        cancel();
        if (current > 0)
            current = 0;
    }

    public String toString(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date(current * 50));
    }
}
