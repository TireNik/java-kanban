import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {

    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    private HashMap<Integer, Epic> epicMap = new HashMap<>();
    private int idCounter = 0;

    public Task addTask(Task task) {
        int id = ++idCounter;
        task.setId(id);
        taskMap.put(id, task);
        return task;
    }

    public Task updateTask(Task updateTask) {
        int id = updateTask.getId();
        if (taskMap.containsKey(id)) {
            taskMap.put(id, updateTask);
            return updateTask;
        }
        return null;
    }

    public Boolean deleteTaskById(Integer id) {
        taskMap.remove(id);
        return true;
    }

    public Task getTaskById(Integer id) {
        return taskMap.get(id);
    }

    public Epic addEpic(Epic epic) {
        int id = ++idCounter;
        epic.setId(id);
        epicMap.put(id, epic);
        return epic;
    }

    public Epic updateEpic(Epic epic) {
        int id = epic.getId();
        if (epicMap.containsKey(id)) {
            updateEpicStatus(epic);
            epicMap.put(id, epic);
            return epic;
        }
        return null;
    }

    public Boolean deleteEpicById(Integer id) {
        List<SubTask> subTasks = getSubtasksForEpic(id);
        for (SubTask subTask : subTasks) {
            subTaskMap.remove(subTask.getId());
        }
        epicMap.remove(id);
        return true;
    }

    public Epic getEpicById(Integer id) {
        return epicMap.get(id);
    }

    public SubTask addSubtask (SubTask subTask) {
        int id = ++idCounter;
        subTask.setId(id);
        subTaskMap.put(id, subTask);
        Epic epic = epicMap.get(subTask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
        return subTask;
    }

    public SubTask updateSubtask (SubTask subTask) {
        int id = subTask.getId();
        if (subTaskMap.containsKey(id)) {
            subTaskMap.put(id, subTask);
            Epic epic = epicMap.get(subTask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
            return subTask;
        }
        return null;
    }

    public Boolean deleteSubtaskForId (Integer id) {
        SubTask subTask = subTaskMap.remove(id);
        if (subTask != null) {
            Epic epic = epicMap.get(subTask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
            return true;
        }
        return false;
    }

    public SubTask getSubtaskById (Integer id) {
        return subTaskMap.get(id);
    }

    public ArrayList<SubTask> getSubtasksForEpic (Integer epicId) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        for (SubTask subTask : subTaskMap.values()) {
            if (subTask.getEpicId().equals(epicId)) {
                subTasks.add(subTask);
            }
        }
        return subTasks;
    }

    private void updateEpicStatus(Epic epic) {
        List<SubTask> subTasks = getSubtasksForEpic(epic.getId());
        boolean isAllNew = true;
        boolean isAllDone = true;

        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() != Progress.NEW) {
                isAllNew = false;
            }
            if (subTask.getStatus() != Progress.DONE) {
                isAllDone = false;
            }
        }

        if (subTasks.isEmpty() || isAllNew) {
            epic.setStatus(Progress.NEW);
        } else if (isAllDone) {
            epic.setStatus(Progress.DONE);
        } else {
            epic.setStatus(Progress.IN_PROGRESS);
        }
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicMap.values());
    }

    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTaskMap.values());
    }

    public void deleteAllTasks() {
        taskMap.clear();
    }

    public void deleteAllEpics() {
        epicMap.clear();
        subTaskMap.clear();
    }
}