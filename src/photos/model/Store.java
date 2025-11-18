package photos.model;

import java.io.*;
import java.util.*;

/**
 * Central storage class for all users in the Photos application.
 * <p>
 * The Store object maintains a mapping of usernames to {@link User} objects.
 * It is responsible for creating, deleting, and retrieving users, and is
 * serialized to disk to persist user data across sessions. The Store is loaded
 * on application startup and saved when the application exits.
 * </p>
 *
 * <p>
 * Usernames are case-insensitive and stored internally in lowercase to
 * ensure consistent access. The serialized data is written to the file
 * {@link #STORE_FILE}.
 * </p>
 *
 * @author Nancy Megali & Safa Gokdemir
 */
public class Store implements Serializable {

    /** Serialization identifier for compatibility. */
    private static final long serialVersionUID = 1L;

    /**
     * Mapping of lowercase usernames to corresponding {@link User} objects.
     */
    private Map<String, User> users = new HashMap<>();

    /**
     * File path where the serialized Store object is saved.
     */
    public static final String STORE_FILE = "data-store/users.ser";

    /**
     * Retrieves a user by username (case-insensitive).
     *
     * @param name the username to search for
     * @return the {@link User} object, or null if none exists
     */
    public User getUser(String name) {
        return users.get(name.toLowerCase());
    }

    /**
     * Returns a collection of all users stored in the system.
     *
     * @return a collection of User objects
     */
    public Collection<User> getUsers() {
        return users.values();
    }

    /**
     * Creates a new user with the given name.
     * <p>
     * Usernames are normalized to lowercase and must be unique.
     * </p>
     *
     * @param name the username of the new user
     * @return true if the user was created; false if the user already exists
     */
    public boolean createUser(String name) {
        name = name.toLowerCase();
        if (users.containsKey(name)) return false;

        users.put(name, new User(name));
        return true;
    }

    /**
     * Deletes a user by username.
     *
     * @param name the username of the user to delete
     * @return true if the user existed and was deleted; false otherwise
     */
    public boolean deleteUser(String name) {
        name = name.toLowerCase();
        if (!users.containsKey(name)) return false;

        users.remove(name);
        return true;
    }

    /**
     * Loads the Store object from disk.
     * <p>
     * If no saved data exists, a new Store is created and returned.
     * If loading fails (due to corruption or incompatible version),
     * a new empty Store is returned.
     * </p>
     *
     * @return the loaded Store object, or a new empty Store if loading fails
     */
    public static Store load() {
        File f = new File(STORE_FILE);

        if (!f.exists()) {
            return new Store();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (Store) ois.readObject();
        } catch (Exception e) {
            // Do NOT print to console; do NOT show UI dialog here
            return new Store();
        }
    }


    /**
     * Saves the current Store object to disk using serialization.
     *
     * @param store the Store object to save
     */
    public static void save(Store store) {
        try {
            File f = new File(STORE_FILE);

            // Ensure parent directory exists
            f.getParentFile().mkdirs();

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
                oos.writeObject(store);
            }
        } catch (Exception e) {
            // System.out.println("ERROR saving store:");
            // e.printStackTrace();
        }
    }
}
