package com.finwise.lk;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiClient {

    public static String post(String apiUrl, String jsonData) {

        HttpURLConnection connection = null;

        try {
            URL url = new URL(apiUrl);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty(
                    "Content-Type", "application/json; charset=UTF-8"
            );
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            try (OutputStream output = connection.getOutputStream()) {
                output.write(jsonData.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            responseCode >= 200 && responseCode < 400
                                    ? connection.getInputStream()
                                    : connection.getErrorStream()
                    )
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            return response.toString();

        } catch (Exception e) {
            return "{\"status\":\"error\",\"message\":\""
                    + e.getMessage().replace("\"", "'") + "\"}";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String get(String apiUrl) {

        HttpURLConnection connection = null;

        try {
            URL url = new URL(apiUrl);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            return response.toString();

        } catch (Exception e) {
            return "{\"status\":\"error\",\"message\":\""
                    + e.getMessage().replace("\"", "'") + "\"}";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
