package xyz.upperlevel.uppercore.update.notifier;

import lombok.Getter;
import org.bukkit.Bukkit;
import xyz.upperlevel.uppercore.Uppercore;

import java.io.*;
import java.net.URLConnection;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class DownloadSession {
    @Getter
    private final InputStream in;
    @Getter
    private final OutputStream out;
    @Getter
    private final long size;
    private AtomicLong amount = new AtomicLong(0);
    private Consumer<Exception> exceptionHandler = Exception::printStackTrace;

    public DownloadSession(InputStream in, long size, OutputStream out) {
        this.in = in;
        this.size = size;
        this.out = out;
    }

    public DownloadSession(URLConnection conn, File outFile) throws IOException {
        this(conn.getInputStream(), conn.getContentLengthLong(), new FileOutputStream(outFile, false));
    }

    public void sync() {
        byte[] buffer = new byte[2048];
        int read;
        try {
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                amount.addAndGet(read);
            }
        } catch (IOException e) {
            exceptionHandler.accept(e);
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {}
            try {
                out.close();
            } catch (IOException ignored) {}
        }
    }

    public void startAsync(Runnable syncCallback) {
        Bukkit.getScheduler().runTaskAsynchronously(
                Uppercore.plugin(),
                () -> {
                    sync();
                    Bukkit.getScheduler().runTask(Uppercore.plugin(), syncCallback);
                }
        );
    }

    public long getAmount() {
        return amount.get();
    }
}
