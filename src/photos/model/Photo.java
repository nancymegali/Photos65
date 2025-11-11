package photos.model;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;


public class Photo implements Serializable {
    private final String filePath;             
    private String caption;                   
    private final Set<Tag> tags;          
    private final Instant date;                

    public Photo(String filePath) {
        this.filePath = filePath;
        this.tags = new LinkedHashSet<>();
        this.caption = "";

        File f = new File(filePath);
        this.date = Instant.ofEpochMilli(f.lastModified());
    }

    public String getFilePath() {
        return filePath;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption == null ? "" : caption.trim();
    }

    public Instant getDate() {
        return date;
    }

    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public boolean addTag(Tag t) {
        return tags.add(t);
    }

    public boolean removeTag(Tag t) {
        return tags.remove(t);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;
        Photo p = (Photo) o;
        return filePath.equalsIgnoreCase(p.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath.toLowerCase());
    }

    @Override
    public String toString() {
        return String.format("Photo[%s, caption=%s, date=%s]", filePath, caption, date);
    }
}
