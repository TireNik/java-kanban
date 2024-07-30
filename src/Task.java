public class Task {
    private  String name;
    private  String description;
    private Progress status;

    public Task(String name, String description, Progress status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
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
