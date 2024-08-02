public class SubTask extends Task{

    private Integer epicId;

    public SubTask(Integer id, String name, String description, Progress status, Integer epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {

        return "subTask {" +
                "имя: '" + getName() + " Id " + getId() + '\'' +
                ", описание: '" + getDescription() + '\'' +
                ", статус: " + getStatus() +
                ", epicId=" + epicId +
                '}';
    }
}
