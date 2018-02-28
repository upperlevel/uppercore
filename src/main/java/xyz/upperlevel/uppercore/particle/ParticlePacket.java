package xyz.upperlevel.uppercore.particle;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.upperlevel.uppercore.particle.data.ParticleData;
import xyz.upperlevel.uppercore.particle.exceptions.PacketInstantiationException;
import xyz.upperlevel.uppercore.particle.exceptions.PacketSendingException;
import xyz.upperlevel.uppercore.util.nms.NmsPacket;
import xyz.upperlevel.uppercore.util.nms.NmsVersion;
import xyz.upperlevel.uppercore.util.nms.exceptions.UnsupportedVersionException;
import xyz.upperlevel.uppercore.util.nms.impl.entity.PlayerNms;

import java.lang.reflect.Constructor;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Represents a particle effect packet with all attributes which is used for sending packets to the players.
 */
public class ParticlePacket {
    private static Class<?> particlePacketClass;
    private static PacketCreator packetCreator;

    private final ParticleEffect effect;
    /**
     * Values used for direction or colour.
     * @see ParticleEffect.ParticleProperty
     */
    private final float offsetX, offsetY, offsetZ;
    private final float speed;
    private final int amount;
    private final boolean longDistance;
    private final ParticleData data;
    private Object packet;

    static {
        try {
            particlePacketClass = NmsPacket.NMS.getClass(NmsVersion.MINOR < 7 ? "Packet63WorldParticles" : "PacketPlayOutWorldParticles");
            packetCreator = NmsVersion.MINOR < 8 ? oldPacketConstructor() : newPacketConstructor();
        } catch (Exception exception) {
            throw new UnsupportedVersionException(exception);
        }
    }

    /**
     * Construct a new particle packet.
     *
     * @param effect particle effect
     * @param offsetX maximum distance particles can fly away from the center on the x-axis
     * @param offsetY maximum distance particles can fly away from the center on the y-axis
     * @param offsetZ maximum distance particles can fly away from the center on the z-axis
     * @param speed display speed of the particles
     * @param amount amount of particles
     * @param longDistance indicates whether the maximum distance is increased from 256 to 65536
     * @param data data of the effect
     * @throws IllegalArgumentException if the speed or amount is lower than 0
     */
    public ParticlePacket(ParticleEffect effect, float offsetX, float offsetY, float offsetZ, float speed, int amount, boolean longDistance, ParticleData data) throws IllegalArgumentException {
        if (speed < 0) {
            throw new IllegalArgumentException("The speed is lower than 0");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("The amount is lower than 0");
        }
        this.effect = effect;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.amount = amount;
        this.longDistance = longDistance;
        this.data = data;
    }

    /**
     * Construct a new particle packet of a single particle flying into a determined direction.
     *
     * @param effect particle effect
     * @param direction direction of the particle
     * @param speed display speed of the particle
     * @param longDistance indicates whether the maximum distance is increased from 256 to 65536
     * @param data data of the effect
     * @throws IllegalArgumentException if the speed is lower than 0
     */
    public ParticlePacket(ParticleEffect effect, Vector direction, float speed, boolean longDistance, ParticleData data) throws IllegalArgumentException {
        this(effect, (float) direction.getX(), (float) direction.getY(), (float) direction.getZ(), speed, 0, longDistance, data);
    }

    /**
     * Construct a new particle packet of a single colored particle.
     *
     * @param effect particle effect
     * @param color color of the particle
     * @param longDistance indicates whether the maximum distance is increased from 256 to 65536
     */
    public ParticlePacket(ParticleEffect effect, ParticleColor color, boolean longDistance) {
        this(
                effect,
                (effect == ParticleEffect.REDSTONE && color.r == 0 ? Float.MIN_NORMAL : color.r),
                color.g,
                color.b,
                1,
                0,
                longDistance,
                null
        );
    }

    /**
     * Initializes {@link #packet}.
     *
     * @param center center location of the effect
     * @throws PacketInstantiationException if instantiation fails due to an unknown error
     */
    private void initializePacket(Location center) throws PacketInstantiationException {
        if (packet != null) return;// Already initialized
        try {
            packet = packetCreator.create(effect, data, longDistance, (float)center.getX(), (float)center.getY(), (float)center.getZ(), offsetX, offsetY, offsetZ, speed, amount);
        } catch (Exception exception) {
            throw new PacketInstantiationException("Packet instantiation failed", exception);
        }
    }

    /**
     * Sends the packet to a single player.
     *
     * @param center center location of the effect
     * @param player receiver of the packet
     * @throws PacketInstantiationException if instantiation fails due to an unknown error
     * @throws PacketSendingException if sending fails due to an unknown error
     */
    public void sendTo(Location center, Player player) throws PacketInstantiationException, PacketSendingException {
        initializePacket(center);
        PlayerNms.sendPacket(player, packet);
    }

