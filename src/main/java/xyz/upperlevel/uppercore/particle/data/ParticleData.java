package xyz.upperlevel.uppercore.particle.data;

import lombok.Getter;
import org.bukkit.Material;

/**
 * Represents the particle data for effects.
 */
public abstract class ParticleData {
    /**
     * The material of this data.
     *
     * @return the material
     */
    @Getter
    private final Material material;

    /**
     * The data value of this data.
     *
     * @return the data value
     */
    @Getter
    private final byte data;

    /**
     * The data as an int array for packet construction.
     *
     * @return the data for the packet
     */
    @Getter
    private final int[] packetData;

    /**
     * Construct a new particle data.
     *
     * @param material material of the item/block
     * @param data data value of the item/block
     */
    @SuppressWarnings("deprecation")
    public ParticleData(Material material, byte data) {
        this.material = material;
        this.data = data;
        this.packetData = new int[] { material.getId(), data };
    }

    /**
     * Returns the data as a string for pre 1.8 versions.
     *
     * @return the data string for the packet
     */
    public String getPacketDataString() {
        return "_" + packetData[0] + "_" + packetData[1];
    }
}