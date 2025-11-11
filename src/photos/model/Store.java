package photos.model;

import java.io.*;
import java.nio.file.*;
import java.util.*;


public class Store {
    private final Path storeDir; 

    public Store(Path storeDir) {
        this.storeDir = storeDir;
    }

    
    public Map<String, User> loadAllUsers() throws IOException, ClassNotFoundException {
        Map<String, User> map = new LinkedHashMap<>();

        if (!Files.exists(storeDir)) {
            return map;
        }

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(storeDir, "*.ser")) {
            for (Path p : ds) {
                try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(p))) {
                    User u = (User) ois.readObject();
                    map.put(u.getUsername(), u);
                }
            }
        }
        return map;
    }

    
    public void saveUser(User user) throws IOException {
        Files.createDirectories(storeDir);
        Path file = storeDir.resolve(user.getUsername() + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
            oos.writeObject(user);
        }
    }

    
    public boolean deleteUser(String username) throws IOException {
        Path file = storeDir.resolve(username.toLowerCase() + ".ser");
        if (Files.exists(file)) {
            Files.delete(file);
            return true;
        }
        return false;
    }
}
