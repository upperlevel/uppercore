package xyz.upperlevel.uppercore.util;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.Material;
import org.bukkit.block.Block;

import static org.bukkit.block.BlockFace.*;

public final class BedUtil {

	public static boolean isBedBlock(Block block) {
		return block != null && (block.getType() == Material.BED_BLOCK);
	}

	public static Block getNeighbor(Block head) {
		if (isBedBlock(head.getRelative(EAST)))
			return head.getRelative(EAST);
		else if (isBedBlock(head.getRelative(WEST)))
			return head.getRelative(WEST);
		else if (isBedBlock(head.getRelative(SOUTH)))
			return head.getRelative(SOUTH);
		else
			return head.getRelative(NORTH);
	}

	private BedUtil() {
	}
}
