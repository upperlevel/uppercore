package xyz.upperlevel.uppercore.command.functional.parser.def;

import org.bukkit.util.Vector;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParseException;
import xyz.upperlevel.uppercore.command.functional.parser.ArgumentParser;

import java.util.Collections;
import java.util.List;

public final class VectorArgumentParser implements ArgumentParser {

    @Override
    public Class<?>[] getParsableTypes() {
        return new Class[] {
                Vector.class
        };
    }

    @Override
    public int getConsumedCount() {
        return 3;
    }

    private static double tryParseDouble(String d) throws ArgumentParseException {
        try {
            return Double.parseDouble(d);
        } catch (NumberFormatException exception) {
            throw new ArgumentParseException(double.class, Collections.singletonList(d));
        }
    }

    @Override
    public Object parse(List<String> args) throws ArgumentParseException {
        return new Vector(
                tryParseDouble(args.get(0)),
                tryParseDouble(args.get(1)),
                tryParseDouble(args.get(2))
        );
    }

    @Override
    public List<String> suggest(List<String> arguments) {
        return Collections.emptyList();
    }
}
