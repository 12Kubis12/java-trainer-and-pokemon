package ParentPackage.db;

public class Trainer extends FromTable {

    public Trainer(int id, String name) {
        super(name, id);
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + super.id +
                ", name='" + super.name + '\'' +
                '}';
    }
}
