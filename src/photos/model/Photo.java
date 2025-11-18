package photos.model;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single photo in the Photos application.
 * <p>
 * Each photo stores:
 * <ul>
 *     <li>its absolute file path,</li>
 *     <li>a user-defined caption,</li>
 *     <li>the date/time the photo was last modified (used as the taken date),</li>
 *     <li>a set of associated {@link Tag} objects.</li>
 * </ul>
 * <p>
 * Photos are uniquely identified by their file path, and equality is defined
 * case-insensitively on that path. Photo objects are serializable so they may
 * be stored and loaded across application sessions.
 * </p>
 *
 * @author Nancy Megali & Safa Gokdemir
 */
public class Photo implements Serializable {

    /** Serialization identifier for version compatibility. */
    private static final long serialVersionUID = 1L;

    private static final Set<String> SINGLE_VALUE_TAGS = Set.of("location");

    /** The absolute or relative path to the photo file on disk. */
    private String filePath;

    /** The caption associated with this photo (may be empty). */
    private String caption;

    /** The date and time the photo was taken (derived from file modification time). */
    private LocalDateTime dateTime;

    /** The set of tags associated with this photo. */
    private Set<Tag> tags;

    /**
     * Constructs a Photo object from a file path.
     * <p>
     * The timestamp of the photo is determined from the file's last modified time
     * and converted into a {@link LocalDateTime} without nanoseconds.
     * </p>
     *
     * @param filePath the file system path to the photo
     */
    public Photo(String filePath) {
        this.filePath = filePath;
        this.tags = new HashSet<>();
        this.caption = "";

        File f = new File(filePath);
        long t = f.lastModified();

        this.dateTime = Instant.ofEpochMilli(t)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .withNano(0);
    }

    /**
     * Returns the file path of this photo.
     *
     * @return the photo's file path
     */
    public String getFilePath() { 
        return filePath; 
    }

    /**
     * Returns the caption of this photo.
     *
     * @return the caption text
     */
    public String getCaption() { 
        return caption; 
    }

    /**
     * Sets a new caption for the photo.
     *
     * @param caption the caption text
     */
    public void setCaption(String caption) { 
        this.caption = caption; 
    }

    /**
     * Returns the date and time this photo was taken.
     *
     * @return the {@link LocalDateTime} timestamp
     */
    public LocalDateTime getDateTime() { 
        return dateTime; 
    }

    /**
     * Returns the set of tags associated with this photo.
     *
     * @return the set of {@link Tag} objects
     */
    public Set<Tag> getTags() { 
        return tags; 
    }

    /**
     * Adds a tag to this photo.
     *
     * @param name  the tag name
     * @param value the tag value
     * @return true if the tag was added, false if it already exists
     */
    public boolean addTag(String name, String value) {
        Tag newTag = new Tag(name, value);

        // If this tag type is single-valued, remove any existing tags of same name
        if (SINGLE_VALUE_TAGS.contains(newTag.getName())) {
            tags.removeIf(t -> t.getName().equals(newTag.getName()));
        }

        return tags.add(newTag);
    }

    /**
     * Removes a tag from this photo.
     *
     * @param name  the tag name
     * @param value the tag value
     * @return true if the tag existed and was removed; false otherwise
     */
    public boolean removeTag(String name, String value) {
        return tags.remove(new Tag(name, value));
    }

    /**
     * Photos are considered equal if their file paths are equal
     * ignoring case. This ensures uniqueness across albums.
     *
     * @param o the object to compare
     * @return true if both photos refer to the same file path
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;
        Photo other = (Photo) o;
        return this.filePath.equalsIgnoreCase(other.filePath);
    }

    /**
     * Hash code based on a case-insensitive version of the file path.
     *
     * @return hash code for this photo
     */
    @Override
    public int hashCode() {
        return filePath.toLowerCase().hashCode();
    }

    /**
     * Returns a string representation of the photo.
     * Displays the caption if present; otherwise uses the file path.
     *
     * @return a textual representation of the photo
     */
    @Override
    public String toString() {
        return caption.isEmpty() ? filePath : caption;
    }
}
