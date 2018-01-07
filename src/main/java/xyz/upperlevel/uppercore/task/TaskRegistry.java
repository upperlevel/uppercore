package xyz.upperlevel.uppercore.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class TaskRegistry {
    @Getter
    private final Plugin plugin;

    public Timer timer(long start, long each, Runnable tick, Runnable end) {
        return new Timer(plugin, start, each, tick, end);
    }
}
