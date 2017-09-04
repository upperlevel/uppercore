package xyz.upperlevel.uppercore.particle.data;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.Material;

/**
 * Represents the item data for the {@link ParticleEffect#ITEM_CRACK} effect
 * <p>
 * This class is part of the <b>ParticleEffect Library</b> and follows the same usage conditions
 *
 * @author DarkBlade12
 * @since 1.6
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
