package xyz.upperlevel.uppercore.particle;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.upperlevel.uppercore.particle.data.ParticleBlockData;
import xyz.upperlevel.uppercore.particle.data.ParticleData;
import xyz.upperlevel.uppercore.particle.data.ParticleItemData;
import xyz.upperlevel.uppercore.particle.exceptions.ParticleColorException;
import xyz.upperlevel.uppercore.particle.exceptions.ParticleDataException;
import xyz.upperlevel.uppercore.particle.exceptions.ParticleVersionException;
import xyz.upperlevel.uppercore.util.nms.NmsVersion;

import java.util.*;
import java.util.stream.Stream;

/**
 * <b>ParticleEffect Library</b>
 * <p>
 * This library was created by @DarkBlade12 and allows you to display all Minecraft particle effects on a Bukkit server
 * <p>
 * You are welcome to use it, modify it and redistribute it under the following conditions:
 * <ul>
 * <li>Don't claim this class as your own
 * <li>Don't removeHotbar this disclaimer
 * </ul>
 * <p>
 * Special thanks:
 * <ul>
 * <li>@microgeek (original idea, names and packet parameters)
 * <li>@ShadyPotato (1.8 names, ids and packet parameters)
 * <li>@RingOfStorms (particle behavior)
 * <li>@Cybermaxke (particle behavior)
 * <li>@JamieSinn (hosting a jenkins server and documentation for particleeffect)
 * </ul>
 * <p>
 * <i>It would be nice if you provide credit to me if you use this class in a published project</i>
 *
 * @author DarkBlade12
 * @version 1.7
 */
