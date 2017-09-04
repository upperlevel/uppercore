package xyz.upperlevel.uppercore.update.method;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ReplaceUpdateMethod implements UpdateMethod {
	@Override
	public void update(File updateFile, Plugin plugin) throws IOException {
		Path src = updateFile.toPath();
		Path trg = UpdateMethod.getPluginFile(plugin).toPath();
		try {
			Files.move(src, trg, ATOMIC_MOVE);
		} catch (Exception e) {
			// Fallback
			Files.move(src, trg, REPLACE_EXISTING);
		}
	}
}
