package xyz.upperlevel.uppercore.util;

import org.bukkit.Bukkit;
import xyz.upperlevel.uppercore.Uppercore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DynLib {
    private final URL url;
    private final File file;

    private final String id; // Just for debug purposes.

    public DynLib(URL url) {
        this.url = url;
        this.file = getFile();
        this.id = file.getName();
        Dbg.pf("The file for %s is: %s", id, file);
    }

    private File getFile() {
        File file = new File(Bukkit.getWorldContainer(), "dyn_libs" + File.separator + new File(url.getPath()).getName());
        file.getParentFile().mkdir(); // In order to create dyn_libs folder.
        return file;
    }

    public void download() throws IOException {
        Logger logger = Uppercore.logger();
        if (file.exists()) {
            logger.info(String.format("Lib already downloaded: %s", id));
            return;
        }
        logger.info(String.format("Downloading: %s", id));
        if (file.createNewFile()) {
            try (InputStream in = url.openStream()) {
                Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    public void load() throws MalformedURLException {
        Logger logger = Uppercore.logger();
        if (!file.exists()) {
            logger.info(String.format("Lib not found at: %s", file));
            return;
        }
        logger.info(String.format("Loading: %s", id));
        ExternalJarUtil.addUrl(file);
    }

    public static class Pool {
        private final List<DynLib> libs;

        public Pool(List<DynLib> libs) {
            this.libs = libs;
        }

        public void install() throws IOException {
            // First downloads all libraries and then loads them.
            for (DynLib lib : libs) lib.download();
            for (DynLib lib : libs) lib.load();
        }
    }

    public static Pool parsePool(List<String> libs) {
        return new Pool(libs.stream().map(url -> {
            try {
                return new DynLib(new URL(url));
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e);
            }
        }).collect(Collectors.toList()));
    }

    public static void checkAssert(String classpath) {
        try {
            Class.forName(classpath);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
