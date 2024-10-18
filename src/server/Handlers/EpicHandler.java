package server.Handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import exeptions.NotFoundException;
import manager.TaskManager;
import server.JsonTimeAdapter.DurationAdapter;
import server.JsonTimeAdapter.LocalDateTimeAdapter;
import tasks.Epic;
import tasks.SubTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            switch (method) {
                case "GET" -> {
                    if (Pattern.matches("/epics", path)) {
                        if (query != null && query.startsWith("id=")) {
                            handleGetById(exchange);
                        } else {
                            String response = gson.toJson(taskManager.getAllEpics());
                            sendText(exchange, response, 200);
                        }
                    } else if (Pattern.matches("/epics/\\d+/subtasks", path)) {
                        handleGetEpicSubtasks(exchange);
                    }
                }
                case "POST" -> handelPost(exchange);
//                {
//                    "name": "",
//                        "description": ""
//                }
                case "DELETE" -> handleDelete(exchange);
                default -> sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendText(exchange, "Internal Server Error", 500);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] segments = path.split("/");
            int id = Integer.parseInt(segments[2]);

            List<SubTask> subtasks = taskManager.getSubtasksByEpic(id);

            System.out.println(subtasks);
            String response = gson.toJson(subtasks);
            System.out.println(response);
            sendText(exchange, response, 200);

        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "Internal Server Error", 500);
        }
    }

    private void handleGetById(HttpExchange exchange) throws IOException {
        try {
            String query = exchange.getRequestURI().getRawQuery();
            if (query != null && query.startsWith("id=")) {
                String idStr = query.split("=")[1];
                int id = Integer.parseInt(idStr);

                Epic epic = taskManager.getEpicById(id);

                String response = gson.toJson(epic);
                System.out.println(response);
                sendText(exchange, response, 200);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "Internal Server Error", 500);
        }
    }

    private void handelPost(HttpExchange exchange) throws IOException {

        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        System.out.println("Body " + body);
        try {
            JsonObject jsonObject = gson.fromJson(body, JsonObject.class);
            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();

            Epic epic = new Epic(1, name, description);

            System.out.println("Deserialized " + epic);

            taskManager.addEpic(epic);
            String response = gson.toJson(epic);
            sendText(exchange, response, 201);
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "Server Error", 500);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        try {
            String query = exchange.getRequestURI().getRawQuery();
            if (query != null && query.startsWith("id=")) {
                String idStr = query.split("=")[1];
                int id = Integer.parseInt(idStr);

                if (id != -1) {
                    taskManager.deleteEpicById(id);
                    sendText(exchange, "Задача с id: " + id + " удалена", 200);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "Internal Server Error", 500);
        }
    }
}
