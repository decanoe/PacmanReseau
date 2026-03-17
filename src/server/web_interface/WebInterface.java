package server.web_interface;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public final class WebInterface {
    private static final String DEFAULT_WEB_ADRESS = "http://localhost:";
    private static final int DEFAULT_WEB_PORT = 8080;
    private static final String WEB_LOGIN_PAGE_PATH = "/PacmanReseau/LoginPage";
    private static final String WEB_COSMETIC_PAGE_PATH = "/PacmanReseau/CosmeticPage";

    protected static String getWebBaseURL() { return DEFAULT_WEB_ADRESS + DEFAULT_WEB_PORT; }
    protected static String getWebLoginURL() { return getWebBaseURL() + WEB_LOGIN_PAGE_PATH; }
    protected static String getWebCosmeticURL() { return getWebBaseURL() + WEB_COSMETIC_PAGE_PATH; }
    
    @SuppressWarnings("deprecation")
    protected static HttpURLConnection openConnection(String url) {
        try {
            HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);

            // Headers
            http.setRequestProperty("Content-Type", "application/json");

            return http;
        } catch (Exception e) {
            return null;
        }
    }
    protected static JSONObject sendRequest(HttpURLConnection http, String data) {
        try (OutputStream os = http.getOutputStream()) {
            byte[] input = data.getBytes("UTF-8");
            os.write(input, 0, input.length);
        } catch (Exception e) {
            return null;
        }

        try {
            // Lecture de la réponse
            int status = http.getResponseCode();
            if (status >= 400) return null;
            InputStream is = http.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String answer = br.readLine();
            br.close();

            return new JSONObject(answer);
        } catch (Exception e) {
            return null;
        }
    }

    protected static boolean checkStatus(JSONObject json) {
        return json.has("status") && json.getString("status").equals("ok");
    }

    public static String getSalt(String login) {
        HttpURLConnection http = openConnection(getWebLoginURL());
        if (http == null) return null;

        // Corps de la requête
        String data = "{ \"action\": \"getSaltUser\", \"user\": \"" + login + "\", \"session\": false }";

        JSONObject json = sendRequest(http, data);
        if (json == null) return null;

        if (checkStatus(json)) {
            if (json.has("salt")) return json.getString("salt");
        }
        return null;
    }
    public static boolean validatePassword(String login, String password_hash) {
        HttpURLConnection http = openConnection(getWebLoginURL());
        if (http == null) return false;

        // Corps de la requête
        String data = "{ \"action\": \"login\", \"user\": \"" + login + "\", \"pwd\": \""+password_hash+"\", \"session\": false }";

        JSONObject json = sendRequest(http, data);
        if (json == null) return false;

        return checkStatus(json);
    }
    public static JSONObject getActiveCosmetics(String login) {
        HttpURLConnection http = openConnection(getWebCosmeticURL());
        if (http == null) return new JSONObject();

        // Corps de la requête
        String data = "{ \"action\": \"getActiveCosm\", \"user\": \"" + login + "\", \"session\": false }";

        JSONObject json = sendRequest(http, data);
        if (json == null) return new JSONObject();

        if (checkStatus(json)) return json;
        return new JSONObject();
    }
}
