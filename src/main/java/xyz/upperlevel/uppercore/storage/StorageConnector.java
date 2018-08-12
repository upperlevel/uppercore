package xyz.upperlevel.uppercore.storage;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.util.ExternalJarUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class StorageConnector {
    public static final File DRIVERS_FOLDER = new File(Uppercore.get().getDataFolder(), "db_drivers");

    static {
        // On class load, adds the current downloaded jars to the classpath
        try {
            File[] files = DRIVERS_FOLDER.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".jar")) {
                        ExternalJarUtil.addUrl(file.toURI().toURL());
                    }
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot setup initial drivers", exception);
        }
    }

    @Getter
    private final String name;

    public StorageConnector(String name) {
        this.name = name;
    }

    /**
     * Can this {@link StorageConnector} be used?
     */
    public abstract boolean isSupported();

    /**
     * Gets the links where to download the required files.
     */
    public abstract String[] getDownloadLinks();

    private String getFilenameByUrl(String url) {
        int index = url.lastIndexOf('/');
        if (index >= 0)
            return url.substring(index + 1);
        else return url;
    }

    private void download(URL url, Path out) throws IOException {
        File file = out.toFile();
        file.getParentFile().mkdirs();
        file.createNewFile();
        try (InputStream in = url.openStream()) {
            Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private File[] downloadRequiredFiles(Path folder) throws IOException {
        String[] downloadLinks = getDownloadLinks();
        File[] downloaded = new File[downloadLinks.length];
        for (int i = 0; i < downloadLinks.length; i++) {
            URL url = new URL(downloadLinks[i]);
            Path out = folder.resolve(getFilenameByUrl(url.getFile()));
            download(url, out);
            downloaded[i] = out.toFile();
        }
        return downloaded;
    }

    /**
     * Downloads the drivers and adds them to the classpath.
     * Checks if they were already downloaded.
     */
    public boolean download() {
        if (!isSupported()) {
            try {
                File[] files = downloadRequiredFiles(DRIVERS_FOLDER.toPath());
                for (File file : files) {
                    ExternalJarUtil.addUrl(file);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return isSupported();
        }
        return true;
    }

    public Storage setupAndConnect(Config access) {
        if (!download()) {
            throw new IllegalStateException("Storage '" + name + "' not supported");
        }
        return connect(access);
    }

    public abstract Storage connect(Config access);

    public static Storage read(Config config) {
        Config access = config.getConfigRequired("storage");
        String type = access.getStringRequired("type");
        StorageConnector storage = Uppercore.storages().get(type);
        if (storage == null) {
            throw new IllegalArgumentException("No storage found for: " + type);
        } else {
            return storage.setupAndConnect(access);
        }
    }

    public static Storage read(Plugin plugin) {
        plugin.saveResource("storage.yml", false);
        File file = new File(plugin.getDataFolder(), "storage.yml");
        if (!file.exists()) {
            throw new IllegalArgumentException("'storage.yml' file not found for: " + plugin.getName());
        }
        return read(Config.wrap(YamlConfiguration.loadConfiguration(file)));
    }
}
