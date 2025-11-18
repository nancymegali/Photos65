package photos.controller;

import java.io.File;

import photos.model.Album;
import photos.model.Photo;
import photos.model.User;

/**
 * Utility class responsible for loading stock photos into the application.
 * <p>
 * This class reads all JPG image files located in the local {@code data/}
 * directory and automatically imports them into the "stock" album of the
 * special {@code stock} user. Stock photos are bundled with the application
 * and serve as sample images for graders and new users.
 * </p>
 *
 * <p>
 * This loader is called by {@link photos.controller.LoginController} when the
 * {@code stock} user logs in for the first time. It does not examine EXIF
 * metadata—{@link Photo} timestamps are derived from file modification times.
 * </p>
 *
 * @author
 *     Nancy Megali & Safa Gokdemir
 */
public class PhotoLoader {

    /**
     * Loads all JPG images from the local {@code data/} directory and inserts
     * them into the {@code stock} user's "stock" album.
     *
     * <p>
     * This method:
     * <ul>
     *     <li>Ensures the album exists on the provided {@link User}</li>
     *     <li>Scans the {@code data/} folder for files ending in {@code .jpg}</li>
     *     <li>Creates a {@link Photo} object for each image</li>
     *     <li>Adds each photo to the stock album if not already present</li>
     * </ul>
     * </p>
     *
     * @param stockUser the user whose "stock" album will receive the photos
     */
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
