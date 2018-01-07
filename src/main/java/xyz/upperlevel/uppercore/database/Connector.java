package xyz.upperlevel.uppercore.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.Config;
import xyz.upperlevel.uppercore.config.exceptions.InvalidConfigException;
import xyz.upperlevel.uppercore.util.ExternalJarUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static xyz.upperlevel.uppercore.Uppercore.storages;

@RequiredArgsConstructor
public class Connector {
    private static File dir;

    @Getter
    private final Plugin plugin;

    @Getter
    private final Storage storage;
    @Getter
    private final String database;
    @Getter
    private final String host;
    @Getter
    private final Integer port;
    @Getter
    private final String user, password;

    private Connection connection;

    public Connector(Plugin plugin, Config config) {
        this.plugin = plugin;
        String stgStr = config.getStringRequired("type");
        this.storage = storages().get(stgStr);
        if (storage == null)
            throw new InvalidConfigException("Storage type not supported: " + stgStr);
        this.database = config.getString("database", plugin.getName());
        this.host = config.getString("host");
        this.port = config.getInt("port", 0);
        this.user = config.getString("username");
        this.password = config.getString("password", "");
    }

    public boolean setup() {
        if(!hasRequiredFiles()) {
            try {
                Uppercore.logger().info("Downloading drivers for " + storage.getId());
                File[] files = downloadRequiredFiles(dir.toPath());
                for (File file : files)
                    ExternalJarUtil.addUrl(file);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return hasRequiredFiles();
        }
        return true;
    }

    public boolean hasRequiredFiles() {
        return storage.isSupported();
    }

    public String[] getDownloadLinks() {
        return storage.getDownloadLinks();
    }

    public File[] downloadRequiredFiles(Path dir) throws IOException {
        String[] downloadLinks = getDownloadLinks();
        File[] downloaded = new File[downloadLinks.length];
        for (int i = 0; i < downloadLinks.length; i++) {
            URL url = new URL(downloadLinks[i]);
            Path out = dir.resolve(getName(url.getFile()));
            download(url, out);
            downloaded[i] = out.toFile();
        }
        return downloaded;
    }

    private void download(URL url, Path out) throws IOException {
        File file = out.toFile();
        file.getParentFile().mkdirs();
        file.createNewFile();
        try (InputStream in = url.openStream()) {
            Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private String getName(String url) {
        int index = url.lastIndexOf('/');
        if (index >= 0)
            return url.substring(index + 1);
        else return url;
    }

    /**
     * If not cached, connects to the configured database with given data.
     */
    public Connection connection() {
        if (connection == null) {
            if (host != null) {
                if (user != null)
                    connection = storage.connect(database, host, port, user, password);
                else
                    connection = storage.connect(database, host, port);
            } else
                connection = storage.connect(database);
        }
        return connection;
    }

    public static void setupDir() throws IOException {
        dir = new File(Uppercore.get().getDataFolder(), "db_drivers");
        if(dir.isFile())
            throw new IllegalStateException(dir + " isn't a directory!");
        File[] files = dir.listFiles();
        if(files != null) {
            for(File file : files) {
                if(file.isFile() && file.getName().endsWith(".jar"))
                    ExternalJarUtil.addUrl(file.toURI().toURL());
            }
        }
    }


}