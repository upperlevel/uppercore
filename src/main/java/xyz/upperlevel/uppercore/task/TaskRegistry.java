package xyz.upperlevel.uppercore.task;

import lombok.Data;
import org.bukkit.plugin.Plugin;

@Data
public class TaskRegistry {
    private final Plugin plugin;

    public Timer timer(long start, long each, Runnable tick, Runnable end) {
        return new Timer(plugin, start, each, tick, end);
    }

}