public enum ParticleEffect {
    /**
     * A particle effect which is displayed by exploding tnt and creepers:
     * <ul>
     * <li>It looks like a white cloud
     * <li>The speed value influences the velocity at which the particle flies off
     * </ul>
     */
    EXPLOSION_NORMAL("explode", 0, -1, ParticleProperty.DIRECTIONAL),
    /**
     * A particle effect which is displayed by exploding ghast fireballs and wither skulls:
     * <ul>
     * <li>It looks like a gray ball which is fading away
     * <li>The speed value slightly influences the size of this particle effect
     * </ul>
     */
    EXPLOSION_LARGE("largeexplode", 1, -1),
    /**
     * A particle effect which is displayed by exploding tnt and creepers:
     * <ul>
     * <li>It looks like a crowd of gray balls which are fading away
     * <li>The speed value has no influence on this particle effect
     * </ul>
     */
    EXPLOSION_HUGE("hugeexplosion", 2, -1),
    /**
     * A particle effect which is displayed by launching fireworks:
     * <ul>
     * <li>It looks like a white star which is sparkling
     * <li>The speed value influences the velocity at which the particle flies off
     * </ul>
     */
    FIREWORKS_SPARK("fireworksSpark", 3, -1, ParticleProperty.DIRECTIONAL),
    /**
     * A particle effect which is displayed by swimming entities and arrows in water:
     * <ul>
     * <li>It looks like a bubble
     * <li>The speed value influences the velocity at which the particle flies off
     * </ul>
     */
    WATER_BUBBLE("bubble", 4, -1, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_WATER),
    /**
     * A particle effect which is displayed by swimming entities and shaking wolves:
     * <ul>
     * <li>It looks like a blue drop
     * <li>The speed value has no influence on this particle effect
     * </ul>
     */
    WATER_SPLASH("splash", 5, -1, ParticleProperty.DIRECTIONAL),
    /**
     * A particle effect which is displayed on water when fishing:
     * <ul>
     * <li>It looks like a blue droplet
     * <li>The speed value influences the velocity at which the particle flies off
     * </ul>
     */
    WATER_WAKE("wake", 6, 7, ParticleProperty.DIRECTIONAL),
    /**
     * A particle effect which is displayed by water:
     * <ul>
     * <li>It looks like a tiny blue square
     * <li>The speed value has no influence on this particle effect
     * </ul>
     */
    SUSPENDED("suspended", 7, -1, ParticleProperty.REQUIRES_WATER),
    /**
     * A particle effect which is displayed by air when close to bedrock and the in the void:
     * <ul>
     * <li>It looks like a tiny gray square
     * <li>The speed value has no influence on this particle effect
     * </ul>
     */
    SUSPENDED_DEPTH("depthSuspend", 8, -1, ParticleProperty.DIRECTIONAL),
    /**
     * A particle effect which is displayed when landing a critical hit and by arrows:
     * <ul>
     * <li>It looks like a light brown cross
     * <li>The speed value influences the velocity at which the particle flies off
     * </ul>
     */
    CRIT("crit", 9, -1, ParticleProperty.DIRECTIONAL),
    /**
     * A particle effect which is displayed when landing a hit with an enchanted weapon:
     * <ul>
     * <li>It looks like a cyan star
     * <li>The speed value influences the velocity at which the particle flies off
     * </ul>
     */
    CRIT_MAGIC("magicCrit", 10, -1, ParticleProperty.DIRECTIONAL),
    /**
     * A particle effect which is displayed by primed tnt, torches, droppers, dispensers, end portals, brewing stands and monster spawners:
     * <ul>
     * <li>It looks like a little gray cloud
     * <li>The speed value influences the velocity at which the particle flies off
     * </ul>
     */
    SMOKE_NORMAL("smoke", 11, -1, ParticleProperty.DIRECTIONAL),
    /**
     * A particle effect which is displayed by fire, minecarts with furnace and blazes:
     * <ul>
     * <li>It looks like a large gray cloud
     * <li>The speed value influences the velocity at which the particle flies off
     * </ul>
     */
    SMOKE_LARGE("largesmoke", 12, -1, ParticleProperty.DIRECTIONAL),
    /**
     * A particle effect which is displayed when splash potions or bottles o' enchanting hit something:
     * <ul>
     * <li>It looks like a white swirl
     * <li>The speed value causes the particle to only move upwards when set to 0
     * <li>Only the motion on the y-axis can be controlled, the motion on the x- and z-axis are multiplied by 0.1 when setting the values to 0
     * </ul>
     */
    SPELL("spell", 13, -1),
    /**
     * A particle effect which is displayed when instant splash potions hit something:
     * <ul>
     * <li>It looks like a white cross
     * <li>The speed value causes the particle to only move upwards when set to 0
     * <li>Only the motion on the y-axis can be controlled, the motion on the x- and z-axis are multiplied by 0.1 when setting the values to 0
     * </ul>
     */
    SPELL_INSTANT("instantSpell", 14, -1),
    /**
     * A particle effect which is displayed by entities with active potion effects:
     * <ul>
     * <li>It looks like a colored swirl
     * <li>The speed value causes the particle to be colored black when set to 0
     * <li>The particle color gets lighter when increasing the speed and darker when decreasing the speed
     * </ul>
     */
    SPELL_MOB("mobSpell", 15, -1, ParticleProperty.COLORABLE),
    /**
     * A particle effect which is displayed by entities with active potion effects applied through a beacon:
     * <ul>
     * <li>It looks like a transparent colored swirl
     * <li>The speed value causes the particle to be always colored black when set to 0
     * <li>The particle color gets lighter when increasing the speed and darker when decreasing the speed
     * </ul>
     */
    SPELL_MOB_AMBIENT("mobSpellAmbient", 16, -1, ParticleProperty.COLORABLE),
    /**
     * A particle effect which is displayed by witches:
     * <ul>
     * <li>It looks like a purple cross
     * <li>The speed value causes the particle to only move upwards when set to 0
     * <li>Only the motion on the y-axis can be controlled, the motion on the x- and z-axis are multiplied by 0.1 when setting the values to 0
     * </ul>
     */
    SPELL_WITCH("witchMagic", 17, -1),
    /**
     * A particle effect which is displayed by blocks beneath a water source:
     * <ul>
     * <li>It looks like a blue drip
     * <li>The speed value has no influence on this particle effect
     * </ul>
     */
    DRIP_WATER("dripWater", 18, -1),
    /**
     * A particle effect which is displayed by blocks beneath a lava source:
     * <ul>
     * <li>It looks like an orange drip
     * <li>The speed value has no influence on this particle effect
     * </ul>
     */
    DRIP_LAVA("dripLava", 19, -1),
    /**
     * A particle effect which is displayed when attacking a villager in a village:
     * <ul>
     * <li>It looks like a cracked gray heart
     * <li>The speed value has no influence on this particle effect
     * </ul>
     */
    VILLAGER_ANGRY("angryVillager", 20, -1),
    /**
     * A particle effect which is displayed when using bone meal and trading with a villager in a village:
     * <ul>
     * <li>It looks like a green star
     * <li>The speed value has no influence on this particle effect
     * </ul>
     */
    VILLAGER_HAPPY("happyVillager", 21, -1, ParticleProperty.DIRECTIONAL),
    /**
     * A particle effect which is displayed by mycelium:
     * <ul>
     * <li>It looks like a tiny gray square
     * <li>The speed value has no influence on this particle effect
     * </ul>
     */
    TOWN_AURA("townaura", 22, -1, ParticleProperty.DIRECTIONAL),
    /**
     * A particle effect which is displayed by note blocks:
     * <ul>
     * <li>It looks like a colored note
     * <li>The speed value causes the particle to be colored green when set to 0
     * </ul>
     */
    NOTE("note", 23, -1, ParticleProperty.COLORABLE),
    /**
     * A particle effect which is displayed by nether portals, endermen, ender pearls, eyes of ender, ender chests and dragon eggs:
     * <ul>
     * <li>It looks like a purple cloud
     * <li>The speed value influences the spread of this particle effect
     * </ul>
     */
    PORTAL("portal", 24, -1, ParticleProperty.DIRECTIONAL),
    /**
     * A particle effect which is displayed by enchantment tables which are nearby bookshelves:
     * <ul>
     * <li>It looks like a cryptic white letter
     * <li>The speed value influences the spread of this particle effect
     * </ul>
     */
    ENCHANTMENT_TABLE("enchantmenttable", 25, -1, ParticleProperty.DIRECTIONAL),
    /**
     * A particle effect which is displayed by torches, active furnaces, magma cubes and monster spawners:
     * <ul>
     * <li>It looks like a tiny flame
     * <li>The speed value influences the velocity at which the particle flies off
     * </ul>
     */
    FLAME("flame", 26, -1, ParticleProperty.DIRECTIONAL),
    /**
     * A particle effect which is displayed by lava:
     * <ul>
     * <li>It looks like a spark
     * <li>The speed value has no influence on this particle effect
     * </ul>
     */
    LAVA("lava", 27, -1),
    /**
     * A particle effect which is currently unused:
     * <ul>
     * <li>It looks like a transparent gray square
     * <li>The speed value has no influence on this particle effect
     * </ul>
     */
    FOOTSTEP("footstep", 28, -1),
    /**
     * A particle effect which is displayed when a mob dies:
     * <ul>
     * <li>It looks like a large white cloud
     * <li>The speed value influences the velocity at which the particle flies off
     * </ul>
     */
    CLOUD("cloud", 29, -1, ParticleProperty.DIRECTIONAL),
    /**
     * A particle effect which is displayed by redstone ore, powered redstone, redstone torches and redstone repeaters:
     * <ul>
     * <li>It looks like a tiny colored cloud
     * <li>The speed value causes the particle to be colored red when set to 0
     * </ul>
     */
    REDSTONE("reddust", 30, -1, ParticleProperty.COLORABLE),
    /**
     * A particle effect which is displayed when snowballs hit a block:
     * <ul>
     * <li>It looks like a little piece with the snowball texture
     * <li>The speed value has no influence on this particle effect
     * </ul>
     */
    SNOWBALL("snowballpoof", 31, -1),
    /**
     * A particle effect which is currently unused:
     * <ul>
     * <li>It looks like a tiny white cloud
     * <li>The speed value influences the velocity at which the particle flies off
     * </ul>
     */
    SNOW_SHOVEL("snowshovel", 32, -1, ParticleProperty.DIRECTIONAL),
    /**
     * A particle effect which is displayed by slimes:
     * <ul>
     * <li>It looks like a tiny part of the slimeball icon
     * <li>The speed value has no influence on this particle effect
     * </ul>
     */
    SLIME("slime", 33, -1),
    /**
     * A particle effect which is displayed when breeding and taming animals:
     * <ul>
     * <li>It looks like a red heart
     * <li>The speed value has no influence on this particle effect
     * </ul>
     */
    HEART("heart", 34, -1),
    /**
     * A particle effect which is displayed by barriers:
     * <ul>
     * <li>It looks like a red box with a slash through it
     * <li>The speed value has no influence on this particle effect
     * </ul>
     */
    BARRIER("barrier", 35, 8),
    /**
     * A particle effect which is displayed when breaking a tool or eggs hit a block:
     * <ul>
     * <li>It looks like a little piece with an item texture
     * </ul>
     */
    ITEM_CRACK("iconcrack", 36, -1, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_DATA),
    /**
     * A particle effect which is displayed when breaking blocks or sprinting:
     * <ul>
     * <li>It looks like a little piece with a block texture
     * <li>The speed value has no influence on this particle effect
     * </ul>
     */
    BLOCK_CRACK("blockcrack", 37, -1, ParticleProperty.REQUIRES_DATA),
    /**
     * A particle effect which is displayed when falling:
     * <ul>
     * <li>It looks like a little piece with a block texture
     * </ul>
     */
    BLOCK_DUST("blockdust", 38, 7, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_DATA),
    /**
     * A particle effect which is displayed when rain hits the ground:
     * <ul>
     * <li>It looks like a blue droplet
     * <li>The speed value has no influence on this particle effect
     * </ul>
     */
    WATER_DROP("droplet", 39, 8),
    /**
     * A particle effect which is currently unused:
     * <ul>
     * <li>It has no visual effect
     * </ul>
     */
    ITEM_TAKE("take", 40, 8),
    /**
     * A particle effect which is displayed by elder guardians:
     * <ul>
     * <li>It looks like the shape of the elder guardian
     * <li>The speed value has no influence on this particle effect
     * <li>The offset values have no influence on this particle effect
     * </ul>
     */
    MOB_APPEARANCE("mobappearance", 41, 8);

