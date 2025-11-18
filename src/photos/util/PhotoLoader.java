package photos.util;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import photos.model.Album;
import photos.model.Photo;
import photos.model.User;

import java.io.File;

public class PhotoLoader {

    public static Photo choosePhoto(Window ownerWindow) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a Photo");

        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.bmp", "*.gif")
        );

        File file = chooser.showOpenDialog(ownerWindow);
        if (file == null) return null;

        return new Photo(file.getAbsolutePath());
    }



    public static void loadStockPhotos(User user) {
        Album album = user.getAlbum("stock");
        if (album == null) return;

        File dataDir = new File("data");
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            System.out.println("ERROR: data/ folder not found.");
            return;
        }

        File[] files = dataDir.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (isImage(f)) {
                Photo p = new Photo("data/" + f.getName());
                album.addPhoto(p);
            }
        }
    }


    private static boolean isImage(File f) {
        String name = f.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg")
                || name.endsWith(".png") || name.endsWith(".bmp")
                || name.endsWith(".gif");
    }
}
