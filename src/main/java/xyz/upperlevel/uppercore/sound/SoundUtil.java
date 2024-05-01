package xyz.upperlevel.uppercore.sound;

import lombok.Getter;
import org.bukkit.Sound;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.bukkit.Sound.valueOf;

public class SoundUtil {
    public static Optional<Sound> get(String str) {
        try {
            return Optional.of(Sound.valueOf(str.replace(' ', '_').toUpperCase(Locale.ENGLISH)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
