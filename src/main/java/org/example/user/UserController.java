package org.example.user;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class UserController {

    private final UserService userService;
    private final Gson gson = new Gson();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();

        switch (method) {
            case "POST":
                handleCreate(exchange);
                break;
            case "GET":
                handleGet(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
                break;
            default:
                sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void handleCreate(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        is.close();

        try {
            User user = gson.fromJson(body, User.class);
            userService.register(user);
            sendResponse(exchange, 200, gson.toJson(user));
        } catch (RuntimeException e) {
            sendResponse(exchange, 400, "Error: " + e.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null || !query.contains("=")) {
            sendResponse(exchange, 400, "Missing email parameter");
            return;
        }
        String email = query.split("=")[1];

        User user = userService.getByEmail(email);
        if (user != null) {
            sendResponse(exchange, 200, gson.toJson(user));
        } else {
            sendResponse(exchange, 404, "User not found");
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null || !query.contains("=")) {
            sendResponse(exchange, 400, "Missing email parameter");
            return;
        }
        String email = query.split("=")[1];

        try {
            userService.delete(email);
            sendResponse(exchange, 200, "User deleted");
        } catch (RuntimeException e) {
            sendResponse(exchange, 400, "Error: " + e.getMessage());
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}