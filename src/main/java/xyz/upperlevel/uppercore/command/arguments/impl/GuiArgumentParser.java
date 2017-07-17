package xyz.upperlevel.uppercore.command.arguments.impl;

import xyz.upperlevel.uppercore.command.arguments.ArgumentParser;
import xyz.upperlevel.uppercore.command.arguments.exceptions.ParseException;
import xyz.upperlevel.uppercore.gui.Gui;
import xyz.upperlevel.uppercore.gui.GuiManager;

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
        Gui gui = GuiManager.getGui(args.get(0));
        if (gui == null)
            throw new ParseException(args.get(0), "gui");
        return gui;
    }
}
