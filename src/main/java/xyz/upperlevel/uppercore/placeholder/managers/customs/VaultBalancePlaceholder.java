package xyz.upperlevel.uppercore.placeholder.managers.customs;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.economy.Balance;
import xyz.upperlevel.uppercore.economy.EconomyManager;
import xyz.upperlevel.uppercore.placeholder.Placeholder;

public class VaultBalancePlaceholder implements Placeholder {

    @Override
    public String getId() {
        return "vault_eco_balance";
    }

    @Override
    public String resolve(Player player, String id) {
        Balance economy = EconomyManager.get(player);
        return economy == null ? "0" : economy.getFormatted();
    }
}
