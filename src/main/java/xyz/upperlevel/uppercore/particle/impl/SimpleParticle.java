package xyz.upperlevel.uppercore.particle.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.particle.CustomParticle;
import xyz.upperlevel.uppercore.particle.ParticleType;
import xyz.upperlevel.uppercore.particle.ParticleUtil;

import java.util.Arrays;
import java.util.List;

/**
 * A simple coloured particle.
 *
 * It is constructed by colouring a redstone particle.
 */
public class SimpleParticle extends CustomParticle {

    /**
     * Color of the particle.
     * @return the particle color
     */
    @Getter
    @Setter
    private Color color;

    public SimpleParticle() {
        super(ParticleType.SIMPLE);

        setColor(Color.WHITE);
    }

    @ConfigConstructor
    public SimpleParticle(
            @ConfigProperty(value = "offset.x", optional = true) Float offsetX,
            @ConfigProperty(value = "offset.y", optional = true) Float offsetY,
            @ConfigProperty(value = "offset.z", optional = true) Float offsetZ,
            @ConfigProperty(value = "speed", optional = true) Float speed,
            @ConfigProperty(value = "amount", optional = true) Integer amount,
            @ConfigProperty(value = "color", optional = true) Color color
    ) {
        super(ParticleType.SIMPLE, offsetX, offsetY, offsetZ, speed, amount);
        setColor(color != null ? color : Color.WHITE);
    }

    @Override
    public void display(Location loc, List<Player> players) {
        ParticleUtil.builder()
                .type(Particle.REDSTONE)
                .center(loc)
                .offset(offsetX, offsetY, offsetZ)
                .amount(getAmount())
                .speed(getSpeed())
                .color(getColor())
                .display(players);
    }
}
