package xyz.upperlevel.uppercore.itemstack.meta;

import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.BannerMeta;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;

public class UBannerMeta extends UItemMeta<UBannerMeta, BannerMeta> {
    private List<Pattern> patterns;

    public UBannerMeta() {
    }

    @ConfigConstructor
    public UBannerMeta(
            @ConfigProperty(value = "display-name", optional = true) String displayName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "patterns", optional = true) List<Pattern> patterns
    ) {
        super(displayName, lore, flags, enchantments);
        this.patterns = patterns;
    }

    public UBannerMeta setPatterns(List<Pattern> patterns) {
        this.patterns = patterns;
        return this;
    }

    @Override
    public void resolve(BannerMeta meta, Player player) {
        super.resolve(meta, player);
        if (patterns != null) {
            meta.setPatterns(patterns);
        }
    }
}
