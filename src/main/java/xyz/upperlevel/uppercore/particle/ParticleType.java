package xyz.upperlevel.uppercore.particle;

import xyz.upperlevel.uppercore.particle.impl.BlockDustParticle;
import xyz.upperlevel.uppercore.particle.impl.SimpleParticle;
import xyz.upperlevel.uppercore.config.Config;

import java.util.HashMap;
import java.util.Map;

public enum ParticleType {

    SIMPLE {
        @Override
        public CustomParticle create() {
            return new SimpleParticle();
        }

        @Override
        public CustomParticle create(Config data) {
            return new SimpleParticle(data);
        }
    },
    BLOCK_DUST {
        @Override
        public CustomParticle create() {
            return new BlockDustParticle();
        }

        @Override
        public CustomParticle create(Config data) {
            return new BlockDustParticle(data);
        }
    };

    public abstract CustomParticle create();

    public abstract CustomParticle create(Config data);

    private static final Map<String, ParticleType> BY_NAME = new HashMap<>();

    static {
        for (ParticleType value : values())
            BY_NAME.put(value.name(), value);
    }

    public static ParticleType get(String name) {
        return BY_NAME.get(name);
    }
}