    private static final Map<String, ParticleEffect> NAME_MAP = new HashMap<>();
    private static final Map<Integer, ParticleEffect> ID_MAP = new HashMap<>();
    private final String name;
    private final int id;
    private final int requiredVersion;
    private final EnumSet<ParticleProperty> properties;

    // Initialize map for quick name and id lookup
    static {
        for (ParticleEffect effect : values()) {
            NAME_MAP.put(effect.name.toLowerCase(), effect);
            ID_MAP.put(effect.id, effect);
        }
    }

    /**
     * Construct a new particle effect
     *
     * @param name Name of this particle effect
     * @param id Id of this particle effect
     * @param requiredVersion Version which is required (1.x)
     * @param properties Properties of this particle effect
     */
    ParticleEffect(String name, int id, int requiredVersion, ParticleProperty... properties) {
        this.name = name;
        this.id = id;
        this.requiredVersion = requiredVersion;
        this.properties = EnumSet.noneOf(ParticleProperty.class);
        this.properties.addAll(Arrays.asList(properties));
    }

    /**
     * Returns the name of this particle effect
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the id of this particle effect
     *
     * @return The id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the required version for this particle effect (1.x)
     *
     * @return The required version
     */
    public int getRequiredVersion() {
        return requiredVersion;
    }

