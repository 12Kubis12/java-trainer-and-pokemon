package ParentPackage.db;

public class Pokemon extends FromTable {
    private int trainer_id;

    public Pokemon(int id, String name, int trainer_id) {
        super(name, id);
        this.trainer_id = trainer_id;
    }

    public int getTrainerId() {
        return this.trainer_id;
    }

    public void setTrainer_id(int trainer_id) {
        this.trainer_id = trainer_id;
    }

    @Override
    public String toString() {
        return "Pokemon{" +
                "id=" + super.id +
                ", name='" + super.name + '\'' +
                ", trainer_id=" + this.trainer_id +
                '}';
    }
}
