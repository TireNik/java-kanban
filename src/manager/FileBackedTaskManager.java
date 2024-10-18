package manager;

import exeptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Stream;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;
    private static final String HEADER = "id,type,name,status,description,epic,duration,startTime,endTime";

    public FileBackedTaskManager(File file) {
        this.file = file;
        loadFromFile();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(HEADER);
            writer.newLine();

            Stream.concat(Stream.concat(getAllTasks().stream(), getAllEpics().stream()),
                            getAllSubTasks().stream())
                    .map(FileBackedTaskManager::toString)
                    .forEach(taskString -> {
                        try {
                            writer.write(taskString);
                            writer.newLine();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });

        } catch (IOException e) {
            throw new ManagerSaveException("Данные не сохранены");
        }
    }

    private void loadFromFile() {
        try {
            if (file.exists()) {
                List<String> lines = Files.readAllLines(file.toPath());

                int maxId = lines.stream()
                        .filter(line -> !line.isEmpty() && !line.startsWith("id,"))
                        .map(line -> {
                            Task task = fromString(line);

                            if (task.getType().equals(TypeTask.SUBTASK)) {
                                if (!getSubTaskMap().containsKey(task.getId())) {
                                    addSubTaskDirectly((SubTask) task);
                                }
                            } else if (task.getType().equals(TypeTask.EPIC)) {
                                if (!getEpicMap().containsKey(task.getId())) {
                                    addEpicDirectly((Epic) task);
                                }
                            } else {
                                if (!getTaskMap().containsKey(task.getId())) {
                                    addTaskDirectly(task);
                                }
                            }
                            return task.getId();
                        })
                        .max(Integer::compareTo)
                        .orElse(0);

                updateIdCounter(maxId);
                restoreEpicSubtasks();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла: " + e.getMessage());
        }
    }

    private void restoreEpicSubtasks() {
        for (SubTask subTask : getSubTaskMap().values()) {
            Epic epic = getEpicMap().get(subTask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
        }
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TypeTask type = TypeTask.valueOf(fields[1]);
        String name = fields[2];
        Progress status = Progress.valueOf(fields[3]);
        String description = fields[4];

        Duration duration = null;
        if (fields[6] != null && !fields[6].equals("null")) {
            try {
                duration = Duration.parse(fields[6]);
            } catch (DateTimeParseException e) {
                duration = Duration.ofMinutes(Long.parseLong(fields[6]));
            }
        }

        LocalDateTime startTime = null;
        if (fields[7] != null && !fields[7].equals("null")) {
            startTime = LocalDateTime.parse(fields[7]);
        }

        return switch (type) {
            case SUBTASK -> {
                int epicId = Integer.parseInt(fields[5]);
                yield new SubTask(id, name, description, status, epicId, duration, startTime);
            }
            case EPIC -> new Epic(id, name, description);
            case TASK -> new Task(id, name, description, status, duration, startTime);
            default -> throw new IllegalArgumentException("Неизвестный тип: " + type);
        };
    }

    private static String toString(Task task) {
        TypeTask type = task.getType();

        return switch (type) {
            case SUBTASK -> {
                SubTask subTask = (SubTask) task;
                yield subTask.getId() + "," + subTask.getType() + "," + subTask.getName() + "," +
                        subTask.getStatus() + "," + subTask.getDescription() + "," + subTask.getEpicId() + "," +
                        subTask.getDuration() + "," +
                        subTask.getStartTime() + "," + subTask.getEndTime();
            }
            case EPIC -> {
                Epic epic = (Epic) task;
                yield epic.getId() + "," + epic.getType() + "," + epic.getName() + "," + epic.getStatus() + "," +
                        epic.getDescription() + ",," + epic.getDuration() + "," +
                        epic.getStartTime() + "," + epic.getEndTime();
            }
            case TASK ->
                    task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," +
                            task.getDescription() + ",," + task.getDuration() + "," + task.getStartTime() + "," +
                            task.getEndTime();
        };
    }

    @Override
    public Task addTask(Task task) {
        Task newTask = super.addTask(task);
        save();
        return newTask;
    }

    @Override
    public SubTask addSubtask(SubTask subTask) {
        SubTask newSubTask = super.addSubtask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic newEpic = super.addEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Task updateTask(Task updateTask) {
        Task updatedTask = super.updateTask(updateTask);
        if (updatedTask != null) {
            save();
        }
        return updatedTask;
    }

    @Override
    public SubTask updateSubtask(SubTask subTask) {
        SubTask updatedSubtask = super.updateSubtask(subTask);
        if (updatedSubtask != null) {
            save();
        }
        return updatedSubtask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        if (updatedEpic != null) {
            save();
        }
        return updatedEpic;
    }

    @Override
    public Boolean deleteTaskById(Integer id) {
        Boolean isDeleted = super.deleteTaskById(id);
        if (isDeleted) {
            save();
        }
        return isDeleted;
    }

    @Override
    public Boolean deleteSubtaskForId(Integer id) {
        Boolean isDeleted = super.deleteSubtaskForId(id);
        if (isDeleted) {
            save();
        }
        return isDeleted;
    }

    @Override
    public Boolean deleteEpicById(Integer id) {
        Boolean isDeleted = super.deleteEpicById(id);
        if (isDeleted) {
            save();
        }
        return isDeleted;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

}
