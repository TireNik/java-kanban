public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = getManager();

        taskManager.printAllTasks();

        taskManager.updateTask(1, "Задача 1", "Описание 1 Изменил",
                Progress.IN_PROGRESS);
        taskManager.updateSubTask(3, 1, "Подзадача 1",
                "Изменил подзадачи 1", Progress.IN_PROGRESS);
        taskManager.updateSubTask(3, 2, "Подзадача 2",
                "Изменил подзадачи 2", Progress.DONE);
        taskManager.updateSubTask(4, 1, "Подзадача 2.1",
                "Изменил подзадачи epic 1", Progress.DONE);

        taskManager.printAllTasks();
        taskManager.deleteTaskForId(1);
        taskManager.deleteSubTaskForId(3, 1);
        taskManager.deleteTaskForId(4);
        taskManager.printAllTasks();

    }

    private static TaskManager getManager() {
        TaskManager taskManager = new TaskManager();

        taskManager.addTask("Задача 1", "Описание 1", "TASK");
        taskManager.addTask("Задача 2", "Описание Задачи 2", "TASK");
        taskManager.addTask("Задача epic 3", "Описание эпик 1", "EPIC");
        taskManager.addTask("Задача epic 4", "Описание эпик 2", "EPIC");
        taskManager.addSubTask(3, "Подзадача 1", "Описание подзадачи 1");
        taskManager.addSubTask(3, "Подзадача 2", "Описание подзадачи 2");
        taskManager.addSubTask(4, "Подзадача 2.1",
                "Описание подзадачи 1 epic 2");
        return taskManager;
    }
}