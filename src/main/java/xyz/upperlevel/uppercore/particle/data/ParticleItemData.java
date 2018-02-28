package xyz.upperlevel.uppercore.particle.data;

import org.bukkit.Material;

/**
 * Represents the item data for the {@link xyz.upperlevel.uppercore.particle.ParticleEffect#ITEM_CRACK} effect
 */
public class ParticleItemData extends ParticleData {
    /**
     * Construct a new item data
     *
     * @param material Material of the item
     * @param data Data value of the item
     * @see ParticleData#ParticleData(Material, byte)
     */
    public ParticleItemData(Material material, byte data) {
        super(material, data);
    }
}
