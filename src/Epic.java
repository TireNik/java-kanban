import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private int subIdCounter = 0;

    public Epic(String name, String description, Progress status) {
        super(name, description, status);
    }

    public void updateStatus() {
        boolean isAllNew = true;
        boolean isAllDone = true;

        for (SubTask subTask : subTasks.values()) {
            if (subTask.getStatus() != Progress.NEW) {
                isAllNew = false;
            }
            if (subTask.getStatus() !=Progress.DONE) {
                isAllDone = false;
            }
        }

        if (isAllNew) {
            setStatus(Progress.NEW);
        } else if (isAllDone) {
            setStatus(Progress.DONE);
        } else {
            setStatus(Progress.IN_PROGRESS);
        }

    }

    public int generateSubId() {
        int subId = ++subIdCounter;
        while (subTasks.containsKey(subId)) {
            subId = ++subIdCounter;
        }
        return subId;
    }

    public void putSubTask(Integer subId, SubTask subTask) {
        subTasks.put(subId, subTask);
        updateStatus();
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public String toString() {
        StringBuilder subTasksString = new StringBuilder();
        for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
            subTasksString.append("\n    Подзадача ID ").append(entry.getKey()).append(": ").append(entry.getValue());
        }
        return "Epic{" +
                "имя: '" + getName() + '\'' +
                ", описание: '" + getDescription() + '\'' +
                ", статус: " + getStatus() +
                ", подзадачи: [" + subTasksString.toString() + "\n]" +
                '}';
    }
}
