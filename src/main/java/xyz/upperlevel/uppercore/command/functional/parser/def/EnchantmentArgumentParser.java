package xyz.upperlevel.uppercore.command.functional.parser.def;

import org.bukkit.enchantments.Enchantment;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParseException;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParser;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnchantmentArgumentParser implements ArgumentParser {
    @Override
    public Class<?>[] getParsableTypes() {
        return new Class[] {
                Enchantment.class
        };
    }

    @Override
    public int getConsumedCount() {
        return 1;
    }

    @Override
    public Object parse(List<String> args) throws ArgumentParseException {
        String ench = args.get(0);

        Enchantment res = null;

        try {
            res = Enchantment.getByKey(Integer.parseInt(ench));
        } catch (NumberFormatException ignored) {}

        if (res == null) {
            res = Enchantment.getByName(ench.toUpperCase(Locale.ENGLISH));
        }

        if (res == null) {
            throw new ArgumentParseException(Enchantment.class, Collections.singletonList(ench));
        }

        return res;
    }

    @Override
    public List<String> suggest(List<String> args) {
        Stream<String> vals = Stream.of(Enchantment.values()).map(Enchantment::getName);

        if (!args.isEmpty()) {
            String e = args.get(0).toUpperCase(Locale.ENGLISH);
            vals = vals.filter(v ->  v.startsWith(e));
        }

        return vals.collect(Collectors.toList());
    }
}
