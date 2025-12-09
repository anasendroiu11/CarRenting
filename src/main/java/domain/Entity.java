package domain;

import java.io.Serial;
import java.io.Serializable;

public abstract class Entity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int id;

    public Entity(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
