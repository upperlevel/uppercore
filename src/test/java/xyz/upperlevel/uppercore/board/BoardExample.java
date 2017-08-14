package xyz.upperlevel.uppercore.board;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;

import java.util.Arrays;
import java.util.List;

public class BoardExample extends JavaPlugin implements Listener {
    private void onJoin(Player p) {
        Board myBoard = Board.builder("Dynamic board!")
                .append(new BoardSection() {
                    @Override
                    public List<String> render(Player player, PlaceholderRegistry placeholders) {
                        return Arrays.asList(
                                "Player name: " + player.getName(),
                                "Countdown: " + game.getCountdown(),
                                "",
                                "Player money: " + eco.getMoney(player),
                                );
                    }
                })
                .append(FixBoardSection.builder()
                        .add("")
                        .add("Website: www.upperlevel.xyz"))
                .updateInterval(2)
                .build();
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

    }
}
