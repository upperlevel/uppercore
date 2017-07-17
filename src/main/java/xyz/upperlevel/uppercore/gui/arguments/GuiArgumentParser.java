package xyz.upperlevel.uppercore.gui.arguments;

import xyz.upperlevel.uppercore.command.arguments.ArgumentParser;
import xyz.upperlevel.uppercore.command.arguments.exceptions.ParseException;
import xyz.upperlevel.uppercore.gui.Gui;
import xyz.upperlevel.uppercore.gui.GuiSystem;

import java.util.Collections;
import java.util.List;

public class GuiArgumentParser implements ArgumentParser {

    @Override
    public List<Class<?>> getParsable() {
        return Collections.singletonList(Gui.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(Class<?> type, List<String> args) throws ParseException {
        Gui gui = GuiSystem.get(args.get(0));
        if (gui == null)
            throw new ParseException(args.get(0), "gui");
        return gui;
    }
}
