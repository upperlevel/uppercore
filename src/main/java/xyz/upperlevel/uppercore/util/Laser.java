package xyz.upperlevel.uppercore.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.upperlevel.uppercore.math.RayTrace;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Laser {
    private final Plugin plugin;

    private final BiConsumer<Location, List<Player>> onStep;
    private final Runnable onEnd;

    private final List<Location> steps;
    private int currentStepIndex;

    private Set<Player> alreadyHit = new HashSet<>(); // this is done to avoid hitting the same player twice

    private final BukkitRunnable task = new BukkitRunnable() {
        @Override
        public void run() {
            Location step = steps.get(currentStepIndex++);
            onStep.accept(
                    step,
                    PlayerUtil.getPlayersAround(step, 0.25)
                            .stream()
                            .filter(player -> alreadyHit.add(player))
                            .collect(Collectors.toList())
            );
            if (step.getBlock().getType().isSolid() || currentStepIndex >= steps.size()) {
                onEnd.run();
                cancel();
            }
        }
    };

    public Laser(Plugin plugin,
                 Location eye,
                 double maxDistance,
                 double accuracy,
                 BiConsumer<Location, List<Player>> onStep,
                 Runnable onEnd) {
        this.plugin = plugin;

        Vector start = eye.toVector();
        start.setY(start.getY() - 0.15);
        this.steps = new RayTrace(start, eye.getDirection()).traverse(maxDistance, accuracy)
                .stream()
                .map(vector -> vector.toLocation(eye.getWorld()))
                .collect(Collectors.toList());
        this.currentStepIndex = 0;

        this.onStep = onStep;
        this.onEnd = onEnd;
    }

    public void shoot() {
        task.runTaskTimer(plugin, 0, 1);
    }
}