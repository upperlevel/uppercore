package xyz.upperlevel.uppercore.update.method;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

public interface UpdateMethod {
    void update(File updateFile, Plugin plugin) throws IOException;

    static File getPluginFile(Plugin plugin) {//This is more like an util
        try {
            Method getFile = plugin.getClass().getDeclaredMethod("getFile");
            getFile.setAccessible(true);
            return (File) getFile.invoke(plugin);
        } catch (Exception e) {
            return null;
        }
    }
}
