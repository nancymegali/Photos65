package photos.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a single tag associated with a {@link Photo}.
 * <p>
 * A tag consists of a tag <b>name</b> (such as "location" or "person")
 * and a corresponding <b>value</b> (such as "paris" or "john"). Both name
 * and value are normalized to lowercase and trimmed to ensure consistent
 * comparisons across the application.
 * </p>
 * <p>
 * Tags are considered equal if both their name and value match exactly
 * (case-insensitive due to normalization). This prevents duplicate tags from
 * being added to a photo. Tag objects are serializable for persistent storage.
 * </p>
 *
 * @author Nancy Megali & Safa Gokdemir
 */
public class Tag implements Serializable {

    /** Serialization identifier for compatibility. */
    private static final long serialVersionUID = 1L;

    /** The tag's name (normalized to lowercase). */
    private String name;

    /** The tag's value (normalized to lowercase). */
    private String value;

    /**
     * Constructs a tag with the given name and value.
     * <p>
     * Both fields are stored in lowercase and trimmed to ensure that
     * comparisons remain consistent across different photo albums.
     * </p>
     *
     * @param name  the tag type (e.g., "location", "person")
     * @param value the tag value (e.g., "paris", "sarah")
     */
    public Tag(String name, String value) {
        this.name = name.trim().toLowerCase();
        this.value = value.trim().toLowerCase();
    }

    /**
     * Returns the tag's name.
     *
     * @return the name of the tag
     */
    public String getName() { 
        return name; 
    }

    /**
     * Returns the tag's value.
     *
     * @return the value of the tag
     */
    public String getValue() { 
        return value; 
    }

    /**
     * Determines equality between two tags.
     * <p>
     * Tags are equal if both their name and value match. This allows tags to
     * be used inside sets and prevents duplicate tags from being added to
     * photos.
     * </p>
     *
     * @param o the object to compare
     * @return true if the tags have the same name and value, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag t = (Tag) o;
        return this.name.equals(t.name) && this.value.equals(t.value);
    }

    /**
     * Computes a hash code consistent with {@link #equals(Object)}.
     *
     * @return the hash code for this tag
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    /**
     * Returns a human-readable string representation of the tag.
     * Format: {@code name=value}
     *
     * @return the tag formatted as "name=value"
     */
    @Override
    public String toString() {
        return name + "=" + value;
    }
}
