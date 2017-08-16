package xyz.upperlevel.uppercore.update.method;

import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;

import java.io.File;
import java.nio.file.Path;

public class UpperResourceZipMethod extends ZipUpdateMethod {
    public static final String CORE_FILENAME = "UpperCore.jar";
    private final Path pluginsDir = Uppercore.get().getDataFolder().getParentFile().toPath();
    private final String pluginName;
    private final boolean hasCore;
    private boolean insideRes = false;

    public UpperResourceZipMethod(String pluginName, boolean hasCore) {
        this.pluginName = pluginName;
        this.hasCore = hasCore;
    }

    @Override
    public void init() {
        File folder = pluginsDir.resolve(pluginName).toFile();
        if(folder.exists())
            deleteDir(folder);
    }

    @Override
    public Path locate(Plugin plugin, Path entry) {
        if (!accept(entry)) {
            return null;
        }
        return pluginsDir.resolve(entry);
    }

    public boolean accept(Path path) {
        String name = path.getFileName().toString();
        return  name.equals(pluginName + ".jar") ||
                (hasCore && name.equals(CORE_FILENAME));
    }


    @Override
    public boolean acceptDir(Path dir) {
        if(dir.getFileName().toString().equals(pluginName) || insideRes) {
            insideRes = true;
            pluginsDir.resolve(dir).toFile().mkdir();
            return true;
        } else return false;
    }

    @Override
    public void finishDir(Path dir) {
        if(insideRes && dir.getFileName().toString().equals(pluginName)) {
            insideRes = false;
        }
    }

    private void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }
}
