package net.badlion.common.libraries;

import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.UUID;

public class HTTPCommon {

    public static JSONObject executeGETRequest(String urlString) throws HTTPRequestFailException {
        return HTTPCommon.executeGETRequest(urlString, 5000);
    }

    /**
     * Query CouchDB to try and get a specific document based on our design and our view
     *
     * @return the return result from the GET
     */
    public static JSONObject executeGETRequest(String urlString, int timeout) throws HTTPRequestFailException {
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setReadTimeout(timeout);
            connection.setConnectTimeout(timeout);

            // Not sure this line is needed
            int responseCode = connection.getResponseCode();

            if (responseCode < 200 || responseCode >= 300) {
                // Response
                InputStream is = connection.getErrorStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                throw new HTTPRequestFailException(responseCode, response.toString());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            return (JSONObject) JSONValue.parse(response.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if (e instanceof SocketTimeoutException) {
                throw new HTTPRequestFailException(HTTPRequestFailException.REQUEST_FAIL_TYPE.TIMEOUT);
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    public static JSONArray executeGETArrayRequest(String urlString) throws HTTPRequestFailException {
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);

            // Not sure this line is needed
            int responseCode = connection.getResponseCode();

            if (responseCode < 200 || responseCode >= 300) {
                // Response
                InputStream is = connection.getErrorStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                throw new HTTPRequestFailException(responseCode, response.toString());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            return (JSONArray) JSONValue.parse(response.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if (e instanceof SocketTimeoutException) {
                throw new HTTPRequestFailException(HTTPRequestFailException.REQUEST_FAIL_TYPE.TIMEOUT);
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    /**
     * Query CouchDB to try and get a specific document based on our design and our view
     *
     * @return the return result from the GET
     */
    public static JSONObject executeDELETERequest(String urlString) throws HTTPRequestFailException {
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);

            // Not sure this line is needed
            int responseCode = connection.getResponseCode();

            if (responseCode < 200 || responseCode >= 300) {
                // Response
                InputStream is = connection.getErrorStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                throw new HTTPRequestFailException(responseCode, response.toString());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            return (JSONObject) JSONValue.parse(response.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if (e instanceof SocketTimeoutException) {
                throw new HTTPRequestFailException(HTTPRequestFailException.REQUEST_FAIL_TYPE.TIMEOUT);
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    public static JSONObject executePOSTRequest(String urlString, JSONObject json) throws HTTPRequestFailException {
        return HTTPCommon.executePOSTRequest(urlString, json, 5000);
    }

    /**
     * Call this when we want to INSERT a new document into the database
     *
     * @param json json data we want to store
     * @return JSON response
     */
    public static JSONObject executePOSTRequest(String urlString, JSONObject json, int timeout) throws HTTPRequestFailException {
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setReadTimeout(timeout);
            connection.setConnectTimeout(timeout);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(json.toJSONString());
            wr.flush();
            wr.close();

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                // Response
                InputStream is = connection.getErrorStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                throw new HTTPRequestFailException(responseCode, response.toString());
            }

            // Response
            InputStream is = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            return (JSONObject) JSONValue.parse(response.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if (e instanceof SocketTimeoutException) {
                throw new HTTPRequestFailException(HTTPRequestFailException.REQUEST_FAIL_TYPE.TIMEOUT);
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    /**
     * Call this when we want to INSERT a new document into the database
     *
     * @param json json data we want to store
     * @return JSON response
     */
    public static JSONArray executePOSTRequestJSONArray(String urlString, JSONObject json) throws HTTPRequestFailException {
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(json.toJSONString());
            wr.flush();
            wr.close();

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                // Response
                InputStream is = connection.getErrorStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                throw new HTTPRequestFailException(responseCode, response.toString());
            }

            // Response
            InputStream is = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            return (JSONArray) JSONValue.parse(response.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if (e instanceof SocketTimeoutException) {
                throw new HTTPRequestFailException(HTTPRequestFailException.REQUEST_FAIL_TYPE.TIMEOUT);
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    /**
     * Call this when we want to INSERT a new document into the database
     *
     * @param json json data we want to store
     * @return JSON response
     */
    public static JSONObject executePUTRequest(String urlString, JSONObject json) throws HTTPRequestFailException {
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(json.toJSONString());
            wr.flush();
            wr.close();

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                // Response
                InputStream is = connection.getErrorStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                throw new HTTPRequestFailException(responseCode, response.toString());
            }

            // Response
            InputStream is = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            return (JSONObject) JSONValue.parse(response.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if (e instanceof SocketTimeoutException) {
                throw new HTTPRequestFailException(HTTPRequestFailException.REQUEST_FAIL_TYPE.TIMEOUT);
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    /**
     * Query a web page to get a txt file back
     *
     * @return the return result from the GET
     */
    public static String executeTextGETRequest(String urlString) throws HTTPRequestFailException {
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "plain/text");
            connection.setUseCaches(false);
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);

            // Not sure this line is needed
            int responseCode = connection.getResponseCode();

            if (responseCode < 200 || responseCode >= 300) {
                // Response
                InputStream is = connection.getErrorStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                throw new HTTPRequestFailException(responseCode, response.toString());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            return response.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if (e instanceof SocketTimeoutException) {
                throw new HTTPRequestFailException(HTTPRequestFailException.REQUEST_FAIL_TYPE.TIMEOUT);
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    /**
     * NEEDS TO BE CALLED ASYNC
     */
    public static String getMostRecentUsernameFromMojang(UUID uuid) {
        try {
            JSONArray jsonArray = HTTPCommon.executeGETArrayRequest("https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names");
            long counter = 0;
            String username = null;
            if (jsonArray != null) {
                System.out.println(jsonArray.toJSONString());
                for (Object object : jsonArray) {
                    JSONObject jsonObject = (JSONObject) object;
                    if (jsonObject.containsKey("changedToAt")) {
                        long changedToAt = (long) jsonObject.get("changedToAt");
                        if (changedToAt > counter) {
                            counter = changedToAt;
                            username = (String) jsonObject.get("name");
                        } else if (counter == 0) {
                            username = (String) jsonObject.get("name");
                        }
                    }
                }

                // Iterate one more time trying to find a username (fix Huahweii edge case)
                if (username == null) {
                    for (Object object : jsonArray) {
                        JSONObject jsonObject = (JSONObject) object;
                        if (jsonObject.containsKey("name")) {
                            username = (String) jsonObject.get("name");
                        }
                    }
                }
            }

            // Valid username
            if (username != null) {
                return username;
            }
        } catch (HTTPRequestFailException e) {
            throw new RuntimeException("Could not fetch UUID " + e.getResponseCode() + " " + e.getResponse());
        }

        throw new RuntimeException("Could not fetch UUID x2");
    }

}
