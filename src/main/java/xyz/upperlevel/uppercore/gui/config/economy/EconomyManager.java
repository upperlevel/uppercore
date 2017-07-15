package xyz.upperlevel.uppercore.gui.config.economy;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import xyz.upperlevel.uppercore.Uppercore;

public class EconomyManager {
    @Getter
    private static boolean enabled = false;
    private static Economy economy;

    public static void enable() {
        if(Bukkit.getPluginManager().getPlugin("Vault") == null) {
            Uppercore.logger().severe("Cannot load vault, economy not supported!");
            enabled = false;
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Uppercore.logger().severe("Cannot find any economy service, economy not supported");
            enabled = false;
            return;
        }
        enabled = true;
        economy = rsp.getProvider();
    }

    public static Economy getEconomy() {
        return enabled ? economy : null;
    }

    public static Balance get(OfflinePlayer player) {
        return economy == null ? null : new Balance(player);
    }
}
