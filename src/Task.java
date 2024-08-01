public class Task {
    private Integer id;
    private  String name;
    private  String description;
    private Progress status;

    public Task(Integer id, String name, String description, Progress status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(Progress status) {
        this.status = status;
    }

    public Progress getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Task {" +
                "имя: '" + name + '\'' +
                ", описание: '" + description + '\'' +
                ", статус: " + status +
                '}';
    }
}
