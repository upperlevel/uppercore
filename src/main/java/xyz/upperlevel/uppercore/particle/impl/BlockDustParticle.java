package xyz.upperlevel.uppercore.particle.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.particle.ParticleType;
import xyz.upperlevel.uppercore.particle.data.ParticleBlockData;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;

import static xyz.upperlevel.uppercore.particle.ParticleEffect.BLOCK_DUST;

/**
 * Particle created from the dust of a specific block.
 * Supports both block material and block data (for colored wool or similar)
 */
public class BlockDustParticle extends EffectParticle {
    /**
     * Type of the block this particle will display.
     * @return the displayed block's type
     */
    @Getter
    private Material blockType;

    /**
     * Data of the block this particle will display.
     * @param blockData the new displayed block data
     * @return the displayed block's data
     */
    @Getter
    @Setter
    private byte blockData;

    @Getter
    private ParticleBlockData data;

    public BlockDustParticle() {
        super(ParticleType.BLOCK_DUST, BLOCK_DUST);

        setBlockType(Material.WOOL);
        setBlockData((byte) 0);
        bake();
    }

    public BlockDustParticle(Config data) {
        super(ParticleType.BLOCK_DUST, data, BLOCK_DUST);
        Config block = data.getConfigRequired("block");
        Material type = block.getMaterialRequired("type");
        if (!type.isBlock()) {
            throw new InvalidConfigException("Particle must be a block!", "in particle '" + ParticleType.BLOCK_DUST.name() + "'");
        }
        setBlockType(type);
        setBlockData(block.getByte("data", (byte)0));
        bake();
    }

    /**
     * Changes the type of the block.
     * @param blockType the displayed block's type
     */
    public void setBlockType(Material blockType) {
        this.blockType = blockType == null ? Material.WOOL : blockType;
    }

    /**
     * Prepares the particle to be sent, updating the data.
     */
    public void bake() {
        data = new ParticleBlockData(blockType, blockData);
    }

    @Override
    public void display(Location location, Iterable<Player> players) {
        BLOCK_DUST.display(
                data,
                getOffsetX(),
                getOffsetY(),
                getOffsetZ(),
                getSpeed(),
                getAmount(),
                location,
                players
        );
    }
}
