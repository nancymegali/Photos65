package photos.model;

import java.io.*;

public class Persistence {

    private static final String STORE_PATH = "data-store/stock.ser";

    public static Store loadStore() {
        try {
            File f = new File(STORE_PATH);
            if (!f.exists()) return new Store();

            ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(f));
            Store s = (Store) ois.readObject();
            ois.close();
            return s;

        } catch (Exception e) {
            e.printStackTrace();
            return new Store();
        }
    }

    public static void saveStore(Store s) {
        try {
            ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(STORE_PATH));
            oos.writeObject(s);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
