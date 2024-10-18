import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Progress;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        File file = new File("/Users/sviridovnikita/IdeaProjects/java-kanban/fileBacked/tasks.csv");
        TaskManager taskManager = Managers.getFileBackedTaskManager(file);


        Task task1 = new Task(1, "ЗАДАЧА 1", "ОПИСАНИЕ 1", Progress.NEW,
                Duration.ofMinutes(20),
                LocalDateTime.now());
//        taskManager.addTask(task1);

        System.out.println(taskManager.getAllTasks());

        Task upTask1 = new Task(1, "ЗАДАЧА 1", "ОПИСАНИЕ 1", Progress.NEW,
                Duration.ofMinutes(50),
                LocalDateTime.now());
//        taskManager.updateTask(upTask1);

//        Task task2 = new Task(null,"ЗАДАЧА 2", "ОПИСАНИЕ 2", Progress.NEW,
//                Duration.ofMinutes(90),
//                LocalDateTime.of(2024, 10, 27, 18, 18));
//
//        taskManager.addTask(task1);
//        taskManager.addTask(task2);
//
//        Task task3 = new Task(2,"ЗАДАЧА 2", "ОПИСАНИЕ 2", Progress.NEW,
//                Duration.ofMinutes(90),
//                LocalDateTime.now());
//
////        taskManager.updateTask(task3);
//
        Epic epic = new Epic(1, "ЭПИК 1", "ОПИСАНИЕ ЭПИК");
//
        Epic epic1 = new Epic(2, "ЭПИК 1!!!!", "ОПИСАНИЕ ЭПИК!!!");
        taskManager.updateEpic(epic1);
//
        SubTask subTask1 = new SubTask(3, "ПОДЗАДАЧА 1", "Subtask ОПИСАНИЕ 1", Progress.NEW,
                2,
                Duration.ofMinutes(110),
                LocalDateTime.of(2025, 10, 22, 18, 18));

//        taskManager.addSubtask(subTask1);

        SubTask upSubTask1 = new SubTask(2, "ПОДЗАДАЧА 1", "Subtask ОПИСАНИЕ 1", Progress.NEW,
                1,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 10, 22, 18, 18));

    }
}