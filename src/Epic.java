import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(Integer id, String name, String description) {
        super(id, name, description, Progress.NEW);
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
        return "Epic{" +
                "имя: '" + getName() + " Id " + getId()+ '\'' +
                ", описание: '" + getDescription() + '\'' +
                ", статус: " + getStatus() +
                '}';
    }
}
