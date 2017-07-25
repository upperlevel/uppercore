package xyz.upperlevel.uppercore.board.arguments;

import xyz.upperlevel.uppercore.command.argument.ArgumentParser;
import xyz.upperlevel.uppercore.command.argument.exceptions.ParseException;
import xyz.upperlevel.uppercore.board.Board;
import xyz.upperlevel.uppercore.board.BoardSystem;

import java.util.Collections;
import java.util.List;

public class ScoreboardArgumentParser implements ArgumentParser {

    @Override
    public List<Class<?>> getParsable() {
        return Collections.singletonList(Board.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(Class<?> type, List<String> args) throws ParseException {
        Board sc = BoardSystem.get(args.get(0));
        if (sc == null)
            throw new ParseException(args.get(0), "board");
        return sc;
    }
}
