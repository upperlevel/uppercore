package xyz.upperlevel.uppercore.sound;

import lombok.Getter;
import org.bukkit.Sound;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.bukkit.Sound.valueOf;

public class CompatibleSound {
    @Getter
    private static Map<String, Sound> translator = new HashMap<>();

    public static Sound getRaw(String str) {
        return translator.get(str);
    }

    public static Sound get(String str) {

    }

    //SETUP METHODS

    static {
        setupSounds();
    }

    /**
     * Any method call would trigger the static block<br>
     * This method is only an optional call for runtime optimization
     */
    public static void setup(){}

    private static void setupSounds() {
        for(Sound s : Sound.values())
            translator.put(s.name(), s);
    }
}
