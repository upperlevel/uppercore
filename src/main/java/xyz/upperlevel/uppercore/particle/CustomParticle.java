package xyz.upperlevel.uppercore.particle;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.config.PolymorphicSelector;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;

import java.util.List;

public abstract class CustomParticle {
    @Getter
    private final ParticleType type;

    @Getter
    @Setter
    protected float offsetX, offsetY, offsetZ;
    @Getter
    protected float speed;
    @Getter
    protected int amount;

    public CustomParticle(ParticleType type) {
        this.type = type;

        setOffset(0f,0f,0f);
        setSpeed(0f);
        setAmount(1);
    }

    public CustomParticle(
            ParticleType type,
            Float offsetX,
            Float offsetY,
            Float offsetZ,
            Float speed,
            Integer amount
    ) {
        this.type = type;
        setOffset(
                offsetX != null ? offsetX : 0f,
                offsetY != null ? offsetY : 0f,
                offsetZ != null ? offsetZ : 0f
        );
        setSpeed(speed != null ? speed : 0.05f);
        setAmount(amount != null ? amount : 10);
    }

    public void setOffset(float x, float y, float z) {
        offsetX = x;
        offsetY = y;
        offsetZ = z;
    }

    public void setSpeed(float speed) {
        this.speed = speed <= 0 ? 0 : speed;
    }

    public void setAmount(int amount) {
        this.amount = amount <= 0 ? 0 : amount;
    }

    public abstract void display(Location location, List<Player> phase);

    @PolymorphicSelector
    private static Class<? extends CustomParticle> selectChild(@ConfigProperty("type") String raw) {
        ParticleType type = ParticleType.get(raw);
        if(type == null) {
            throw new InvalidConfigException("Cannot find particle type \"" + raw + "\"");
        }
        return type.getClazz();
    }
}
