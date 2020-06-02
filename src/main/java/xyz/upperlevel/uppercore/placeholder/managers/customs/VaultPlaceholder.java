package xyz.upperlevel.uppercore.placeholder.managers.customs;

import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.economy.Balance;
import xyz.upperlevel.uppercore.economy.EconomyManager;
import xyz.upperlevel.uppercore.placeholder.Placeholder;

public class VaultPlaceholder implements Placeholder {

    @Override
    public String getId() {
        return "vault";
    }

    @Override
    public String resolve(Player player, String id) {
        Balance economy = EconomyManager.get(player);
        double balance = economy == null ? 0 : economy.get();

        switch (id) {
            case "eco_balance":
                return String.valueOf(balance);
            case "eco_balance_fixed":
                return String.valueOf((long) balance);
        }

        return null;
    }
}
