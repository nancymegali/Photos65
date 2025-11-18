package photos.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<Photo> photos;

    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Photo> getPhotos() { return photos; }

    public boolean addPhoto(Photo p) {
        if (photos.contains(p)) return false;
        return photos.add(p);
    }

    public boolean removePhoto(Photo p) {
        return photos.remove(p);
    }

    public int getNumPhotos() { return photos.size(); }

    public LocalDateTime getEarliestDate() {
        return photos.stream()
                .map(Photo::getDateTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    public LocalDateTime getLatestDate() {
        return photos.stream()
                .map(Photo::getDateTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public String toString() {
        return name;
    }
}
