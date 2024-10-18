package testHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Progress;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskHandlerTest {
    private static final String TASKS_URL = "http://localhost:8080/tasks";
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
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task(null, "Задача 1", "Описание задачи 1", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());

        String jsonTask = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TASKS_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task addTask = taskManager.getTaskById(1);
        assertNotNull(addTask);
        assertEquals("Задача 1", addTask.getName());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task(null, "Задача 1", "Описание задачи 1", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TASKS_URL + "?id=1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task returnedTask = gson.fromJson(response.body(), Task.class);
        assertEquals("Задача 1", returnedTask.getName());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task(null, "Задача 1", "Описание задачи 1", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);

        task.setName("Обновленная задача 111");
        String jsonTask = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TASKS_URL + "?id=1"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task updatedTask = taskManager.getTaskById(1);
        assertEquals("Обновленная задача 111", updatedTask.getName());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task(null, "Задача 1", "Описание задачи 1", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TASKS_URL + "?id=1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task = new Task(null, "Задача 1", "Описание задачи 1", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task(null, "Задача 1", "Описание задачи 1", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2028, 10, 27, 18, 18));
        taskManager.addTask(task);
        taskManager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TASKS_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, tasks.length);
    }
}