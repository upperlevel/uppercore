package xyz.upperlevel.uppercore.database;

import lombok.Getter;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.util.ExternalJarUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class Storage {
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

    public Storage(String name) {
        this.name = name;
    }

    /**
     * Can this {@link Storage} be used?
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

    /**
     * Connects to the storage.
     * Before connecting checks if the storage is supported.
     *
     * @param address  the address
     * @param port     the port
     * @param database the database
     * @param username the username
     * @param password the password
     * @return a {@link Database} where is possible to write and read.
     */
    public Database connect(String address, int port, String database, String username, String password) {
        if (!isSupported()) {
            throw new StorageNotSupportedException(this);
        }
        return onConnect(address, port, database, username, password);
    }

    protected abstract Database onConnect(String address, int port, String database, String username, String password);
}
