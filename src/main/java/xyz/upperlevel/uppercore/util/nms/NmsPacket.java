package xyz.upperlevel.uppercore.util.nms;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Getter;

import static xyz.upperlevel.uppercore.util.nms.NmsVersion.VERSION;

public enum NmsPacket {
    NMS("net.minecraft.server"),
    CRAFT("org.bukkit.craftbukkit"),
    CRAFT_ADVANCEMENT(CRAFT, "advancement"),
    CRAFT_ATTRIBUTE(CRAFT, "attribute"),
    CRAFT_BLOCK(CRAFT, "block"),
    CRAFT_BOSS(CRAFT, "boss"),
    CRAFT_CHUNKIO(CRAFT, "chunkio"),
    CRAFT_COMMAND(CRAFT, "command"),
    CRAFT_CONVERSATIONS(CRAFT, "conversations"),
    CRAFT_ENCHANTMENT(CRAFT, "enchantments"),
    CRAFT_ENTITY(CRAFT, "entity"),
    CRAFT_EVENT(CRAFT, "event"),
    CRAFT_GENERATOR(CRAFT, "generator"),
    CRAFT_HELP(CRAFT, "help"),
    CRAFT_INVENTORY(CRAFT, "inventory"),
    CRAFT_MAP(CRAFT, "map"),
    CRAFT_METADATA(CRAFT, "metadata"),
    CRAFT_POTION(CRAFT, "potion"),
    CRAFT_PROJECTILES(CRAFT, "projectiles"),
    CRAFT_SCHEDULER(CRAFT, "scheduler"),
    CRAFT_SCOREBOARD(CRAFT, "scoreboard"),
    CRAFT_UTIL(CRAFT, "util"),
    CRAFT_UTIL_PERMISSION(CRAFT_UTIL, "permissions")
    ;

    @Getter
    private final String path;

    NmsPacket(String path) {
        this.path = path + '.' + VERSION;
    }

    NmsPacket(NmsPacket parent, String path) {
        this.path = parent.path + '.' + path;
    }

    public Class<?> getClass(String clazz) throws ClassNotFoundException {
        return Class.forName(path + '.' + clazz);
    }
}
