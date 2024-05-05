package xyz.upperlevel.uppercore.economy;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.util.PluginUtil;

public class EconomyManager {
    @Getter
    private static boolean enabled = false;
    private static Economy economy;

    public static void enable() {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            onVaultLoaded();
        } else {
            Uppercore.logger().info("Disabling economy until vault load");
            enabled = false;

            PluginUtil.onPluginLoaded("Vault", p -> {
                onVaultLoaded();
            });
        }
    }

    private static void onVaultLoaded() {
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

    public static String format(double v) {
        return economy.format(v);
    }
}
