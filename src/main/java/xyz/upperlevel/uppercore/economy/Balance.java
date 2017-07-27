package xyz.upperlevel.uppercore.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static xyz.upperlevel.uppercore.economy.EconomyManager.format;

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

    public String getFormatted() {
        return economy.format(get());
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
