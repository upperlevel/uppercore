package xyz.upperlevel.uppercore.itemstack.meta;

import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.MapMeta;
import xyz.upperlevel.uppercore.config.ConfigConstructor;
import xyz.upperlevel.uppercore.config.ConfigProperty;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;

import java.util.List;
import java.util.Map;

public class UMapMeta extends UItemMeta<UMapMeta, MapMeta> {
    private boolean scaling;
    private PlaceholderValue<String> locationName;
    private Color color;

    public UMapMeta() {
    }

    @ConfigConstructor
    public UMapMeta(
            @ConfigProperty(value = "display-name", optional = true) String displayName,
            @ConfigProperty(value = "lore", optional = true) List<PlaceholderValue<String>> lore,
            @ConfigProperty(value = "flags", optional = true) List<ItemFlag> flags,
            @ConfigProperty(value = "enchantments", optional = true) Map<Enchantment, PlaceholderValue<Integer>> enchantments,
            @ConfigProperty(value = "map-scaling", optional = true) Boolean scaling,
            @ConfigProperty(value = "map-location-name") PlaceholderValue<String> mapLocationName,
            @ConfigProperty(value = "map-color") Color mapColor
    ) {
        super(displayName, lore, flags, enchantments);
        this.scaling = scaling != null ? scaling : false;
        this.locationName = mapLocationName;
        this.color = mapColor;
    }

    public UMapMeta setScaling(boolean scaling) {
        this.scaling = scaling;
        return this;
    }

    public UMapMeta setLocationName(String locationName) {
        this.locationName = PlaceholderValue.fake(locationName);
        return this;
    }

    public UMapMeta setColor(Color color) {
        this.color = color;
        return this;
    }

    @Override
    public void resolve(MapMeta meta, Player player) {
        super.resolve(meta, player);
        if (locationName != null) {
            meta.setLocationName(locationName.resolve(player));
        }
        if (color != null) {
            meta.setColor(color);
        }
        meta.setScaling(scaling);
    }
}
