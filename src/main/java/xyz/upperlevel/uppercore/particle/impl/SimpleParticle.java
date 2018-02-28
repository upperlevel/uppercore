package xyz.upperlevel.uppercore.particle.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.particle.ParticleColor;
import xyz.upperlevel.uppercore.particle.ParticleType;
import xyz.upperlevel.uppercore.config.Config;

import static xyz.upperlevel.uppercore.particle.ParticleEffect.REDSTONE;

/**
 * A simple coloured particle.
 *
 * It is constructed by colouring a redstone particle.
 */
public class SimpleParticle extends EffectParticle {

    /**
     * Color of the particle.
     * @return the particle color
     */
    @Getter
    private ParticleColor color;

    public SimpleParticle() {
        super(ParticleType.SIMPLE, REDSTONE);

        setColor(Color.WHITE);
    }

    public SimpleParticle(Config data) {
        super(ParticleType.SIMPLE, data, REDSTONE);
        setColor(data.getColor("color", Color.WHITE));
    }

    /**
     * Changes the color of the particle.
     * @param color the new particle color
     */
    public void setColor(Color color) {
        this.color = ParticleColor.of(color);
    }

    /**
     * Changes the color of the particle.
     * @param color the new particle color
     */
    public void setColor(ParticleColor color) {
        this.color = color;
    }

    @Override
    public void display(Location loc, Iterable<Player> players) {
        REDSTONE.display(
                color,
                loc,
                players
        );
    }
}
