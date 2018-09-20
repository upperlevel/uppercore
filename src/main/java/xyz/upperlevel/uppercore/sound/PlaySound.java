package xyz.upperlevel.uppercore.sound;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Map;

import static xyz.upperlevel.uppercore.placeholder.PlaceholderValue.fake;

@Getter
@Setter
public class PlaySound {
    public static PlaySound SILENT = new PlaySound(null, null, null) {
        @Override
        public void play(Location loc, Player player) {
        }

        @Override
        public void play(Location loc) {
        }
    };


    private PlaceholderValue<Sound> sound;
    private PlaceholderValue<Float> volume;
    private PlaceholderValue<Float> pitch;

    @ConfigConstructor(inlineable = true)
    public PlaySound(@ConfigProperty("sound")                   PlaceholderValue<Sound> sound,
                     @ConfigProperty(value = "volume", optional = true) PlaceholderValue<Float> volume,
                     @ConfigProperty(value = "pitch", optional = true)  PlaceholderValue<Float> pitch) {
        this.sound = sound;
        this.volume = volume != null ? volume : PlaceholderValue.fake(1.0f);
        this.pitch = pitch != null ? pitch : PlaceholderValue.fake(1.0f);
    }

    public PlaySound(Config config) {
        this.sound = PlaceholderValue.soundValue(config.getStringRequired("sound"));
        this.volume = PlaceholderValue.floatValue(config.getString("volume", "1.0"));
        this.pitch = PlaceholderValue.floatValue(config.getString("pitch", "1.0"));
    }

    public void play(Location loc, Player player) {
        player.playSound(
                loc,
                sound.resolve(player),
                volume.resolve(player),
                pitch.resolve(player)
        );
    }

    public void play(Player player) {
        play(player.getLocation(), player);
    }

    public void play(Location loc) {
        loc.getWorld().playSound(
                loc,
                sound.resolve(null),
                volume.resolve(null),
                pitch.resolve(null)
        );
    }

    public void play(Player player, PlaceholderRegistry<?> reg) {
        player.playSound(
                player.getLocation(),
                sound.resolve(player, reg),
                volume.resolve(player, reg),
                pitch.resolve(player, reg)
        );
    }

    public static PlaySound of(Sound sound, float volume, float pitch) {
        return new PlaySound(
                fake(sound),
                fake(volume),
                fake(pitch)
        );
    }

    public static PlaySound of(Sound sound) {
        return of(sound, 1.0f, 1.0f);
    }

    @SuppressWarnings("unchecked")
    public static PlaySound fromConfig(Object o) {
        if(o == null) {
            return null;
        } if(o instanceof Sound) {
            return of((Sound) o);
        } else if(o instanceof String) {
            return new PlaySound(PlaceholderValue.soundValue((String) o), fake(1.0f), fake(1.0f));
        } else if(o instanceof Map) {
            return new PlaySound(Config.from((Map<String, Object>) o));
        } else if(o instanceof ConfigurationSection) {
            return new PlaySound(Config.from((ConfigurationSection)o));
        } else {
            throw new InvalidConfigException("Expected: Sound or map, found: " + o.getClass().getSimpleName() + " (" + o + ")");
        }
    }
}
