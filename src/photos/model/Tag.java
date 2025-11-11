package photos.model;

import java.io.Serializable;
import java.util.Objects;


public class Tag implements Serializable {
    private final String name;
    private final String value;

    public Tag(String name, String value) {
        this.name = name.trim().toLowerCase();
        this.value = value.trim().toLowerCase();
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag t = (Tag) o;
        return name.equals(t.name) && value.equals(t.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}
