package manager;

import tasks.Epic;
import tasks.Progress;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private Map<Integer, Task> taskMap = new HashMap<>();
    private Map<Integer, SubTask> subTaskMap = new HashMap<>();
    private Map<Integer, Epic> epicMap = new HashMap<>();
    private int idCounter = 0;

    private TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())));

    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    protected void updateIdCounter(int id) {
        idCounter = Math.max(idCounter, id);
    }

    protected void addTaskDirectly(Task task) {
        taskMap.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    protected void addEpicDirectly(Epic epic) {
        epicMap.put(epic.getId(), epic);
        if (epic.getStartTime() != null) {
            prioritizedTasks.add(epic);
        }
    }

    protected void addSubTaskDirectly(SubTask subTask) {
        subTaskMap.put(subTask.getId(), subTask);
        Epic epic = getEpicMap().get(subTask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subTask.getId());
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
        if (subTask.getStartTime() != null) {
            prioritizedTasks.add(subTask);
        }
    }

    protected Map<Integer, Task> getTaskMap() {
        return taskMap;
    }

    protected Map<Integer, SubTask> getSubTaskMap() {
        return subTaskMap;
    }

    protected Map<Integer, Epic> getEpicMap() {
        return epicMap;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task addTask(Task task) {
        if (isTaskOverlapping(task)) {
            throw new IllegalArgumentException("Задача пересекается с существующей задачей.");
        }
        int id = ++idCounter;
        task.setId(id);
        taskMap.put(id, task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Task updateTask(Task updateTask) {
        Task oldTask = taskMap.get(updateTask.getId());

        if (oldTask != null) {
            prioritizedTasks.remove(oldTask);

            if (isTaskOverlapping(updateTask)) {
                prioritizedTasks.add(oldTask);
                throw new IllegalArgumentException("Обнавленная адача пересекается с существующей задачей.");
            }

            taskMap.put(updateTask.getId(), updateTask);
            prioritizedTasks.add(updateTask);
            return updateTask;
        }
        return null;
    }

    @Override
    public Boolean deleteTaskById(Integer id) {
        if (taskMap.containsKey(id)) {
            Task removeTask = taskMap.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(removeTask);
            return removeTask != null;
        }
        return false;
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = taskMap.get(id);
        if (task != null) {
            historyManager.add(task);
            return task;
        }
        return null;
    }

    @Override
    public Epic addEpic(Epic epic) {
        int id = ++idCounter;
        epic.setId(id);
        epicMap.put(id, epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        int id = epic.getId();
        if (epicMap.containsKey(id)) {
            Epic epic1 = epicMap.get(id);
            epic1.setName(epic.getName());
            epic1.setDescription(epic.getDescription());
            return epic;
        }
        return null;
    }

    @Override
    public Boolean deleteEpicById(Integer id) {
        Epic epic = epicMap.get(id);
        if (epic != null) {
            epic.getSubtaskIds()
                    .forEach(subTaskId -> {
                        subTaskMap.remove(subTaskId);
                        historyManager.remove(subTaskId);
                    });

            prioritizedTasks.removeIf(task -> epic.getSubtaskIds().contains(task.getId()));
            epicMap.remove(id);
            historyManager.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = epicMap.get(id);
        if (epic != null) {
            historyManager.add(epic);
            return epic;
        }
        return null;
    }

    @Override
    public SubTask addSubtask(SubTask subTask) {
        if (isTaskOverlapping(subTask)) {
            throw new IllegalArgumentException("Подзадача пересекается с существующими задачами.");
        }
        Epic epic = epicMap.get(subTask.getEpicId());
        if (epic != null) {
            int id = ++idCounter;
            subTask.setId(id);
            subTaskMap.put(id, subTask);
            epic.addSubtaskId(id);
            updateEpicStatus(epic);
            updateEpicTime(epic);
            prioritizedTasks.add(subTask);

            return subTask;
        }
        return null;
    }

    @Override
    public SubTask updateSubtask(SubTask subTask) {
        SubTask oldSub = subTaskMap.get(subTask.getId());

        if (oldSub == null) {
            return null;
        }
        prioritizedTasks.remove(oldSub);

        if (isTaskOverlapping(subTask)) {
            prioritizedTasks.add(oldSub);
            throw new IllegalArgumentException("Обнавленная подзадача пересекается с существующими задачами.");
        }

        int id = subTask.getId();
        if (subTaskMap.containsKey(id)) {
            SubTask currentSubtaskId = subTaskMap.get(id);
            if (currentSubtaskId.getEpicId().equals(subTask.getEpicId())) {
                subTaskMap.put(id, subTask);
                prioritizedTasks.add(subTask);
                Epic epic = epicMap.get(subTask.getEpicId());
                if (epic != null) {
                    updateEpicStatus(epic);
                    updateEpicTime(epic);
                }
                return subTask;
            }
        }

        return null;
    }

    @Override
    public Boolean deleteSubtaskForId(Integer id) {
        SubTask subTask = subTaskMap.remove(id);
        if (subTask != null) {
            Epic epic = epicMap.get(subTask.getEpicId());
            if (epic != null) {
                historyManager.remove(id);
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
                updateEpicTime(epic);
                prioritizedTasks.remove(subTask);
            }
            return true;
        }
        return false;
    }

    @Override
    public SubTask getSubtaskById(Integer id) {
        SubTask subTask = subTaskMap.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
            return subTask;
        }
        return null;
    }

    @Override
    public ArrayList<SubTask> getSubtasksByEpic(Integer epicId) {
        Epic epic = epicMap.get(epicId);
        if (epic != null) {
            return epic.getSubtaskIds().stream()
                    .map(subTaskMap::get)
                    .filter(Objects::nonNull)
                    .peek(historyManager::add)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return new ArrayList<>();
    }


    @Override
    public void updateEpicTime(Epic epic) {
        List<SubTask> subTasks = getSubtasksByEpic(epic.getId()).stream()
                .distinct()
                .toList();

        if (subTasks.isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        Duration totalDuration = subTasks.stream()
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        LocalDateTime earliestStart = subTasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime latestEnd = subTasks.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        System.out.println("Обновление времени эпика: " + epic.getName());
        System.out.println("Общая длительность: " + totalDuration);
        System.out.println("Самое раннее начало: " + earliestStart);
        System.out.println("Самое позднее завершение: " + latestEnd);
        epic.setDuration(totalDuration);
        epic.setStartTime(earliestStart);
        epic.setEndTime(latestEnd);
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        List<SubTask> subTasks = getSubtasksByEpic(epic.getId());

        if (subTasks.isEmpty()) {
            epic.setStatus(Progress.NEW);
            return;
        }
        boolean allNew = subTasks.stream().allMatch(subTask -> subTask.getStatus() == Progress.NEW);
        boolean allDone = subTasks.stream().allMatch(subTask -> subTask.getStatus() == Progress.DONE);

        if (allNew) {
            epic.setStatus(Progress.NEW);
        } else if (allDone) {
            epic.setStatus(Progress.DONE);
        } else {
            epic.setStatus(Progress.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTaskMap.values());
    }

    @Override
    public void deleteAllTasks() {
        List<Integer> taskIds = new ArrayList<>(taskMap.keySet());
        prioritizedTasks.removeIf(task -> taskMap.containsKey(task.getId()));
        taskMap.clear();
        taskIds.forEach(historyManager::remove);
    }

    @Override
    public void deleteAllEpics() {
        List<Integer> epicIds = new ArrayList<>(epicMap.keySet());
        List<Integer> subtaskIds = new ArrayList<>(subTaskMap.keySet());

        prioritizedTasks.removeIf(subtask -> subTaskMap.containsKey(subtask.getId()));
        epicMap.clear();
        subTaskMap.clear();

        epicIds.forEach(historyManager::remove);
        subtaskIds.forEach(historyManager::remove);
    }

    @Override
    public void deleteAllSubTasks() {
        List<Integer> subTaskIds = new ArrayList<>(subTaskMap.keySet());

        subTaskIds.forEach(historyManager::remove);
        prioritizedTasks.removeIf(subTask -> subTaskMap.containsKey(subTask.getId()));
        subTaskMap.clear();

        epicMap.values().forEach(epic -> {
            epic.clearSubtaskId();
            updateEpicStatus(epic);
        });
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isOverlapping(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();

        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }
        return (start1.isBefore(end2) && end1.isAfter(start2));
    }

    private boolean isTaskOverlapping(Task newTask) {
        return prioritizedTasks.stream()
                .filter(task -> task.getStartTime() != null && task.getEndTime() != null)
                .anyMatch(existingTask -> isOverlapping(existingTask, newTask));
    }

}