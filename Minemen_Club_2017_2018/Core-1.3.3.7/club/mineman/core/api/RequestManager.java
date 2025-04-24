// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.api;

import java.beans.ConstructorProperties;
import org.bukkit.plugin.Plugin;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.HttpResponse;
import java.util.Iterator;
import org.apache.http.client.HttpClient;
import java.io.IOException;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.HttpGet;
import java.util.List;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import java.util.ArrayList;
import org.apache.http.impl.client.HttpClients;
import java.util.Map;
import java.util.HashMap;
import org.json.simple.JSONObject;
import club.mineman.core.api.request.RequestCallback;
import club.mineman.core.CorePlugin;

public class RequestManager
{
    private final CorePlugin plugin;
    
    public void sendRequest(final APIMessage message, final RequestCallback callback) {
        this.sendRequest(message, callback, true);
    }
    
    public JSONObject sendRequestNow(final APIMessage message) {
        if (this.plugin.getServer().isPrimaryThread()) {
            System.out.println("OH HELL NO NIGGER");
            System.out.println("OH HELL NO NIGGER");
            System.out.println("OH HELL NO NIGGER");
            System.out.println("OH HELL NO NIGGER");
            System.out.println("OH HELL NO NIGGER");
            System.out.println("OH HELL NO NIGGER");
            System.out.println("OH HELL NO NIGGER");
            System.out.println("OH HELL NO NIGGER");
            try {
                throw new Exception("NIGGERS");
            }
            catch (final Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        final Map<String, Object> encoded = new HashMap<String, Object>();
        encoded.put("message", message.getChannel());
        encoded.put("api-key", "#N38(#UHe0d3js0#E&3uK");
        encoded.putAll(message.toMap());
        final HttpClient httpclient = HttpClients.createDefault();
        final List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        for (final String key : encoded.keySet()) {
            final Object value = encoded.get(key);
            params.add(new BasicNameValuePair(key, (value == null) ? null : value.toString()));
        }
        try {
            final HttpGet httpGet = new HttpGet("http://142.44.162.32:1741/api?" + URLEncodedUtils.format(params, "utf-8"));
            HttpResponse response;
            try {
                response = httpclient.execute(httpGet);
            }
            catch (final Exception e2) {
                e2.printStackTrace();
                System.out.println("lol nigger | " + URLEncodedUtils.format(params, "utf-8") + " | " + e2.getMessage());
                return null;
            }
            final StatusLine statusLine = response.getStatusLine();
            if (statusLine != null) {
                final int code = statusLine.getStatusCode();
                if (code != 200) {
                    return null;
                }
            }
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
                    final JSONParser parser = new JSONParser();
                    return (JSONObject)parser.parse((Reader)reader);
                }
                catch (final ParseException e3) {
                    e3.printStackTrace();
                }
            }
        }
        catch (final IOException e4) {
            e4.printStackTrace();
        }
        return null;
    }
    
    public void sendRequest(final APIMessage message, final RequestCallback callback, final boolean async) {
        final Map<String, Object> encoded = new HashMap<String, Object>();
        encoded.put("message", message.getChannel());
        encoded.put("api-key", "#N38(#UHe0d3js0#E&3uK");
        encoded.putAll(message.toMap());
        if (async) {
            this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> this.handleRequest(callback, encoded));
        }
        else {
            this.handleRequest(callback, encoded);
        }
    }
    
    private void handleRequest(final RequestCallback callback, final Map<String, Object> encoded) {
        final HttpClient httpclient = HttpClients.createDefault();
        final List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        for (final String key : encoded.keySet()) {
            final Object value = encoded.get(key);
            params.add(new BasicNameValuePair(key, (value == null) ? null : value.toString()));
        }
        try {
            final HttpGet httpGet = new HttpGet("http://142.44.162.32:1741/api?" + URLEncodedUtils.format(params, "utf-8"));
            HttpResponse response;
            try {
                response = httpclient.execute(httpGet);
            }
            catch (final Exception e) {
                e.printStackTrace();
                System.out.println("lol nigger | " + URLEncodedUtils.format(params, "utf-8") + " | " + e.getMessage());
                this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> callback.error("Error connecting to " + httpGet.getURI().getHost() + " : " + e.getMessage()));
                return;
            }
            final StatusLine statusLine = response.getStatusLine();
            if (statusLine != null) {
                final int code = statusLine.getStatusCode();
                if (code != 200) {
                    this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> callback.error("Request error code " + code));
                    return;
                }
            }
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
                    final JSONParser parser = new JSONParser();
                    final JSONObject jsonObject = (JSONObject)parser.parse((Reader)reader);
                    this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> callback.callback(jsonObject));
                }
                catch (final ParseException e2) {
                    e2.printStackTrace();
                    final Exception e;
                    this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> callback.error("ParseException: " + e.getMessage()));
                }
            }
        }
        catch (final IOException e3) {
            e3.printStackTrace();
            final Exception e;
            this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> callback.error("IOException: " + e.getMessage()));
        }
    }
    
    @ConstructorProperties({ "plugin" })
    public RequestManager(final CorePlugin plugin) {
        this.plugin = plugin;
    }
}
