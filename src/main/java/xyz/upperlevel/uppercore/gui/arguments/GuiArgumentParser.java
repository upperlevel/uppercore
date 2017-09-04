package xyz.upperlevel.uppercore.gui.arguments;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.command.argument.ArgumentParser;
import xyz.upperlevel.uppercore.command.argument.ArgumentParserSystem;
import xyz.upperlevel.uppercore.command.argument.exceptions.ParseException;
import xyz.upperlevel.uppercore.gui.GuiId;

import java.util.Collections;
import java.util.List;

import static xyz.upperlevel.uppercore.Uppercore.guis;

public class GuiArgumentParser implements ArgumentParser {

    @Override
    public List<Class<?>> getParsable() {
        return Collections.singletonList(GuiId.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(Class<?> type, List<String> args) throws ParseException {
        GuiId gui = guis().get(args.get(0));
        if (gui == null)
            throw new ParseException(args.get(0), "gui");
        return gui;
    }

    @Override
    public List<String> onTabCompletion(CommandSender sender, Class<?> type, List<String> args) {
        return ArgumentParserSystem.tabComplete(guis().getEntries().keySet(), args);
    }
}
