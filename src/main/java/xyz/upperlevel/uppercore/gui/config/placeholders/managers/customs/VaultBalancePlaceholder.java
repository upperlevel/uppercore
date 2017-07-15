package xyz.upperlevel.uppercore.gui.config.placeholders.managers.customs;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.gui.config.economy.EconomyManager;

public class VaultBalancePlaceholder implements CustomPlaceholder {
    @Override
    public String id() {
        return "vault_eco_balance";
    }

    @Override
    public String get(Player player) {
        final Economy economy = EconomyManager.getEconomy();
        return economy == null ? "0.0" : Double.toString(economy.getBalance(player));
    }
}
