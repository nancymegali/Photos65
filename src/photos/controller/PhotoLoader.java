package photos.controller;

import java.io.File;

import photos.model.Album;
import photos.model.Photo;
import photos.model.User;

public class PhotoLoader {

    public static void loadStockPhotos(User stockUser) {

        Album stockAlbum = stockUser.getAlbum("stock");

        File dir = new File("data");
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".jpg"));

        if (files == null) return;

        for (File f : files) {
            Photo p = new Photo(f.getAbsolutePath());
            stockAlbum.addPhoto(p);
        }
    }
}
