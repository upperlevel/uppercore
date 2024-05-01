package xyz.upperlevel.uppercore.update;

import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import java.util.Map;

import static xyz.upperlevel.uppercore.util.JsonUtil.GENERAL_GSON;
import static xyz.upperlevel.uppercore.util.JsonUtil.JSON_MAP_TYPE;

public class SpigetUpdateChecker extends DownloadableUpdateChecker {
    private static final String USER_AGENT  = "Mozilla/5.0 (X11; Linux x86_64; rv:54.0) Gecko/20100101 Firefox/54.0";
    public static final String SITE_PREFIX = "https://api.spiget.org/v2/resources/";
    public static final String DOWNLOAD_POSTFIX = "/download";
    public static final String VERSIONS_POSTFIX = "/versions?size=1&sort=-releaseDate";

    @Getter
    private final long spigetId;

    public SpigetUpdateChecker(Plugin plugin, String spigotFullId, long spigetId) {
        super(plugin, spigotFullId);
        this.spigetId = spigetId;
    }

    @Override
    public URLConnection getDownload() throws IOException {
        return fileQuery(DOWNLOAD_POSTFIX);
    }

    @Override
    public String fetchVersion() throws IOException {
        return getLatestVersion();
    }

    @SuppressWarnings("unchecked")
    public String getLatestVersion() throws IOException {
        List<Map<String, Object>> res = (List<Map<String, Object>>) jsonQuery(VERSIONS_POSTFIX);
        return res == null ? null : (String) res.get(0).get("name");
    }

    private URL parseUrl(String str) throws IOException {
        try {
            return new URI(str).toURL();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to create URI", e);
        }
    }

    public Object jsonQuery(String postfix) throws IOException {
        URL url = parseUrl(SITE_PREFIX + spigetId + postfix);
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("User-Agent", USER_AGENT);
        if(((HttpURLConnection)conn).getResponseCode() == 404)
            return null;
        return  GENERAL_GSON.fromJson(new InputStreamReader((InputStream)conn.getContent()), JSON_MAP_TYPE);
    }

    public URLConnection fileQuery(String postfix) throws IOException {
        URL url = parseUrl(SITE_PREFIX + spigetId + postfix);
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("User-Agent", USER_AGENT);
        if(((HttpURLConnection)conn).getResponseCode() == 404)
            return null;
        return conn;
    }
}
