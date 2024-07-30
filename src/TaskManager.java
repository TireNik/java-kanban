import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    HashMap<Integer, Task> taskMap = new HashMap<>();
    HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    private static int idCounter = 0;

    public String addTask(String name, String description, String taskType) {
        Task task;
        if (taskType.equals("EPIC")) {
            task = new Epic(name, description, Progress.NEW);
        } else {
            task = new Task(name, description, Progress.NEW);
        }

        int id = ++idCounter;
        while (taskMap.containsKey(id)) {
            id = ++idCounter;
        }

        taskMap.put(id, task);
        return "Задача добавлена успешно! ID задачи: " + id;
    }

    public String addSubTask(int epicId, String name, String description) {
        Task task = taskMap.get(epicId);
        if (task instanceof Epic) {
            Epic epic = (Epic) task;
            int subId = epic.generateSubId();

            SubTask subTask = new SubTask(name, description, Progress.NEW);
            epic.putSubTask(subId, subTask);
            subTaskMap.put(subId, subTask);

            return "Подзадача добавлена успешно! ID подзадачи: " + subId;
        } else {
            return "Задача с ID " + epicId + " не является эпиком.";
        }
    }


    public String printAllTasks() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Integer, Task> entry : taskMap.entrySet()) {
            result.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return result.toString();
    }

    public String printTaskForId(int id) {
        StringBuilder result = new StringBuilder();
        if (taskMap.containsKey(id)) {
            result.append(taskMap.get(id));
        }
        return result.toString();
    }

    public String printSubTaskForId(int subId) {
        if (subTaskMap.containsKey(subId)) {
            return "Подзадача с ID " + subId + ": " + subTaskMap.get(subId);
        } else {
            return "Такого subId не существует.";
        }
    }


    public String updateTask(int id, String name, String description, Progress status) {
        if (taskMap.containsKey(id)) {
            Task task = taskMap.get(id);

            task.setName(name);
            task.setDescription(description);
            task.setStatus(status);

            if (task instanceof Epic) {
                Epic epic = (Epic) task;
                epic.updateStatus();
            }
            return "Задача с ID " + id + " успешно обновлена.";
        } else {
            return "Задача с ID " + id + " не найдена.";
        }
    }

    public String updateSubTask(int epicId, int subId,  String name, String description, Progress status) {
        if (taskMap.containsKey(epicId)) {
            Task task = taskMap.get(epicId);
            if (task instanceof Epic) {
                Epic epic = (Epic) task;
                if (epic.getSubTasks().containsKey(subId)) {
                    SubTask subTask = epic.getSubTasks().get(subId);
                    subTask.setName(name);
                    subTask.setDescription(description);
                    subTask.setStatus(status);

                    epic.updateStatus();
                    return "Подзадача с ID " + subId + " успешно обновлена.";
                } else {
                    return "Подзадача с ID " + subId + " не найдена в эпике с ID " + epicId + ".";
                }
            } else {
                return "Задача с ID " + epicId + " не является эпиком.";
            }
        } else {
            return "Эпик с ID " + epicId + " не найден.";
        }
    }


    public String deleteAllTask() {
        taskMap.clear();
        subTaskMap.clear();
        return "Удалено";
    }

    public String deleteTaskForId (int id) {
        if (taskMap.containsKey(id)) {
            taskMap.remove(id);
            return "Задача с ID " + id + " удалена.";
        } else {
            return "Такого id не существует";
        }
    }

    public String deleteSubTaskForId (int epicId, int subId) {
        if (taskMap.containsKey(epicId)) {
            Task task = taskMap.get(epicId);
            if (task instanceof Epic) {
                Epic epic = (Epic) task;
                if (epic.getSubTasks().containsKey(subId)) {
                    epic.getSubTasks().remove(subId);
                    subTaskMap.remove(subId);
                    return "Подзадача с ID " + subId + " удалена из эпика с ID " + epicId + ".";
                } else {
                    return "Подзадача с ID " + subId + " не найдена в эпике с ID " + epicId + ".";
                }
            } else {
                return "Задача с ID " + epicId + " не является эпиком.";
            }
        } else {
            return "Задача с ID " + epicId + " не найдена.";
        }
    }

}
