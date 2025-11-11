package photos.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;


public class Album implements Serializable {
    private String name;                 
    private final List<Photo> photos;    

    public Album(String name) {
        this.name = name.trim();
        this.photos = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void rename(String newName) {
        this.name = newName.trim();
    }

    public List<Photo> getPhotos() {
        return Collections.unmodifiableList(photos);
    }

    public boolean addPhoto(Photo p) {
        if (photos.contains(p)) {
            return false; 
        }
        photos.add(p);
        return true;
    }

    public boolean removePhoto(Photo p) {
        return photos.remove(p);
    }

    public int size() {
        return photos.size();
    }

    public Optional<Instant> getEarliestDate() {
        return photos.stream()
                     .map(Photo::getDate)
                     .min(Comparator.naturalOrder());
    }

    public Optional<Instant> getLatestDate() {
        return photos.stream()
                     .map(Photo::getDate)
                     .max(Comparator.naturalOrder());
    }

    @Override
    public String toString() {
        String range = "";
        if (!photos.isEmpty()) {
            range = " [" + getEarliestDate().orElse(null) + " - " +
                            getLatestDate().orElse(null) + "]";
        }
        return String.format("Album{name='%s', photos=%d%s}", name, photos.size(), range);
    }
}
