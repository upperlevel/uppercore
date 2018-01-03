package xyz.upperlevel.uppercore.util;

import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class CrashUtil {
    public static final String CRASH_DIR = "crash" + File.separator;
    public static final DateFormat CRASH_FORMAT = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss_z");

    public static void saveCrash(Plugin plugin, Throwable t) {
        try(FileWriter writer = createCrashFile(plugin)) {
            if (writer != null)
                t.printStackTrace(new PrintWriter(writer));
            else
                plugin.getLogger().severe("CANNOT SAVE CRASH LOG!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.getLogger().severe(t.getMessage());
    }

    public static FileWriter createCrashFile(Plugin plugin) {
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
        try {
            return new FileWriter(file, false);
        } catch (IOException e) {
            plugin.getLogger().severe("CANNOT OPEN LOG FILE, CHECK PERMISSION IN '" + plugin.getDataFolder() + File.separator + CRASH_DIR + "'");
            return null;
        }
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
