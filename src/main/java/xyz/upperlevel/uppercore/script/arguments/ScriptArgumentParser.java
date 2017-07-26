package xyz.upperlevel.uppercore.script.arguments;

import xyz.upperlevel.uppercore.command.argument.ArgumentParser;
import xyz.upperlevel.uppercore.command.argument.exceptions.ParseException;
import xyz.upperlevel.uppercore.script.Script;
import xyz.upperlevel.uppercore.script.ScriptId;

import java.util.Collections;
import java.util.List;

import static xyz.upperlevel.uppercore.Uppercore.scripts;

public class ScriptArgumentParser implements ArgumentParser {

    @Override
    public List<Class<?>> getParsable() {
        return Collections.singletonList(Script.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(Class<?> type, List<String> args) throws ParseException {
        ScriptId s = scripts().get(args.get(0));
        if (s == null)
            throw new ParseException(args.get(0), "script");
        return s.get();
    }
}
