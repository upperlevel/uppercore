package xyz.upperlevel.uppercore.command.arguments;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.command.argument.ArgumentParser;
import xyz.upperlevel.uppercore.command.argument.exceptions.ParseException;

import java.util.Collections;
import java.util.List;

public class PlayerArgumentParser implements ArgumentParser {

    @Override
    public List<Class<?>> getParsable() {
        return Collections.singletonList(Player.class);
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Object parse(Class<?> type, List<String> args) throws ParseException {
        Player player = Bukkit.getPlayer(args.get(0));
        if (player == null)
            throw new ParseException(args.get(0), "player");
        return player;
    }
}
