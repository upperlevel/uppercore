package xyz.upperlevel.uppercore.task;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.upperlevel.uppercore.Uppercore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public abstract class Countdown extends BukkitRunnable {
    @Getter
    private final long startAt, repeatEach;

    @Getter
    private long currentTick;

    public Countdown(long startAt, long repeatEach) {
        this.startAt = startAt;
        this.repeatEach = repeatEach;
    }

    public void start() {
        currentTick = startAt + repeatEach;
        runTaskTimer(Uppercore.plugin(), 0, repeatEach);
    }

    public long getTime() {
        return currentTick / repeatEach;
    }

    protected abstract void onTick(long time);

    protected abstract void onEnd();

    @Override
    public void run() {
        currentTick -= repeatEach;
        if (currentTick == 0) {
            onEnd();
            super.cancel();
        } else {
            onTick(currentTick / repeatEach);
        }
    }

    public String toString(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date(currentTick * 50));
    }

    public static Countdown create(long startAt, long repeatEach, LongConsumer onTick, Runnable onEnd) {
        return new Countdown(startAt, repeatEach) {
            @Override
            protected void onTick(long tick) {
                onTick.accept(tick);
            }

            @Override
            protected void onEnd() {
                onEnd.run();
            }
        };
    }
}
