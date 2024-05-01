package xyz.upperlevel.uppercore.update;

import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SpigotUpdateChecker extends UpdateChecker {
    public static final String API_URL = "https://api.spigotmc.org/legacy/update.php?resource=";

    private final long resId;

    public SpigotUpdateChecker(Plugin plugin, String spigotFullId) {
        super(plugin, spigotFullId);
        String[] parts = spigotFullId.split("\\.");
        try {
            resId = Long.parseLong(parts[parts.length - 1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid id: " + spigotFullId);
        }
    }
    
    @Override
    public String fetchVersion() throws IOException {
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URI(API_URL + resId).toURL().openConnection();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error creating URL", e);
        }
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");
        return (new BufferedReader(new InputStreamReader(connection.getInputStream()))).readLine();
    }
}
