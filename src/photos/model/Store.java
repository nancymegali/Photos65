package photos.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Store implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, User> users;

    public Store() {
        users = new HashMap<>();
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User getUser(String username) {
        return users.get(username.toLowerCase());
    }

    public User createUser(String username) {
        username = username.toLowerCase();
        if (users.containsKey(username)) return null;
        User u = new User(username);
        users.put(username, u);
        return u;
    }

    public boolean deleteUser(String username) {
        return users.remove(username.toLowerCase()) != null;
    }
}
