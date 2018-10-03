package xyz.upperlevel.uppercore.command.functional.parser.def;

import org.bukkit.Material;
import org.bukkit.Sound;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParseException;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MaterialArgumentParser implements ArgumentParser {
    @Override
    public Class<?>[] getParsableTypes() {
        return new Class[] {
                Material.class
        };
    }

    @Override
    public int getConsumedCount() {
        return 1;
    }

    @Override
    public Object parse(List<String> args) throws ArgumentParseException {
        Material mat = null;
        try {
            mat = Material.getMaterial(Integer.parseInt(args.get(0)));
        } catch (NumberFormatException ignored) {}

        if (mat == null) {
            mat = Material.getMaterial(args.get(0).toUpperCase());
        }

        if (mat != null) return mat;

        throw new ArgumentParseException(Material.class, Collections.singletonList(args.get(0)));
    }

    @Override
    public List<String> suggest(List<String> args) {
        Stream<String> vals = Arrays.stream(Material.values()).map(Material::name);

        if (!args.isEmpty()) {
            String mat = args.get(0).toUpperCase(Locale.ENGLISH);
            vals = vals.filter(s -> s.startsWith(mat));
        }

        return vals.collect(Collectors.toList());
    }
}
