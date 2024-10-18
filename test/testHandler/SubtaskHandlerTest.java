package testHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Epic;
import tasks.Progress;
import tasks.SubTask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskHandlerTest {
    private static final String SUBTASKS_URL = "http://localhost:8080/subtasks";
    private HttpTaskServer server;
    private TaskManager taskManager;
    private HttpClient client;
    private Gson gson;

    @BeforeEach
    public void startServer() throws IOException {
        taskManager = new InMemoryTaskManager();
        server = new HttpTaskServer(taskManager);
        client = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new server.JsonTimeAdapter.DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new server.JsonTimeAdapter.LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        server.start();
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask(null, "Подзадача 1", "Описание подзадачи 1", Progress.NEW,
                1, Duration.ofMinutes(30), LocalDateTime.of(2024, 10, 22, 18, 18));
        String jsonSubTask = gson.toJson(subTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SUBTASKS_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubTask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        SubTask addedSubTask = taskManager.getSubtaskById(2);
        assertNotNull(addedSubTask);
        assertEquals("Подзадача 1", addedSubTask.getName());
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask(1, "Подзадача 1", "Описание подзадачи 1", Progress.NEW,
                1, Duration.ofMinutes(30), LocalDateTime.of(2024, 10, 22, 18, 18));
        taskManager.addSubtask(subTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SUBTASKS_URL + "?id=2"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        SubTask returnedSubTask = gson.fromJson(response.body(), SubTask.class);
        assertEquals("Подзадача 1", returnedSubTask.getName());
    }

    @Test
    public void testGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);
        taskManager.addSubtask(new SubTask(1, "Подзадача 1", "Описание подзадачи 1", Progress.NEW,
                1, Duration.ofMinutes(30), LocalDateTime.of(2024, 10, 22, 18, 18)));
        taskManager.addSubtask(new SubTask(2, "Подзадача 2", "Описание подзадачи 2", Progress.NEW,
                1, Duration.ofMinutes(30), LocalDateTime.of(2025, 10, 23, 18, 18)));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SUBTASKS_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        SubTask[] subtasks = gson.fromJson(response.body(), SubTask[].class);
        assertEquals(2, subtasks.length);
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask(1, "Подзадача 1", "Описание подзадачи 1", Progress.NEW,
                1, Duration.ofMinutes(30), LocalDateTime.of(2024, 10, 22, 18, 18));
        taskManager.addSubtask(subTask);

        subTask.setName("Обновленная подзадача");
        String jsonSubTask = gson.toJson(subTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SUBTASKS_URL + "?id=2"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubTask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        SubTask updatedSubTask = taskManager.getSubtaskById(2);
        assertEquals("Обновленная подзадача", updatedSubTask.getName());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask(1, "Подзадача 1", "Описание подзадачи 1", Progress.NEW,
                1, Duration.ofMinutes(30), LocalDateTime.of(2024, 10, 22, 18, 18));
        taskManager.addSubtask(subTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SUBTASKS_URL + "?id=2"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertNull(taskManager.getSubtaskById(1));
    }

}
