package xyz.upperlevel.uppercore.util.nms.impl.entity;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.util.nms.NmsPacket;
import xyz.upperlevel.uppercore.util.nms.impl.WorldNms;
import xyz.upperlevel.uppercore.util.nms.refl.Class;
import xyz.upperlevel.uppercore.util.nms.refl.field.IntField;

public final class FireworkNms {
    private static final Class CLASS;

    static  {
        CLASS = Class.of(NmsPacket.NMS, "EntityFireworks");
    }

    public static void instantFirework(Location loc, FireworkEffect effect) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(effect);
        firework.setFireworkMeta(meta);
        Bukkit.getScheduler().runTaskLater(
                Uppercore.get(),
                firework::detonate,
                1
        );
    }
}
