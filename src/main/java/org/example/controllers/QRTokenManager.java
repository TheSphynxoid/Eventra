package org.example.controllers;

import org.example.entities.User;
import org.example.services.AuthService;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Handles QR login token generation, verification and login resolution.
 */
public class QRTokenManager {

    private static final String SERVER_URL = "http://192.168.1.27:8000"; // change to your IP or config

    /**
     * Generates a QR login token and corresponding verification URL for a given email.
     *
     * @param email Email to attach to the token
     * @return QRToken object with token & URL
     * @throws Exception if network or parsing fails
     */
    public static QRToken generateToken(String email) throws Exception {
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
        URL url = new URL(SERVER_URL + "/generate-token.php?email=" + encodedEmail);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Erreur lors de la génération du token (code " + conn.getResponseCode() + ")");
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line);

            JSONObject obj = new JSONObject(response.toString());
            return new QRToken(obj.getString("token"), obj.getString("url"));
        }
    }

    /**
     * Verifies whether a QR token has been validated by the user (via phone).
     *
     * @param token The token to verify
     * @return true if verified
     * @throws Exception if request fails
     */
    public static boolean isTokenVerified(String token) throws Exception {
        URL url = new URL(SERVER_URL + "/check-token.php?token=" + token);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        return conn.getResponseCode() == 200;
    }

    /**
     * Looks up the user by email in the Java-side database.
     *
     * @param email Email to lookup
     * @param authService AuthService to query
     * @return Optional<User> if found
     * @throws Exception if DB error occurs
     */
    public static Optional<User> loginWithEmail(String email, AuthService authService) throws Exception {
        return authService.findByEmail(email);
    }

    /**
     * Validates email and password credentials against the local AuthService through HTTP.
     * This simulates what PHP would call to verify credentials.
     *
     * @param email Email to verify
     * @param password Password to verify
     * @return true if credentials are valid
     */
    public static boolean validateCredentialsLocally(String email, String password) {
        try {
            String data = "email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                    + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);
            byte[] postData = data.getBytes(StandardCharsets.UTF_8);

            URL url = new URL("http://localhost:8080/qr-login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData);
            }

            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Token structure holding token ID and associated verification URL.
     */
    public record QRToken(String token, String url) {}
}
