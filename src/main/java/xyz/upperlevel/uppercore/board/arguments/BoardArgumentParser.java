package xyz.upperlevel.uppercore.board.arguments;

import org.bukkit.command.CommandSender;
import xyz.upperlevel.uppercore.Identifier;
import xyz.upperlevel.uppercore.board.Board;
import xyz.upperlevel.uppercore.command.argument.ArgumentParser;
import xyz.upperlevel.uppercore.command.argument.ArgumentParserSystem;
import xyz.upperlevel.uppercore.command.argument.exceptions.ParseException;

import java.util.Collections;
import java.util.List;

import static xyz.upperlevel.uppercore.Uppercore.boards;

public class BoardArgumentParser implements ArgumentParser {

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
        Identifier<Board> board = boards().get(args.get(0));
        if (board == null)
            throw new ParseException(args.get(0), "board");
        return board.get();
    }

    @Override
    public List<String> onTabCompletion(CommandSender sender, Class<?> type, List<String> args) {
        return ArgumentParserSystem.tabComplete(boards().getEntries().keySet(), args);
    }
}
