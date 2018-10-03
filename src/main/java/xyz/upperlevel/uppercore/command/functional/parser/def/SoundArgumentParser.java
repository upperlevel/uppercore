package xyz.upperlevel.uppercore.command.functional.parser.def;

import org.bukkit.Sound;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParseException;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParser;
import xyz.upperlevel.uppercore.sound.CompatibleSound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SoundArgumentParser implements ArgumentParser {
    @Override
    public Class<?>[] getParsableTypes() {
        return new Class[] {
                Sound.class
        };
    }

    @Override
    public int getConsumedCount() {
        return 1;
    }

    @Override
    public Object parse(List<String> args) throws ArgumentParseException {
        Sound s = CompatibleSound.get(args.get(0));
        if (s != null) return s;
        throw new ArgumentParseException(Sound.class, Collections.singletonList(args.get(0)));
    }

    @Override
    public List<String> suggest(List<String> args) {
        if (args.isEmpty()) return new ArrayList<>(CompatibleSound.getTranslator().keySet());

        String color = args.get(0).toUpperCase(Locale.ENGLISH);
        return CompatibleSound.getTranslator().keySet()
                .stream()
                .filter(c -> c.startsWith(color))
                .collect(Collectors.toList());
    }
}
