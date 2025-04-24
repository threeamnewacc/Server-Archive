// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.util.finalutil;

import java.io.IOException;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class HttpUtil
{
    public static String getHastebin(final String body) {
        try {
            final URL url = new URL("https://www.hastebin.com/documents");
            final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            final DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(body);
            os.flush();
            os.close();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final JSONParser parser = new JSONParser();
            final JSONObject object = (JSONObject)parser.parse(reader.readLine());
            return object.get((Object)"key").toString();
        }
        catch (final ParseException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
