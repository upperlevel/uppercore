package xyz.upperlevel.uppercore.sound.argument;

import org.bukkit.Sound;
import xyz.upperlevel.uppercore.command.argument.ArgumentParser;
import xyz.upperlevel.uppercore.command.argument.exceptions.ParseException;
import xyz.upperlevel.uppercore.sound.CompatibleSound;

import java.util.List;

import static java.util.Collections.singletonList;

public class SoundArgumentParser implements ArgumentParser {
    @Override
    public List<Class<?>> getParsable() {
        return singletonList(Sound.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(Class<?> type, List<String> args) throws ParseException {
        Sound sound = CompatibleSound.get(args.get(0));
        if (sound != null)
            return sound;
        else throw new ParseException(args.get(0), "Sound");
    }
}