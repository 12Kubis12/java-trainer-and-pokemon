package ParentPackage.db;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Trainer trainer = (Trainer) o;
        return super.id == trainer.id && Objects.equals(super.name, trainer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.name, super.id);
    }
}
