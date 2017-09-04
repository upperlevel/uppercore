package xyz.upperlevel.uppercore.economy;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import xyz.upperlevel.uppercore.Uppercore;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class EconomyManager {
	@Getter
	private static boolean enabled = false;
	private static Economy economy;

	public static void enable() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
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

	public static String format(double v) {
		return economy.format(v);
	}
}
