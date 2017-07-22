package xyz.upperlevel.uppercore.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

public class Balance {
    private final Economy economy;
    private final OfflinePlayer player;

    public Balance(Economy economy, OfflinePlayer player) {
        this.economy = economy;
        this.player = player;
    }

    public Balance(OfflinePlayer player) {
        this(EconomyManager.getEconomy(), player);
        if(economy == null)
            throw new IllegalStateException("Cannot find economy, vault not found (?)");
    }

    public double get() {
        return economy.getBalance(player);
    }

    public void give(double money) {
        economy.depositPlayer(player, money);
    }

    public boolean take(double money) {
        return economy.withdrawPlayer(player, money).transactionSuccess();
    }

    public boolean has(double money) {
        return economy.has(player, money);
    }
}
