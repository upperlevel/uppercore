package xyz.upperlevel.uppercore.update;

import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SpigotUpdateChecker extends UpdateChecker {
    public static final String API_URL = "http://www.spigotmc.org/api/general.php";
    public static final String API_KEY = "98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4";

    private final int resId;

    public SpigotUpdateChecker(Plugin plugin, String spigotFullId) {
        super(plugin, spigotFullId);
        String[] parts = spigotFullId.split("\\.");
        try {
            resId = Integer.parseInt(parts[parts.length - 1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid id: " + spigotFullId);
        }
    }


    @Override
    public String fetchVersion() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.getOutputStream().write(("key=" + API_KEY + "&resource=" + resId).getBytes("UTF-8"));
        return (new BufferedReader(new InputStreamReader(connection.getInputStream()))).readLine();
    }
}
