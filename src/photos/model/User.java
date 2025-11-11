package photos.model;

import java.io.Serializable;
import java.util.*;


public class User implements Serializable {
    private final String username;
    private final Map<String, Album> albums; 

    public User(String username) {
        this.username = username.trim().toLowerCase();
        this.albums = new LinkedHashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public Collection<Album> getAlbums() {
        return Collections.unmodifiableCollection(albums.values());
    }

    public Album getAlbum(String name) {
        return albums.get(name.trim().toLowerCase());
    }

    public boolean createAlbum(String name) {
        String key = name.trim().toLowerCase();
        if (albums.containsKey(key)) return false;
        albums.put(key, new Album(name));
        return true;
    }

    public boolean deleteAlbum(String name) {
        return albums.remove(name.trim().toLowerCase()) != null;
    }

    public boolean renameAlbum(String oldName, String newName) {
        String oldKey = oldName.trim().toLowerCase();
        if (!albums.containsKey(oldKey)) return false;
        Album a = albums.remove(oldKey);
        a.rename(newName);
        albums.put(newName.trim().toLowerCase(), a);
        return true;
    }

    public int getAlbumCount() {
        return albums.size();
    }

    @Override
    public String toString() {
        return String.format("User[%s, %d albums]", username, albums.size());
    }
}
