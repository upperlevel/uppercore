package xyz.upperlevel.uppercore.gui.hotbar.arguments;

import xyz.upperlevel.uppercore.command.arguments.ArgumentParser;
import xyz.upperlevel.uppercore.command.arguments.exceptions.ParseException;
import xyz.upperlevel.uppercore.gui.hotbar.Hotbar;
import xyz.upperlevel.uppercore.gui.hotbar.HotbarSystem;

import java.util.Collections;
import java.util.List;

public class HotbarArgumentParser implements ArgumentParser {

    @Override
    public List<Class<?>> getParsable() {
        return Collections.singletonList(Hotbar.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(Class<?> type, List<String> args) throws ParseException {
        Hotbar h = HotbarSystem.getHotbar(args.get(0));
        if (h == null)
            throw new ParseException(args.get(0), "hotbar");
        return h;
    }
}
