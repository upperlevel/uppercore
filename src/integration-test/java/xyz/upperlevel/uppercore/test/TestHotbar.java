package xyz.upperlevel.uppercore.test;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.command.functional.AsCommand;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.hotbar.Hotbar;

public class TestHotbar {
    private static Hotbar hotbar;

    @AsCommand
    private void hotbar(Player player) {
        Uppercore.hotbars().view(player).addHotbar(hotbar);
        player.sendMessage(ChatColor.GREEN + "Hotbar added!");
    }

    public static void loadConfig() {
        Config config = Config.from(UppercoreTest.get().getConfig());
        hotbar = config.get("hotbar", Hotbar.class);
    }
}
