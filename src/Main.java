public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();


        Task task1 = new Task(1, "1 Задача", "1 описание", Progress.NEW);
        Task task2 = new Task(2, "2 Задача", "2 описание", Progress.NEW);

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic(3, "1 Эпик", "1 эпик описание");
        Epic epic2 = new Epic(4, "2 Эпик", "2 эпик описание");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        SubTask subTask1_1 = new SubTask(5, "1_1 подзадача", "1_1 описание подзадачи", Progress.DONE, 3);
        SubTask subTask1_2 = new SubTask(6, "1_2 подзадача", "1_2 описание подзадачи", Progress.DONE, 3);

        taskManager.addSubtask(subTask1_1);
        taskManager.addSubtask(subTask1_2);

        SubTask subTask2_1 = new SubTask(7, "2_1 подзадача", "2_1 описание подхадачи", Progress.IN_PROGRESS, 4);

        taskManager.addSubtask(subTask2_1);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTasks());

        System.out.println("___________");

        Task updateTask1 = new Task(1, "1 Задача", "1 описание", Progress.IN_PROGRESS);
        Task updateTask2 = new Task(2, "2 Задача", "2 описание", Progress.IN_PROGRESS);

        taskManager.updateTask(updateTask1);
        taskManager.updateTask(updateTask2);

        Epic updateEpic1 = new Epic(3, "1 Эпик", "1 Изменил описание");
        Epic updateEpic2 = new Epic(4, "2 Эпик", "2 Изменил описание");

        taskManager.updateEpic(updateEpic1);
        taskManager.updateEpic(updateEpic2);

        SubTask updateSubTask1_1 = new SubTask(5, "1_1 подзадача", "1_1 описание подзадачи", Progress.NEW, 3);
        SubTask updateSubTask1_2 = new SubTask(6, "1_2 подзадача", "1_2 описание подзадачи", Progress.NEW, 3);

        taskManager.updateSubtask(updateSubTask1_1);
        taskManager.updateSubtask(updateSubTask1_2);

        SubTask updateSubTask2_1 = new SubTask(7, "2_1 должна удалиться подзадача", "2_1 описание подхадачи", Progress.DONE, 4);

        taskManager.updateSubtask(updateSubTask2_1);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println("________");

        System.out.println(taskManager.getSubtasksForEpic(3));

        System.out.println(taskManager.deleteTaskById(1));
        System.out.println(taskManager.deleteEpicById(4));
        System.out.println(taskManager.deleteSubtaskForId(5));

        SubTask updateSubTask3 = new SubTask(5, "333", "1_1 описание подзадачи", Progress.NEW, 10);
        taskManager.addSubtask(updateSubTask3);


        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTasks());
    }
}
