package xyz.upperlevel.uppercore.itemstack.meta;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;

public class USkullMeta extends UItemMeta<USkullMeta, SkullMeta> {
    private PlaceholderValue<String> skullOwner;

    public USkullMeta() {
    }

    @ConfigConstructor
    public USkullMeta(
            @ConfigProperty(value = "display-name", optional = true) String displayName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "skull-owner", optional = true) PlaceholderValue<String> skullOwner
    ) {
        super(displayName, lore, flags, enchantments);
        this.skullOwner = skullOwner;
    }

    public void setSkullOwner(String skullOwner) {
        this.skullOwner = PlaceholderValue.fake(skullOwner);
    }

    @Override
    public void resolve(SkullMeta meta, Player player) {
        super.resolve(meta, player);
        if (skullOwner != null) {
            meta.setOwner(skullOwner.resolve(player));
        }
    }
}
