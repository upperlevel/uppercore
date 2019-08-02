package xyz.upperlevel.uppercore.util;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import xyz.upperlevel.uppercore.Uppercore;

public final class FireworkUtil {
    public static void instantFirework(Location loc, FireworkEffect effect) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(effect);
        firework.setFireworkMeta(meta);
        Bukkit.getScheduler().runTaskLater(
                Uppercore.plugin(),
                firework::detonate,
                1
        );
    }

    private FireworkUtil() {}
}
