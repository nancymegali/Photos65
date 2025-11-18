package photos.model;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String filePath;      
    private String caption;
    private LocalDateTime dateTime; 
    private Set<Tag> tags;

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

    public String getFilePath() { return filePath; }
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    public LocalDateTime getDateTime() { return dateTime; }

    public Set<Tag> getTags() { return tags; }

    public boolean addTag(String name, String value) {
        return tags.add(new Tag(name, value));
    }

    public boolean removeTag(String name, String value) {
        return tags.remove(new Tag(name, value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;
        Photo other = (Photo) o;
        return this.filePath.equalsIgnoreCase(other.filePath);
    }

    @Override
    public int hashCode() {
        return filePath.toLowerCase().hashCode();
    }


    @Override
    public String toString() {
        return caption.isEmpty() ? filePath : caption;
    }
}
