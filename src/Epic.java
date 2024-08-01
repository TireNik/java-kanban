public class Epic extends Task {

    public Epic(Integer id, String name, String description, Progress status) {
        super(id, name, description, status);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "имя: '" + getName() + '\'' +
                ", описание: '" + getDescription() + '\'' +
                ", статус: " + getStatus() +
                '}';
    }
}
