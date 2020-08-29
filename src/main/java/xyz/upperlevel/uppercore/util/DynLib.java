package xyz.upperlevel.uppercore.util;

import xyz.upperlevel.uppercore.Uppercore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.function.Predicate;

public final class DynLib {
    private DynLib() {
    }

    public static void download(URL url, File file) throws IOException {
        Uppercore.logger().fine(String.format("Downloading: %s", url));
        try (InputStream in = url.openStream()) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void load(File file) throws MalformedURLException {
        Uppercore.logger().fine(String.format("Loading: %s", file.getName()));
        ExternalJarUtil.addUrl(file);
    }

    public static boolean isLoaded(String classpath) {
        try {
            Class.forName(classpath);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static Predicate<File> req(String url) {
        return folder -> {
            try {
                URL parsedUrl = new URL(url);
                File file = new File(folder, new File(parsedUrl.getPath()).getName());
                download(parsedUrl, file);
                load(file);
                return true;
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        };
    }

    public static Predicate<File> check(String classpath) {
        return folder -> isLoaded(classpath);
    }

    public static Predicate<File> reqAndCheck(String url, String classpath) {
        return folder -> {
            req(url).test(folder);
            return check(classpath).test(folder);
        };
    }
}
