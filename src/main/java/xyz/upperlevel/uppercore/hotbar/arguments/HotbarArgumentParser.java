package xyz.upperlevel.uppercore.hotbar.arguments;

import xyz.upperlevel.uppercore.command.argument.ArgumentParser;
import xyz.upperlevel.uppercore.command.argument.exceptions.ParseException;
import xyz.upperlevel.uppercore.hotbar.HotbarId;

import java.util.Collections;
import java.util.List;

import static xyz.upperlevel.uppercore.Uppercore.hotbars;

public class HotbarArgumentParser implements ArgumentParser {

    @Override
    public List<Class<?>> getParsable() {
        return Collections.singletonList(HotbarId.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(Class<?> type, List<String> args) throws ParseException {
        HotbarId h = hotbars().get(args.get(0));
        if (h == null)
            throw new ParseException(args.get(0), "hotbar");
        return h;
    }
}
