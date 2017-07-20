package xyz.upperlevel.uppercore.placeholder.managers.customs;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.gui.config.economy.EconomyManager;
import xyz.upperlevel.uppercore.placeholder.Placeholder;

public class VaultBalancePlaceholder implements Placeholder {

    @Override
    public String getId() {
        return "vault_eco_balance";
    }

    @Override
    public String resolve(Player player, String id) {
        Economy economy = EconomyManager.getEconomy();
        return economy == null ? "0.0" : Double.toString(economy.getBalance(player));
    }
}
