package xyz.upperlevel.uppercore.particle.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;
import xyz.upperlevel.uppercore.particle.CustomParticle;
import xyz.upperlevel.uppercore.particle.ParticleType;
import xyz.upperlevel.uppercore.particle.ParticleUtil;

import java.util.List;

/**
 * Particle created from the dust of a specific block.
 * Supports both block material and block data (for colored wool or similar)
 */
public class BlockDustParticle extends CustomParticle {
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

    public BlockDustParticle() {
        super(ParticleType.BLOCK_DUST);

        setBlockType(Material.WOOL);
        setBlockData((byte) 0);
    }

    public BlockDustParticle(Config data) {
        super(ParticleType.BLOCK_DUST, data);
        Config block = data.getConfigRequired("block");
        Material type = block.getMaterialRequired("type");
        if (!type.isBlock()) {
            throw new InvalidConfigException("Particle must be a block!", "in particle '" + ParticleType.BLOCK_DUST.name() + "'");
        }
        setBlockType(type);
        setBlockData(block.getByte("data", (byte)0));
    }

    /**
     * Changes the type of the block.
     * @param blockType the displayed block's type
     */
    public void setBlockType(Material blockType) {
        this.blockType = blockType == null ? Material.WOOL : blockType;
    }

    @Override
    public void display(Location location, List<Player> players) {
        ParticleUtil.builder()
                .type(Particle.BLOCK_DUST)
                .center(location)
                .amount(getAmount())
                .offset(offsetX, offsetY, offsetZ)
                .speed(getSpeed())
                .material(blockType, blockData)
                .display(players);
    }
}
