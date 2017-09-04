package xyz.upperlevel.uppercore.particle;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import xyz.upperlevel.uppercore.particle.impl.BlockDustParticle;
import xyz.upperlevel.uppercore.particle.impl.SimpleParticle;
import xyz.upperlevel.uppercore.config.Config;

import java.util.HashMap;
import java.util.Map;

public enum ParticleType {

    SIMPLE {
        @Override
        public Particle create() {
            return new SimpleParticle();
        }

        @Override
        public Particle create(Config data) {
            return new SimpleParticle(data);
        }
    },
    BLOCK_DUST {
        @Override
        public Particle create() {
            return new BlockDustParticle();
        }

        @Override
        public Particle create(Config data) {
            return new BlockDustParticle(data);
        }
    };

    public abstract Particle create();

    public abstract Particle create(Config data);

    private static final Map<String, ParticleType> BY_NAME = new HashMap<>();

    static {
        for (ParticleType value : values())
            BY_NAME.put(value.name(), value);
    }

    public static ParticleType get(String name) {
        return BY_NAME.get(name);
    }
}
