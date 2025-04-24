package me.devkevin.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 4:40
 */
@Getter @RequiredArgsConstructor
public abstract class AbstractRequestProcessor implements RequestProcessor {
    private final String apiUrl;
    private final String apiKey;

    @Override
    public JsonElement sendRequest(Request request) {
        return this.sendRequest(request, null);
    }

    @Override
    public void sendRequestAsync(Request request) {
        this.sendRequest(request, null, true);
    }

    @Override
    public void sendRequestAsync(Request request, Callback callback) {
        this.sendRequest(request, callback,true);
    }

    @Override
    public void sendRequest(Request request, Callback callback, boolean async) {
        if (async) {
            this.runTaskAsynchronously(() -> this.sendRequest(request, callback));
        } else {
            this.sendRequest(request, callback);
        }
    }

    @Override
    public JsonElement sendRequest(Request request, Callback callback) {
        if (!this.shouldSend()) {

            System.out.println("!!!!!!!!!!!! " +
                    "Attempt on main thread " +
                    "!!!!!!!!!!!!");

            throw new IllegalStateException("Attempted to send an API request on the main thread.");
        }

        Map<String, Object> data = request.toMap();
        if (data == null) {
            data = new HashMap<>();
        }

        List<NameValuePair> parameters = new ArrayList<>(2);

        for (String key : data.keySet()) {
            Object value = data.get(key);
            parameters.add(new BasicNameValuePair(key, value == null ? null : value.toString()));
        }

        CloseableHttpClient client = HttpClients.createDefault();
        try {
            String url = ("http://" + this.apiUrl + "/api/" + this.apiKey + request.getPath());
            HttpPost post = new HttpPost(
                    url.replace(" ", "%20"));
            post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));

            CloseableHttpResponse response = null;
            try {
                try {
                    response = client.execute(post);
                } catch (Exception e) {
                    e.printStackTrace();

                    if (callback instanceof ErrorCallback) {
                        this.runTask(()
                                -> ((ErrorCallback) callback).onError("Error connecting to " + post.getURI().getHost() +
                                " : " + e.getMessage()));
                    }
                    System.out.println("Error connecting to: " + url);
                    System.out.println("The issue: " + e.getMessage());
                    return null;
                }

                StatusLine line = response.getStatusLine();
                if (line != null) {
                    int code = line.getStatusCode();
                    if (code != 200) {
                        if (callback instanceof ErrorCallback) {
                            this.runTask(() -> ((ErrorCallback) callback).onError("Error code: " + code));
                        }

                        System.out.println("Error code: " + code);
                        System.out.println("From request: " + url);
                        return null;
                    }
                }

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
                        JsonParser parser = new JsonParser();
                        JsonElement object = parser.parse(reader);

                        if (callback != null) {
                            this.runTask(() -> callback.callback(object));
                        }

                        return object;
                    } catch (ParseException e) {
                        e.printStackTrace();

                        if (callback instanceof ErrorCallback) {
                            this.runTask(()
                                    -> ((ErrorCallback) callback).onError("Error parsing Json: " + e.getMessage()));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            if (callback instanceof ErrorCallback) {
                this.runTask(()
                        -> ((ErrorCallback) callback).onError("Unknown error: " + e.getMessage()));
            }
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
