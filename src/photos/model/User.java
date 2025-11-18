package photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user of the Photos application.
 * <p>
 * Each user maintains a personal collection of {@link Album} objects.
 * This class provides operations to create, delete, rename, and retrieve
 * albums belonging to the user. User objects are serialized to disk
 * so that album information persists across application sessions.
 * </p>
 *
 * @author Nancy Megali & Safa Gokdemir
 */
public class User implements Serializable {

    /** Serialization identifier for compatibility. */
    private static final long serialVersionUID = 1L;

    /** The unique username for this user. */
    private String username;

    /** The list of albums owned by this user. */
    private List<Album> albums;

    /**
     * Constructs a new User with the given username.
     *
     * @param username the username for the user
     */
    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<>();
    }

    /**
     * Returns the username of this user.
     *
     * @return the username
     */
    public String getUsername() { 
        return username; 
    }

    /**
     * Returns the list of albums belonging to this user.
     *
     * @return list of albums
     */
    public List<Album> getAlbums() { 
        return albums; 
    }

    /**
     * Retrieves an album by name, ignoring case.
     *
     * @param name the album name to search for
     * @return the matching album, or null if none found
     */
    public Album getAlbum(String name) {
        for (Album a : albums) {
            if (a.getName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Adds a new album with the given name, if it does not already exist.
     *
     * @param name the name of the new album
     * @return true if the album was added; false if an album
     *         with the same name already exists
     */
    public boolean addAlbum(String name) {
        if (getAlbum(name) != null) return false;
        albums.add(new Album(name));
        return true;
    }

    /**
     * Deletes an album with the given name.
     *
     * @param name the name of the album to remove
     * @return true if the album existed and was removed; false otherwise
     */
    public boolean deleteAlbum(String name) {
        Album a = getAlbum(name);
        if (a == null) return false;
        albums.remove(a);
        return true;
    }

    /**
     * Renames an existing album.
     *
     * @param oldName the current name of the album
     * @param newName the desired new name
     * @return true if the rename succeeded; false if the new name already
     *         exists or the old name does not exist
     */
    public boolean renameAlbum(String oldName, String newName) {
        if (getAlbum(newName) != null) return false; // name already exists
        Album a = getAlbum(oldName);
        if (a == null) return false; // old album not found

        a.setName(newName);
        return true;
    }
}
