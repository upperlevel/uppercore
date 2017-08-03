package xyz.upperlevel.uppercore.util.nms.impl.entity;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import xyz.upperlevel.uppercore.util.nms.NmsPacket;
import xyz.upperlevel.uppercore.util.nms.refl.Class;
import xyz.upperlevel.uppercore.util.nms.refl.field.IntField;

public final class FireworkNms {
    private static final Class CLASS;
    private static final IntField expectedLifespan;
    private static final IntField ticksFlown;

    static  {
        CLASS = Class.of(NmsPacket.NMS, "EntityFireworks");
        expectedLifespan = CLASS.getIntField("expectedLifespan", true);
        ticksFlown = CLASS.getIntField("ticksFlown", false);
    }

    public static void instantFirework(Firework firework) {
        /*
        EntityFirework handle = ((CraftFirework)firework).getHandle();
        handle.ticksFlown = handle.expectedLifespan - 1;
         */
        Object handle = EntityNms.getHandle(firework);
        ticksFlown.set(handle, expectedLifespan.get(handle));
    }

    public static void instantFirework(Location loc, FireworkEffect effect) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(effect);
        firework.setFireworkMeta(meta);
        instantFirework(firework);
    }
}
