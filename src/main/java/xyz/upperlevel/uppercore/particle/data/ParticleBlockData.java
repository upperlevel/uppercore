package xyz.upperlevel.uppercore.particle.data;

import org.bukkit.Material;

/**
 * Represents the block data for the {@link xyz.upperlevel.uppercore.particle.ParticleEffect#BLOCK_CRACK} and {@link xyz.upperlevel.uppercore.particle.ParticleEffect#BLOCK_DUST} effects
 */
public class ParticleBlockData extends ParticleData {
    /**
     * Construct a new block data
     *
     * @param material material of the block
     * @param data data value of the block
     * @throws IllegalArgumentException if the material is not a block
     * @see ParticleData#ParticleData(Material, byte)
     */
    public ParticleBlockData(Material material, byte data) throws IllegalArgumentException {
        super(material, data);
        if (!material.isBlock())
            throw new IllegalArgumentException("The material + '" + material.name() + "' is not a block");
    }
}