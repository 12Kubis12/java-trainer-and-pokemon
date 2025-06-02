package ParentPackage.db;

import java.util.Objects;

public abstract class FromTable {
    protected final String name;
    protected final int id;

    public FromTable(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FromTable fromTable = (FromTable) o;
        return this.id == fromTable.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
