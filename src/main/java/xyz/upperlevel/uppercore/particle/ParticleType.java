package xyz.upperlevel.uppercore.particle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.uppercore.particle.impl.BlockDustParticle;
import xyz.upperlevel.uppercore.particle.impl.SimpleParticle;
import xyz.upperlevel.uppercore.config.Config;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public enum ParticleType {
    SIMPLE(SimpleParticle.class) {
        @Override
        public CustomParticle create() {
            return new SimpleParticle();
        }
    },
    BLOCK_DUST(BlockDustParticle.class) {
        @Override
        public CustomParticle create() {
            return new BlockDustParticle();
        }
    };

    private static final Map<String, ParticleType> BY_NAME = new HashMap<>();

    @Getter
    private final Class<? extends CustomParticle> clazz;

    public abstract CustomParticle create();

    static {
        for (ParticleType value : values())
            BY_NAME.put(value.name(), value);
    }

    public static ParticleType get(String name) {
        return BY_NAME.get(name);
    }
}
