package photos.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an album owned by a user in the Photos application.
 * <p>
 * An album is a named collection of {@link Photo} objects. This class
 * provides operations for adding, removing, and retrieving photos as well as
 * determining chronological information such as earliest and latest photo
 * dates. Album objects are serialized to disk along with their containing
 * {@link photos.model.User}.
 * </p>
 *
 * @author Nancy Megali & Safa Gokdemir
 */
public class Album implements Serializable {

    /** Serialization identifier for compatibility. */
    private static final long serialVersionUID = 1L;

    /** The name of the album. */
    private String name;

    /** The list of photos contained in this album. */
    private List<Photo> photos;

    /**
     * Constructs a new Album with the given name.
     *
     * @param name the name of the album
     */
    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    /**
     * Returns the name of this album.
     *
     * @return the album name
     */
    public String getName() { 
        return name; 
    }

    /**
     * Sets a new name for this album.
     *
     * @param name the new album name
     */
    public void setName(String name) { 
        this.name = name; 
    }

    /**
     * Returns the list of photos in this album.
     *
     * @return the list of photos
     */
    public List<Photo> getPhotos() { 
        return photos; 
    }

    /**
     * Adds a photo to the album if it is not already present.
     *
     * @param p the photo to add
     * @return true if the photo was added; false if it already exists
     */
    public boolean addPhoto(Photo p) {
        if (photos.contains(p)) return false;
        return photos.add(p);
    }

    /**
     * Removes a photo from the album.
     *
     * @param p the photo to remove
     * @return true if the photo was present and removed; false otherwise
     */
    public boolean removePhoto(Photo p) {
        return photos.remove(p);
    }

    /**
     * Returns the number of photos in the album.
     *
     * @return the photo count
     */
    public int getNumPhotos() { 
        return photos.size(); 
    }

    /**
     * Returns the earliest (oldest) photo date in the album.
     *
     * @return the earliest {@link LocalDateTime}, or null if the album is empty
     */
    public LocalDateTime getEarliestDate() {
        return photos.stream()
                .map(Photo::getDateTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    /**
     * Returns the latest (most recent) photo date in the album.
     *
     * @return the latest {@link LocalDateTime}, or null if the album is empty
     */
    public LocalDateTime getLatestDate() {
        return photos.stream()
                .map(Photo::getDateTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    /**
     * Returns the album name as its string representation.
     *
     * @return the album name
     */
    @Override
    public String toString() {
        return name;
    }
}
