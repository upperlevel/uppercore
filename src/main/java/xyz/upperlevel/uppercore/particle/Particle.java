package xyz.upperlevel.uppercore.particle;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigurationException;

@Data
public abstract class Particle {

    private final ParticleType type;

    protected float offsetX, offsetY, offsetZ;
    protected float speed;
    protected int amount;

    public Particle(ParticleType type) {
        this.type = type;

        setOffset(0f,0f,0f);
        setSpeed(0f);
        setAmount(0);
    }

    public Particle(ParticleType type, Config data) {
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

    public abstract void display(Location location, Iterable<Player> phase);


    public static Particle deserialize(Config data) {
        String raw = data.getStringRequired("type");
        ParticleType type = ParticleType.get(raw);
        if(type == null)
            throw new InvalidConfigurationException("Cannot find particle type \"" + raw + "\"");
        try {
            return type.create(data);
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Cannot load particle \"" + type + "\": ");
        } catch (InvalidConfigurationException e) {
            e.addLocalizer("in particle");
            throw e;
        }
    }
}
