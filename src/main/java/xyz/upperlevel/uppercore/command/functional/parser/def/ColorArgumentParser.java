package xyz.upperlevel.uppercore.command.functional.parser.def;

import org.bukkit.Color;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParseException;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParser;
import xyz.upperlevel.uppercore.config.ConfigUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ColorArgumentParser implements ArgumentParser {
    @Override
    public Class<?>[] getParsableTypes() {
        return new Class[] {
                Color.class
        };
    }

    @Override
    public int getConsumedCount() {
        return 1;
    }

    @Override
    public Object parse(List<String> args) throws ArgumentParseException {
        try {
            return ConfigUtil.parseColor(args.get(0));
        } catch (NumberFormatException exception) {
            throw new ArgumentParseException(Color.class, Collections.singletonList(args.get(0)));
        }
    }

    @Override
    public List<String> suggest(List<String> args) {
        if (args.isEmpty()) return new ArrayList<>(ConfigUtil.COLOR_BY_NAME.keySet());

        String color = args.get(0).toUpperCase(Locale.ENGLISH);
        return ConfigUtil.COLOR_BY_NAME.keySet()
                .stream()
                .filter(c -> c.startsWith(color))
                .collect(Collectors.toList());
    }
}
