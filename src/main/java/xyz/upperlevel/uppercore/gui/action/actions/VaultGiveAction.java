package xyz.upperlevel.uppercore.gui.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.config.CurrentPlugin;
import xyz.upperlevel.uppercore.gui.action.Action;
import xyz.upperlevel.uppercore.gui.action.BaseActionType;
import xyz.upperlevel.uppercore.gui.action.Parser;
import xyz.upperlevel.uppercore.economy.EconomyManager;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.Map;

public class VaultGiveAction extends Action<VaultGiveAction> {
    public static final VaultGiveActionType TYPE = new VaultGiveActionType();

    @Getter
    private final PlaceholderValue<Double> value;

    @ConfigConstructor(inlineable = true)
    public VaultGiveAction(
            @CurrentPlugin Plugin plugin,
            @ConfigProperty("value") PlaceholderValue<Double> value
    ) {
        super(plugin, TYPE);
        this.value = value;
    }

    @Override
    public void run(Player player) {
        final Economy economy = EconomyManager.getEconomy();
        if (economy == null) {
            Uppercore.logger().severe("Cannot find vault's economy!");
            return;
        }
        economy.depositPlayer(player, value.resolve(player));
    }


    public static class VaultGiveActionType extends BaseActionType<VaultGiveAction> {

        public VaultGiveActionType() {
            super(VaultGiveAction.class, "vault-give");
            setParameters(
                    Parameter.of("value", Parser.strValue(), true)//TODO: better Placeholder support
            );
        }

        @Override
        public VaultGiveAction create(Plugin plugin, Map<String, Object> pars) {
            return new VaultGiveAction(
                    plugin,
                    PlaceholderValue.doubleValue((String) pars.get("value"))
            );
        }

        @Override
        public Map<String, Object> read(VaultGiveAction action) {
            return ImmutableMap.of(
                    "value", action.value.toString()
            );
        }
    }
}