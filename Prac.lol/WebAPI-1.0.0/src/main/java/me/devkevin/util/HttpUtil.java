package me.devkevin.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Logger;

public final class HttpUtil {

    /**
     * Fetch UUID from name via Mojang's API. <p></p>
     *
     * @param name - Player's name to fetch
     *
     * @return - UUID assigned to the name provided
     */
    public static UUID getUniqueIdFromName(String name) {
        BufferedReader in = null;

        try {
            String url = "https://api.mojang.com/profiles/minecraft";
            URL obj = new URL(url);

            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection(Proxy.NO_PROXY);

            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Content-Type", "application/json");

            String urlParameters = "[\"" + name + "\"]";

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            if (responseCode != 200) {
                return null;
            }

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            JsonArray array = new JsonParser().parse(in).getAsJsonArray();
            JsonObject object = array.get(0).getAsJsonObject();

            String uuid = object.get("uuid").getAsString().trim();

            String[] parts = {
                    uuid.substring(0, 8),
                    uuid.substring(8, 12),
                    uuid.substring(12, 16),
                    uuid.substring(16, 20),
                    uuid.substring(20, 32)
            };

            return UUID.fromString(String.join("-", parts));
        } catch (Exception e) {
            e.printStackTrace();

            Logger.getGlobal().warning("!!!!!!!!!!!!!!!!!!!!!!! POTENTIAL RATE LIMITINGS ON CHECKING FOR " + name);

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Logger.getGlobal().severe("Failed to fetch UUID for " + name);

        return null;
    }
}
