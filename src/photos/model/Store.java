package photos.model;

import java.io.*;
import java.util.*;


public class Store implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, User> users = new HashMap<>();

    public static final String STORE_FILE = "data-store/users.ser";

    public User getUser(String name) {
        return users.get(name.toLowerCase());
    }

    public Collection<User> getUsers() {
        return users.values();
    }

    /**
     * Create a user.
     * @return true if created, false if user already exists
     */
    public boolean createUser(String name) {
        name = name.toLowerCase();
        if (users.containsKey(name)) return false;

        users.put(name, new User(name));
        return true;
    }

    public boolean deleteUser(String name) {
        name = name.toLowerCase();
        if (!users.containsKey(name)) return false;

        users.remove(name);
        return true;
    }

    public static Store load() {
        File f = new File(STORE_FILE);
        if (!f.exists()) {
            return new Store();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (Store) ois.readObject();
        } catch (Exception e) {
            System.out.println("Failed to load store. Creating new.");
            return new Store();
        }
    }

    public static void save(Store store) {
        try {
            File f = new File(STORE_FILE);
            f.getParentFile().mkdirs(); 

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
                oos.writeObject(store);
            }
        } catch (Exception e) {
            System.out.println("ERROR saving store:");
            e.printStackTrace();
        }
    }
}
