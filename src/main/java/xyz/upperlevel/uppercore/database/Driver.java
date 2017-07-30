package xyz.upperlevel.uppercore.database;

import lombok.Data;
import xyz.upperlevel.uppercore.Uppercore;
import xyz.upperlevel.uppercore.config.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;

import static java.io.File.separator;
import static xyz.upperlevel.uppercore.Uppercore.get;
import static xyz.upperlevel.uppercore.Uppercore.logger;

@Data
public abstract class Driver {
    private final String id;
    private String url;

    public void load(Config config) {
        url = config.getString("url");
    }

    private static void download(String url, File file) throws IOException {
        URL link = new URL(url);
        ReadableByteChannel rbc = Channels.newChannel(link.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    public File getLib() {
        return new File(get().getDataFolder() + separator + "db" + separator + id);
    }

    public void download() {
        logger().info("Attempting to download source for \"" + id + "\"...");
        if (getLib().exists()) {
            if (url != null) {
                try {
                    download(url, getLib());
                } catch (IOException e) {
                    throw new IllegalStateException("Cannot download " + id + " source: " + e);
                }
                logger().info("Downloaded source for \"" + id + "\"");
            } else
                logger().warning("No source found for \"" + id + "\"");
        } else
            logger().info("Source for \"" + id + "\" already exists");
    }

    public Accessor access() {
        if (!getLib().exists())
            download();
        return  accessor();
    }

    protected abstract Accessor accessor();
}
