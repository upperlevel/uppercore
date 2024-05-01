package xyz.upperlevel.uppercore.util;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public final class CrashUtil {
    public static final String CRASH_DIR = "crash/";
    public static final DateFormat CRASH_FORMAT = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss_z");

    public static void saveCrash(Plugin plugin, Throwable t) {
        String crashHeader = "--- A severe error caused Uppercore to crash ---";
        String barrier = StringUtils.repeat("-", crashHeader.length());
        plugin.getLogger().severe(barrier);
        plugin.getLogger().severe(crashHeader);
        File target = saveCrashToFile(plugin, t);
        if (target != null) {
            plugin.getLogger().severe("Report written in this file: " + target.getName());
        }
        plugin.getLogger().log(Level.SEVERE, "Writing report on console:", t);
        plugin.getLogger().severe(barrier);
    }

    public static File saveCrashToFile(Plugin plugin, Throwable exc) {
        File file = createCrashFile(plugin);
        if (file == null) {
            return null;
        }
        try (FileWriter writer = new FileWriter(file)) {
            exc.printStackTrace(new PrintWriter(writer));
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error while writing crash log", e);
            return null;
        }
        return file;
    }

    public static File createCrashFile(Plugin plugin) {
        String date = CRASH_FORMAT.format(new Date());
        File file = findEmpty(plugin.getDataFolder(), CRASH_DIR + date, ".txt");
        if(file == null) {
            plugin.getLogger().severe("CANNOT FIND EMPTY LOG FILE!");
            return null;
        }
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            plugin.getLogger().severe("CANNOT CREATE LOG FILE, CHECK PERMISSION IN '" + plugin.getDataFolder() + File.separator + CRASH_DIR + "'");
            return null;
        }
        return file;
    }

    private static File findEmpty(File dir, String path, String ext) {
        for(int i = 0; i < 1000; i++) {
            File file = new File(dir,  path + (i == 0 ? "" : i) + ext);
            if(!file.exists())
                return file;
        }
        return null;
    }

    public static void loadSafe(String name, Runnable loader) {
        try {
            loader.run();
        } catch (InvalidConfigException e) {
            e.addLocation("in " + name);
            throw e;
        }
    }

    private CrashUtil(){}
}
