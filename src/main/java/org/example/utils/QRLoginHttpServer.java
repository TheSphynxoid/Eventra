package org.example.utils;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import org.example.entities.User;
import org.example.services.AuthService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;

public class QRLoginHttpServer {

    public static void start(AuthService authService) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/qr-login", exchange -> {
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    handleLoginRequest(exchange, authService);
                } else {
                    exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                }
            });

            server.setExecutor(null);
            server.start();
            System.out.println("✅ QRLogin HTTP server started on port 8080");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleLoginRequest(HttpExchange exchange, AuthService authService) throws IOException {
        byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
        String body = new String(bodyBytes, StandardCharsets.UTF_8);

        HashMap<String, String> params = parseFormData(body);
        String email = params.get("email");
        String password = params.get("password");

        try {
            Optional<User> userOpt = authService.login(email, password);
            if (userOpt.isPresent()) {
                String response = "Login successful";
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                String response = "Invalid credentials";
                exchange.sendResponseHeaders(401, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        } catch (Exception e) {
            String response = "Internal server error";
            exchange.sendResponseHeaders(500, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            e.printStackTrace();
        }
    }

    private static HashMap<String, String> parseFormData(String formData) {
        HashMap<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                map.put(key, value);
            }
        }
        return map;
    }
}
