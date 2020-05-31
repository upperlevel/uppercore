package xyz.upperlevel.uppercore.command.functional.parser.def;

import org.bukkit.enchantments.Enchantment;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParseException;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParser;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static xyz.upperlevel.uppercore.util.PluginUtil.parseNamespacedKey;

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
        String ench = args.get(0).toLowerCase(Locale.ENGLISH);

        Enchantment result = Enchantment.getByKey(parseNamespacedKey(ench));

        if (result == null) {
            throw new ArgumentParseException(Enchantment.class, Collections.singletonList(ench));
        }

        return result;
    }

    @Override
    public List<String> suggest(List<String> args) {
        Stream<String> vals = Stream.of(Enchantment.values()).map(e -> e.getKey().toString());

        if (!args.isEmpty()) {
            String e = args.get(0).toUpperCase(Locale.ENGLISH);
            vals = vals.filter(v ->  v.startsWith(e));
        }

        return vals.collect(Collectors.toList());
    }
}
