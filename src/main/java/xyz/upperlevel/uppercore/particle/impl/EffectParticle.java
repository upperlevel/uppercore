package xyz.upperlevel.uppercore.particle.impl;

import xyz.upperlevel.uppercore.particle.Particle;
import xyz.upperlevel.uppercore.particle.ParticleEffect;
import xyz.upperlevel.uppercore.particle.ParticleType;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;

public abstract class EffectParticle extends Particle {
    public EffectParticle(ParticleType type, ParticleEffect effect) {
        super(type);
        if(!effect.isSupported())
            throw new IllegalArgumentException("Unsupported particle: " + type.name());
    }

    public EffectParticle(ParticleType type, Config data, ParticleEffect effect) {
        super(type, data);
        if(!effect.isSupported())
            throw new InvalidConfigException("Unsupported particle: " + type.name());
    }
}
