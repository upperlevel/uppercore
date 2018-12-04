package xyz.upperlevel.uppercore.itemstack.meta;

import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;

public class UPotionMeta extends UItemMeta<UPotionMeta, PotionMeta> {
    private PotionData potionData;
    private PlaceholderValue<Color> customColor;
    private List<PotionEffect> customEffects;

    @ConfigConstructor
    public UPotionMeta(
            @ConfigProperty(value = "display-name", optional = true) String displayName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "potion-data", optional = true) PotionData potionData,
            @ConfigProperty(value = "custom-color") PlaceholderValue<Color> customColor,
            @ConfigProperty(value = "custom-effects") List<PotionEffect> customEffects
    ) {
        super(displayName, lore, flags, enchantments);
        this.potionData = potionData;
        this.customColor = customColor;
        this.customEffects = customEffects;
    }

    public UPotionMeta setPotionData(PotionData potionData) {
        this.potionData = potionData;
        return this;
    }

    public UPotionMeta setCustomColor(Color customColor) {
        this.customColor = PlaceholderValue.fake(customColor);
        return this;
    }

    public UPotionMeta setCustomEffects(List<PotionEffect> customEffects) {
        this.customEffects = customEffects;
        return this;
    }

    @Override
    public void resolve(PotionMeta meta, Player player) {
        super.resolve(meta, player);
        if (potionData != null) {
            meta.setBasePotionData(potionData);
        }
        if (customColor != null) {
            meta.setColor(customColor.resolve(player));
        }
        if (customEffects != null) {
            customEffects.forEach(effect -> meta.addCustomEffect(effect, true));
        }
    }
}
