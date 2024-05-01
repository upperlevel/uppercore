package xyz.upperlevel.uppercore.particle.impl;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;
import xyz.upperlevel.uppercore.particle.CustomParticle;
import xyz.upperlevel.uppercore.particle.ParticleType;
import xyz.upperlevel.uppercore.particle.ParticleUtil;

import java.util.List;

/**
 * Particle created from the dust of a specific block.
 */
public class BlockDustParticle extends CustomParticle {
    /**
     * Type of the block this particle will display.
     * @return the displayed block's type
     */
    @Getter
    private Material blockType;

    public BlockDustParticle() {
        super(ParticleType.BLOCK_DUST);

        setBlockType(Material.WHITE_WOOL);
    }

    @ConfigConstructor
    public BlockDustParticle(
            @ConfigProperty(value = "offset.x", optional = true) Float offsetX,
            @ConfigProperty(value = "offset.y", optional = true) Float offsetY,
            @ConfigProperty(value = "offset.z", optional = true) Float offsetZ,
            @ConfigProperty(value = "speed", optional = true) Float speed,
            @ConfigProperty(value = "amount", optional = true) Integer amount,
            @ConfigProperty("block.type") Material blockType
    ) {
        super(ParticleType.BLOCK_DUST, offsetX, offsetY, offsetZ, speed, amount);
        if (!blockType.isBlock()) {
            throw new InvalidConfigException("Particle must be a block!", "in particle '" + ParticleType.BLOCK_DUST.name() + "'");
        }
        setBlockType(blockType);
    }

    /**
     * Changes the type of the block.
     * @param blockType the displayed block's type
     */
    public void setBlockType(Material blockType) {
        this.blockType = blockType == null ? Material.WHITE_WOOL : blockType;
    }

    @Override
    public void display(Location location, List<Player> players) {
        ParticleUtil.builder()
                .type(Particle.FALLING_DUST)
                .center(location)
                .amount(getAmount())
                .offset(offsetX, offsetY, offsetZ)
                .speed(getSpeed())
                .material(blockType)
                .display(players);
    }
}
