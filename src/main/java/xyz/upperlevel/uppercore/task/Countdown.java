package xyz.upperlevel.uppercore.task;

import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

public class Countdown {
    @Getter
    private final Plugin plugin;

    @Getter
    private BukkitRunnable task;

    @Getter
    private final long startAt, repeatEach;

    @Getter
    private long currentTick;

    private final Consumer<Long> onTick;
    private final Runnable onEnd;

    public Countdown(Plugin plugin, long startAt, long repeatEach, Consumer<Long> onTick, Runnable onEnd) {
        this.plugin = plugin;
        this.startAt = startAt;
        this.repeatEach = repeatEach;
        this.onTick = onTick;
        this.onEnd = onEnd;
    }

    public void start() {
        if (task != null) {
            stop();
        }
        currentTick = startAt;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                onTick.accept(currentTick / repeatEach);
                if (currentTick > 0)
                    currentTick -= repeatEach;
                else {
                    onEnd.run();
                    stop();
                }
            }
        };
        task.runTaskTimer(plugin, 0, repeatEach);
    }

    public boolean isStarted() {
        return task != null;
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public String format(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date(currentTick * 50));
    }
}
