public class SubTask extends Task{

    public SubTask(String name, String description, Progress status){
        super(name, description, status);
    }

    @Override
    public String toString() {

        return "subTask {" +
                "имя: '" + getName() + '\'' +
                ", описание: '" + getDescription() + '\'' +
                ", статус: " + getStatus() +
                '}';
    }
}
