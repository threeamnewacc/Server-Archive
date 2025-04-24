package me.devkevin.landcore.utils.web;

import me.devkevin.landcore.callback.WebCallback;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class WebUtil {
    public static void getResponse(JavaPlugin plugin, String url, WebCallback callback) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
                callback.callback(reader.readLine());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public static List<String> getLines(String link) {
        List<String> lines = new ArrayList<>();
        try {
            URL url = new URL(link);
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lines;
    }
}
