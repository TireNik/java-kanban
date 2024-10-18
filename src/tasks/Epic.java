package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic(Integer id, String name, String description) {
        super(id, name, description, Progress.NEW);
        this.endTime = null;
        this.subtaskIds = new ArrayList<>();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }


    @Override
    public TypeTask getType() {
        return TypeTask.EPIC;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(Integer subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    public void clearSubtaskId() {
        subtaskIds.clear();
    }

    @Override
    public String toString() {
        return getId() + "," + getType() + "," + getName() + "," +
                getStatus() + "," + getDescription() + "," +
                getDuration() + "," + getStartTime() + "," + getEndTime();
    }
}
