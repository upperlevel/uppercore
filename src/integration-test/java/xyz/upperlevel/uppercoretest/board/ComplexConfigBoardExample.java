package xyz.upperlevel.uppercoretest.board;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.board.Board;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ComplexConfigBoardExample implements Board {
    private final Config config;

    public ComplexConfigBoardExample(Config config) {
        this.config = config;
    }

    @Override
    public String getTitle(Player player, PlaceholderRegistry placeholderRegistry) {
        return config.getMessageStr("title").resolve(player, placeholderRegistry);
    }

    @Override
    public List<String> getLines(Player player, PlaceholderRegistry placeholderRegistry) {
        // based on the number of lines to generate gives a different output
        int numOfLines = new Random().nextInt(5) + 1;
        return config.getMessageStrList(String.valueOf(numOfLines)).stream().map(line -> line.resolve(player, placeholderRegistry)).collect(Collectors.toList());
    }

    @Override
    public int getAutoUpdateInterval() {
        return 10; // each half tick update speed
    }
}