    /**
     * Sends the packet to all players in the list.
     *
     * @param center center location of the effect
     * @param players receivers of the packet
     */
    public void sendTo(Location center, Iterable<Player> players) {
        for (Player player : players) {
            sendTo(center, player);
        }
    }

    /**
     * Sends the packet to all players in the stream.
     *
     * @param center center location of the effect
     * @param players receivers of the packet
     * @throws IllegalArgumentException if the player list is empty
     */
    public void sendTo(Location center, Stream<Player> players) throws IllegalArgumentException {
        players.forEach(p -> sendTo(center, players));
    }

    /**
     * Sends the packet to all players in a certain range.
     *
     * @param center center location of the effect
     * @param range range in which players will receive the packet (Maximum range for particles is usually 16, but it can differ for some types)
     * @throws IllegalArgumentException if the range is lower than 1
     */
    public void sendTo(Location center, double range) throws IllegalArgumentException {
        if (range < 1.0) {
            throw new IllegalArgumentException("The range is lower than 1");
        }
        forEveryoneAround(center, range, p -> sendTo(center, p));
    }


    /**
     * Executes an action for every player that is in the range.
     *
     * @param center the action sphere center
     * @param radius the action sphere radius
     * @param action the action to execute for every player
     */
    private void forEveryoneAround(Location center, double radius, Consumer<Player> action) {
        /*
         * TODO: To remove some checking we could divide the iterated chunks
         * in those wo are surely inside the circle and those who touch the circle edge
         * the first ones don't need any testing while the other ones do
         */
        int chunkRadius = (int) Math.ceil(radius) >> 4;
        double squared = radius * radius;
        final int x = center.getBlockX() >> 4;
        final int z = center.getBlockZ() >> 4;

        int sx = x - chunkRadius;
        int ex = x + chunkRadius;

        int sz = z - chunkRadius;
        int ez = z + chunkRadius;

        final World world = center.getWorld();
        for (int chX = sx; chX <= ex; chX++) {
            for (int chZ = sz; chZ <= ez; chZ++) {
                if(world.isChunkLoaded(chX, chZ)) {
                    for (Entity e : world.getChunkAt(chX, chZ).getEntities()) {
                        if (e instanceof Player && e.getLocation().distanceSquared(center) <= squared) {
                            action.accept((Player) e);
                        }
                    }
                }
            }
        }
    }

    /**
     * Class that creates the particle interacting with Bukkit's NMS code.
     */
    private interface PacketCreator {
        Object create(ParticleEffect effect, ParticleData data, boolean longDistance, float posX, float posY, float posZ, float offsetX, float offsetY, float offsetZ, float speed, int amount) throws Exception;
    }

    /**
     * Creates a {@link PacketCreator} compatible with Bukkit < 1.8.
     *
     * @return a < 1.8 compatible {@link PacketCreator}
     * @throws NoSuchMethodException if the version isn't compatible
     */
    private static PacketCreator oldPacketConstructor() throws NoSuchMethodException {
        final Constructor<?> constructor = particlePacketClass.getConstructor(
                String.class,
                Float.TYPE, Float.TYPE, Float.TYPE,
                Float.TYPE, Float.TYPE, Float.TYPE,
                Float.TYPE,
                Integer.TYPE
        );
        return (effect, data, longDistance, posX, posY, posZ, offsetX, offsetY, offsetZ, speed, amount) -> {
            String name = effect.getName();
            if(data != null)
                name += data.getPacketDataString();
            return constructor.newInstance(
                    name,
                    posX, posY, posZ,
                    offsetX, offsetY, offsetZ,
                    speed,
                    amount
            );
        };
    }

    /**
     * Creates a {@link PacketCreator} compatible with Bukkit 1.9 or older.
     *
     * @return a 1.9+ compatible {@link PacketCreator}
     * @throws NoSuchMethodException if the version isn't compatible
     * @throws ClassNotFoundException if the version isn't compatible
     */
    private static PacketCreator newPacketConstructor() throws NoSuchMethodException, ClassNotFoundException {
        Class<?> enumParticle =  NmsPacket.NMS.getClass("EnumParticle");
        Object[] particleValues = enumParticle.getEnumConstants();
        final Constructor<?> constructor = particlePacketClass.getConstructor(
                enumParticle,
                Boolean.TYPE,
                Float.TYPE, Float.TYPE, Float.TYPE,
                Float.TYPE, Float.TYPE, Float.TYPE,
                Float.TYPE,
                Integer.TYPE,
                int[].class
        );
        return (effect, data, longDistance, posX, posY, posZ, offsetX, offsetY, offsetZ, speed, amount) -> {
            int[] rawData;
            if (data != null) {
                int[] packetData = data.getPacketData();
                rawData = effect == ParticleEffect.ITEM_CRACK ? packetData : new int[] { packetData[0] | (packetData[1] << 12) };
            } else rawData = null;


            return constructor.newInstance(
                    particleValues[effect.getId()],
                    longDistance,
                    posX, posY, posZ,
                    offsetX, offsetY, offsetZ,
                    speed,
                    amount,
                    rawData
            );
        };
    }
}