    /**
     * Determine if this particle effect has a specific property
     *
     * @return Whether it has the property or not
     */
    public boolean hasProperty(ParticleProperty property) {
        return properties.contains(property);
    }

    /**
     * Determine if this particle effect is supported by your current server version
     *
     * @return Whether the particle effect is supported or not
     */
    public boolean isSupported() {
        return requiredVersion == -1 || NmsVersion.MINOR >= requiredVersion;
    }

    /**
     * Returns the particle effect with the given name
     *
     * @param name Name of the particle effect
     * @return The particle effect
     */
    public static ParticleEffect fromName(String name) {
        return NAME_MAP.get(name.toLowerCase());
    }

    /**
     * Returns the particle effect with the given id
     *
     * @param id Id of the particle effect
     * @return The particle effect
     */
    public static ParticleEffect fromId(int id) {
        return ID_MAP.get(id);
    }

    /**
     * Determine if water is at a certain location
     *
     * @param location Location to check
     * @return Whether water is at this location or not
     */
    private static boolean isWater(Location location) {
        Material material = location.getBlock().getType();
        return material == Material.WATER || material == Material.STATIONARY_WATER;
    }

    /**
     * Determine if the distance between @param location and one of the players exceeds 256
     *
     * @param location Location to check
     * @return Whether the distance exceeds 256 or not
     */
    private static boolean isLongDistance(Location location, Iterable<Player> players) {
        World world = location.getWorld();
        for (Player player : players) {
            Location playerLocation = player.getLocation();
            if (world != playerLocation.getWorld() || playerLocation.distanceSquared(location) < 65536) {
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     * Determine if the data type for a particle effect is correct
     *
     * @param effect Particle effect
     * @param data Particle data
     * @return Whether the data type is correct or not
     */
    private static boolean isDataCorrect(ParticleEffect effect, ParticleData data) {
        return ((effect == BLOCK_CRACK || effect == BLOCK_DUST) && data instanceof ParticleBlockData) || (effect == ITEM_CRACK && data instanceof ParticleItemData);
    }

    /**
     * Displays a particle effect which is only visible for all players within a certain range in the world of @param center
     *
     * @param offsetX Maximum distance particles can fly away from the center on the x-axis
     * @param offsetY Maximum distance particles can fly away from the center on the y-axis
     * @param offsetZ Maximum distance particles can fly away from the center on the z-axis
     * @param speed Display speed of the particles
     * @param amount Amount of particles
     * @param center Center location of the effect
     * @param range Range of the visibility
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleDataException If the particle effect requires additional data
     * @throws IllegalArgumentException If the particle effect requires water and none is at the center location
     * @see ParticlePacket
     * @see ParticlePacket#sendTo(Location, double)
     */
    public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range) {
        if (!isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect requires additional data");
        }
        if (hasProperty(ParticleProperty.REQUIRES_WATER) && !isWater(center)) {
            throw new IllegalArgumentException("There is no water at the center location");
        }
        new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, range > 256, null).sendTo(center, range);
    }

    /**
     * Displays a particle effect which is only visible for the specified players
     *
     * @param offsetX Maximum distance particles can fly away from the center on the x-axis
     * @param offsetY Maximum distance particles can fly away from the center on the y-axis
     * @param offsetZ Maximum distance particles can fly away from the center on the z-axis
     * @param speed Display speed of the particles
     * @param amount Amount of particles
     * @param center Center location of the effect
     * @param players Receivers of the effect
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleDataException If the particle effect requires additional data
     * @throws IllegalArgumentException If the particle effect requires water and none is at the center location
     * @see ParticlePacket
     * @see ParticlePacket#sendTo(Location, List)
     */
    public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, Iterable<Player> players) {
        if (!isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect requires additional data");
        }
        if (hasProperty(ParticleProperty.REQUIRES_WATER) && !isWater(center)) {
            throw new IllegalArgumentException("There is no water at the center location");
        }

        new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, true, null).sendTo(center, players);
    }

    /**
     * Displays a particle effect which is only visible for the specified players
     *
     * @param offsetX Maximum distance particles can fly away from the center on the x-axis
     * @param offsetY Maximum distance particles can fly away from the center on the y-axis
     * @param offsetZ Maximum distance particles can fly away from the center on the z-axis
     * @param speed Display speed of the particles
     * @param amount Amount of particles
     * @param center Center location of the effect
     * @param players Receivers of the effect
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleDataException If the particle effect requires additional data
     * @throws IllegalArgumentException If the particle effect requires water and none is at the center location
     * @see ParticlePacket
     * @see ParticlePacket#sendTo(Location, List)
     */
    public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, Stream<Player> players) {
        if (!isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect requires additional data");
        }
        if (hasProperty(ParticleProperty.REQUIRES_WATER) && !isWater(center)) {
            throw new IllegalArgumentException("There is no water at the center location");
        }
        new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, true, null).sendTo(center, players);
    }

    /**
     * Displays a particle effect which is only visible for the specified players
     *
     * @param offsetX Maximum distance particles can fly away from the center on the x-axis
     * @param offsetY Maximum distance particles can fly away from the center on the y-axis
     * @param offsetZ Maximum distance particles can fly away from the center on the z-axis
     * @param speed Display speed of the particles
     * @param amount Amount of particles
     * @param center Center location of the effect
     * @param players Receivers of the effect
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleDataException If the particle effect requires additional data
     * @throws IllegalArgumentException If the particle effect requires water and none is at the center location
     * @see #display(float, float, float, float, int, Location, List)
     */
    public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, Player... players) {
        display(offsetX, offsetY, offsetZ, speed, amount, center, Arrays.asList(players));
    }

    /**
     * Displays a single particle which flies into a determined direction and is only visible for all players within a certain range in the world of @param center
     *
     * @param direction Direction of the particle
     * @param speed Display speed of the particle
     * @param center Center location of the effect
     * @param range Range of the visibility
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleDataException If the particle effect requires additional data
     * @throws IllegalArgumentException If the particle effect is not directional or if it requires water and none is at the center location
     * @see ParticlePacket#ParticlePacket(ParticleEffect, Vector, float, boolean, ParticleData)
     * @see ParticlePacket#sendTo(Location, double)
     */
    public void display(Vector direction, float speed, Location center, double range) {
        if (!isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect requires additional data");
        }
        if (!hasProperty(ParticleProperty.DIRECTIONAL)) {
            throw new IllegalArgumentException("This particle effect is not directional");
        }
        if (hasProperty(ParticleProperty.REQUIRES_WATER) && !isWater(center)) {
            throw new IllegalArgumentException("There is no water at the center location");
        }
        new ParticlePacket(this, direction, speed, range > 256, null).sendTo(center, range);
    }

    /**
     * Displays a single particle which flies into a determined direction and is only visible for the specified players
     *
     * @param direction Direction of the particle
     * @param speed Display speed of the particle
     * @param center Center location of the effect
     * @param players Receivers of the effect
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleDataException If the particle effect requires additional data
     * @throws IllegalArgumentException If the particle effect is not directional or if it requires water and none is at the center location
     * @see ParticlePacket#ParticlePacket(ParticleEffect, Vector, float, boolean, ParticleData)
     * @see ParticlePacket#sendTo(Location, List)
     */
    public void display(Vector direction, float speed, Location center, Iterable<Player> players) {
        if (!isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect requires additional data");
        }
        if (!hasProperty(ParticleProperty.DIRECTIONAL)) {
            throw new IllegalArgumentException("This particle effect is not directional");
        }
        if (hasProperty(ParticleProperty.REQUIRES_WATER) && !isWater(center)) {
            throw new IllegalArgumentException("There is no water at the center location");
        }
        new ParticlePacket(this, direction, speed, true, null).sendTo(center, players);
    }

    /**
     * Displays a single particle which flies into a determined direction and is only visible for the specified players
     *
     * @param direction Direction of the particle
     * @param speed Display speed of the particle
     * @param center Center location of the effect
     * @param players Receivers of the effect
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleDataException If the particle effect requires additional data
     * @throws IllegalArgumentException If the particle effect is not directional or if it requires water and none is at the center location
     * @see ParticlePacket#ParticlePacket(ParticleEffect, Vector, float, boolean, ParticleData)
     * @see ParticlePacket#sendTo(Location, List)
     */
    public void display(Vector direction, float speed, Location center, Stream<Player> players) {
        if (!isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect requires additional data");
        }
        if (!hasProperty(ParticleProperty.DIRECTIONAL)) {
            throw new IllegalArgumentException("This particle effect is not directional");
        }
        if (hasProperty(ParticleProperty.REQUIRES_WATER) && !isWater(center)) {
            throw new IllegalArgumentException("There is no water at the center location");
        }
        new ParticlePacket(this, direction, speed, true, null).sendTo(center, players);
    }

    /**
     * Displays a single particle which flies into a determined direction and is only visible for the specified players
     *
     * @param direction Direction of the particle
     * @param speed Display speed of the particle
     * @param center Center location of the effect
     * @param players Receivers of the effect
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleDataException If the particle effect requires additional data
     * @throws IllegalArgumentException If the particle effect is not directional or if it requires water and none is at the center location
     * @see #display(Vector, float, Location, List)
     */
    public void display(Vector direction, float speed, Location center, Player... players) {
        display(direction, speed, center, Arrays.asList(players));
    }

    /**
     * Displays a single particle which is colored and only visible for all players within a certain range in the world of @param center
     *
     * @param color Color of the particle
     * @param center Center location of the effect
     * @param range Range of the visibility
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleColorException If the particle effect is not colorable or the color type is incorrect
     * @see ParticlePacket#ParticlePacket(ParticleEffect, ParticleColor, boolean)
     * @see ParticlePacket#sendTo(Location, double)
     */
    public void display(ParticleColor color, Location center, double range) {
        if (!isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (!hasProperty(ParticleProperty.COLORABLE)) {
            throw new ParticleColorException("This particle effect is not colorable");
        }
        new ParticlePacket(this, color, range > 256).sendTo(center, range);
    }

    /**
     * Displays a single particle which is colored and only visible for the specified players
     *
     * @param color Color of the particle
     * @param center Center location of the effect
     * @param players Receivers of the effect
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleColorException If the particle effect is not colorable or the color type is incorrect
     * @see ParticlePacket#ParticlePacket(ParticleEffect, ParticleColor, boolean)
     * @see ParticlePacket#sendTo(Location, List)
     */
    public void display(ParticleColor color, Location center, Iterable<Player> players) {
        if (!isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (!hasProperty(ParticleProperty.COLORABLE)) {
            throw new ParticleColorException("This particle effect is not colorable");
        }
        new ParticlePacket(this, color, true).sendTo(center, players);
    }

    /**
     * Displays a single particle which is colored and only visible for the specified players
     *
     * @param color Color of the particle
     * @param center Center location of the effect
     * @param players Receivers of the effect
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleColorException If the particle effect is not colorable or the color type is incorrect
     * @see #display(ParticleColor, Location, List)
     */
    public void display(ParticleColor color, Location center, Player... players) {
        display(color, center, Arrays.asList(players));
    }

    /**
     * Displays a particle effect which requires additional data and is only visible for all players within a certain range in the world of @param center
     *
     * @param data Data of the effect
     * @param offsetX Maximum distance particles can fly away from the center on the x-axis
     * @param offsetY Maximum distance particles can fly away from the center on the y-axis
     * @param offsetZ Maximum distance particles can fly away from the center on the z-axis
     * @param speed Display speed of the particles
     * @param amount Amount of particles
     * @param center Center location of the effect
     * @param range Range of the visibility
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleDataException If the particle effect does not require additional data or if the data type is incorrect
     * @see ParticlePacket
     * @see ParticlePacket#sendTo(Location, double)
     */
    public void display(ParticleData data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range) {
        if (!isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (!hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect does not require additional data");
        }
        if (!isDataCorrect(this, data)) {
            throw new ParticleDataException("The particle data type is incorrect");
        }
        new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, range > 256, data).sendTo(center, range);
    }

    /**
     * Displays a particle effect which requires additional data and is only visible for the specified players
     *
     * @param data Data of the effect
     * @param offsetX Maximum distance particles can fly away from the center on the x-axis
     * @param offsetY Maximum distance particles can fly away from the center on the y-axis
     * @param offsetZ Maximum distance particles can fly away from the center on the z-axis
     * @param speed Display speed of the particles
     * @param amount Amount of particles
     * @param center Center location of the effect
     * @param players Receivers of the effect
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleDataException If the particle effect does not require additional data or if the data type is incorrect
     * @see ParticlePacket
     * @see ParticlePacket#sendTo(Location, List)
     */
    public void display(ParticleData data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, Iterable<Player> players) {
        if (!isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (!hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect does not require additional data");
        }
        if (!isDataCorrect(this, data)) {
            throw new ParticleDataException("The particle data type is incorrect");
        }
        new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, true, data).sendTo(center, players);
    }

    /**
     * Displays a particle effect which requires additional data and is only visible for the specified players
     *
     * @param data Data of the effect
     * @param offsetX Maximum distance particles can fly away from the center on the x-axis
     * @param offsetY Maximum distance particles can fly away from the center on the y-axis
     * @param offsetZ Maximum distance particles can fly away from the center on the z-axis
     * @param speed Display speed of the particles
     * @param amount Amount of particles
     * @param center Center location of the effect
     * @param players Receivers of the effect
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleDataException If the particle effect does not require additional data or if the data type is incorrect
     * @see #display(ParticleData, float, float, float, float, int, Location, List)
     */
    public void display(ParticleData data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, Player... players) {
        display(data, offsetX, offsetY, offsetZ, speed, amount, center, Arrays.asList(players));
    }

    /**
     * Displays a single particle which requires additional data that flies into a determined direction and is only visible for all players within a certain range in the world of @param center
     *
     * @param data Data of the effect
     * @param direction Direction of the particle
     * @param speed Display speed of the particles
     * @param center Center location of the effect
     * @param range Range of the visibility
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleDataException If the particle effect does not require additional data or if the data type is incorrect
     * @see ParticlePacket
     * @see ParticlePacket#sendTo(Location, double)
     */
    public void display(ParticleData data, Vector direction, float speed, Location center, double range) {
        if (!isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (!hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect does not require additional data");
        }
        if (!isDataCorrect(this, data)) {
            throw new ParticleDataException("The particle data type is incorrect");
        }
        new ParticlePacket(this, direction, speed, range > 256, data).sendTo(center, range);
    }

    /**
     * Displays a single particle which requires additional data that flies into a determined direction and is only visible for the specified players
     *
     * @param data Data of the effect
     * @param direction Direction of the particle
     * @param speed Display speed of the particles
     * @param center Center location of the effect
     * @param players Receivers of the effect
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleDataException If the particle effect does not require additional data or if the data type is incorrect
     * @see ParticlePacket
     * @see ParticlePacket#sendTo(Location, List)
     */
    public void display(ParticleData data, Vector direction, float speed, Location center, Iterable<Player> players) {
        if (!isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (!hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect does not require additional data");
        }
        if (!isDataCorrect(this, data)) {
            throw new ParticleDataException("The particle data type is incorrect");
        }
        new ParticlePacket(this, direction, speed, true, data).sendTo(center, players);
    }

    /**
     * Displays a single particle which requires additional data that flies into a determined direction and is only visible for the specified players
     *
     * @param data Data of the effect
     * @param direction Direction of the particle
     * @param speed Display speed of the particles
     * @param center Center location of the effect
     * @param players Receivers of the effect
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleDataException If the particle effect does not require additional data or if the data type is incorrect
     * @see ParticlePacket
     * @see ParticlePacket#sendTo(Location, List)
     */
    public void display(ParticleData data, Vector direction, float speed, Location center, Stream<Player> players) {
        if (!isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (!hasProperty(ParticleProperty.REQUIRES_DATA)) {
            throw new ParticleDataException("This particle effect does not require additional data");
        }
        if (!isDataCorrect(this, data)) {
            throw new ParticleDataException("The particle data type is incorrect");
        }
        new ParticlePacket(this, direction, speed, true, data).sendTo(center, players);
    }

    /**
     * Displays a single particle which requires additional data that flies into a determined direction and is only visible for the specified players
     *
     * @param data Data of the effect
     * @param direction Direction of the particle
     * @param speed Display speed of the particles
     * @param center Center location of the effect
     * @param players Receivers of the effect
     * @throws ParticleVersionException If the particle effect is not supported by the server version
     * @throws ParticleDataException If the particle effect does not require additional data or if the data type is incorrect
     * @see #display(ParticleData, Vector, float, Location, List)
     */
    public void display(ParticleData data, Vector direction, float speed, Location center, Player... players) {
        display(data, direction, speed, center, Arrays.asList(players));
    }

    /**
     * Represents the property of a particle effect
     * <p>
     * This class is part of the <b>ParticleEffect Library</b> and follows the same usage conditions
     *
     * @author DarkBlade12
     * @since 1.7
     */
    public enum ParticleProperty {
        /**
         * The particle effect requires water to be displayed
         */
        REQUIRES_WATER,
        /**
         * The particle effect requires block or item data to be displayed
         */
        REQUIRES_DATA,
        /**
         * The particle effect uses the offsets as direction values
         */
        DIRECTIONAL,
        /**
         * The particle effect uses the offsets as color values
         */
        COLORABLE;
    }
}
