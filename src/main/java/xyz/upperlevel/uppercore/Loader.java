package xyz.upperlevel.uppercore;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.plugin.Plugin;

import java.io.File;

public interface Loader<T> {
	T load(Plugin plugin, String id, File config);

	static <T> Loader<T> of(ConfigLoader<T> loader) {
		return loader.normalize();
	}
}
