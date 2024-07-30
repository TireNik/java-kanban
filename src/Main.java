public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();
        System.out.println(taskManager.addTask("Задача 1", "Описание 1", "TASK"));
        System.out.println(taskManager.addTask("Задача 2", "Описание Задачи 2", "TASK"));
        System.out.println(taskManager.addTask("Задача epic 3", "Описание эпик 1", "EPIC"));
        System.out.println(taskManager.addTask("Задача epic 4", "Описание эпик 2", "EPIC"));
        System.out.println(taskManager.addSubTask(3, "Подзадача 1", "Описание подзадачи 1"));
        System.out.println(taskManager.addSubTask(3, "Подзадача 2", "Описание подзадачи 2"));
        System.out.println(taskManager.addSubTask(4, "Подзадача 2.1",
                "Описание подзадачи 1 epic 2"));

        System.out.println(taskManager.printAllTasks());

        System.out.println(taskManager.updateTask(1, "Задача 1", "Описание 1 Изменил",
                Progress.IN_PROGRESS));
        System.out.println(taskManager.updateSubTask(3, 1, "Подзадача 1",
                "Изменил подзадачи 1", Progress.IN_PROGRESS));
        System.out.println(taskManager.updateSubTask(3, 2, "Подзадача 2",
                "Изменил подзадачи 2", Progress.DONE));
        System.out.println(taskManager.updateSubTask(4, 1, "Подзадача 2.1",
                "Изменил подзадачи epic 1", Progress.DONE));

        System.out.println(taskManager.printAllTasks());
        System.out.println(taskManager.deleteTaskForId(1));
        System.out.println(taskManager.deleteSubTaskForId(3, 1));
        System.out.println(taskManager.deleteTaskForId(4));
        System.out.println(taskManager.printAllTasks());

    }
}