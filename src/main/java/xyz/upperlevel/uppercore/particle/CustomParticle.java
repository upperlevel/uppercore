package xyz.upperlevel.uppercore.particle;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;

import java.util.List;

public abstract class CustomParticle {
    @Getter
    private final ParticleType type;

    @Getter
    @Setter
    protected float offsetX, offsetY, offsetZ;
    @Getter
    @Setter
    protected float speed;
    @Getter
    @Setter
    protected int amount;

    public CustomParticle(ParticleType type) {
        this.type = type;

        setOffset(0f,0f,0f);
        setSpeed(0f);
        setAmount(1);
    }

    public CustomParticle(ParticleType type, Config data) {
        this.type = type;
        setOffset(
                data.getFloat("offset.x", 0f),
                data.getFloat("offset.y", 0f),
                data.getFloat("offset.z", 0f)
        );
        setSpeed(data.getFloat("speed", 0.05f));
        setAmount(data.getInt("amount", 10));
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


    public static CustomParticle deserialize(Config data) {
        String raw = data.getStringRequired("type");
        ParticleType type = ParticleType.get(raw);
        if(type == null)
            throw new InvalidConfigException("Cannot find particle type \"" + raw + "\"");
        try {
            return type.create(data);
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigException("Cannot load particle \"" + type + "\": ");
        } catch (InvalidConfigException e) {
            e.addLocation("in particle");
            throw e;
        }
    }
}
