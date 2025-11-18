package photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private List<Album> albums;

    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<>();
    }

    public String getUsername() { return username; }
    public List<Album> getAlbums() { return albums; }

    public Album getAlbum(String name) {
        for (Album a : albums) {
            if (a.getName().equalsIgnoreCase(name))
                return a;
        }
        return null;
    }

    public boolean addAlbum(String name) {
        if (getAlbum(name) != null) return false;
        albums.add(new Album(name));
        return true;
    }

    public boolean deleteAlbum(String name) {
        Album a = getAlbum(name);
        if (a == null) return false;
        albums.remove(a);
        return true;
    }

    public boolean renameAlbum(String oldName, String newName) {
        if (getAlbum(newName) != null) return false;
        Album a = getAlbum(oldName);
        if (a == null) return false;
        a.setName(newName);
        return true;
    }
}
