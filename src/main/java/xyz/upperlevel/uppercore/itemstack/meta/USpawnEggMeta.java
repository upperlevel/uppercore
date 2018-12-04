package xyz.upperlevel.uppercore.itemstack.meta;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.SpawnEggMeta;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;

public class USpawnEggMeta extends UItemMeta<USpawnEggMeta, SpawnEggMeta> {
    private EntityType eggType;

    @ConfigConstructor
    public USpawnEggMeta(
            @ConfigProperty(value = "display-name", optional = true) String displayName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "egg-type", optional = true) EntityType eggType
    ) {
        super(displayName, lore, flags, enchantments);
        this.eggType = eggType;
    }

    @Override
    public void resolve(SpawnEggMeta meta, Player player) {
        super.resolve(meta, player);
        if (eggType != null) {
            meta.setSpawnedType(eggType);
        }
    }
}
