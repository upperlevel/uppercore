package xyz.upperlevel.uppercore.particle.impl;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.particle.ParticleColor;
import xyz.upperlevel.uppercore.particle.ParticleType;
import xyz.upperlevel.uppercore.config.Config;

import static xyz.upperlevel.uppercore.particle.ParticleEffect.REDSTONE;

public class SimpleParticle extends EffectParticle {

    @Getter
    @Setter
    private ParticleColor color;

    public SimpleParticle() {
        super(ParticleType.SIMPLE, REDSTONE);

        setColor(Color.WHITE);
    }

    public SimpleParticle(Config data) {
        super(ParticleType.SIMPLE, data, REDSTONE);
        setColor(data.getColor("color", Color.WHITE));
    }

    public void setColor(Color color) {
        this.color = ParticleColor.of(color);
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
