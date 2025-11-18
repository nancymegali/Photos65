package photos.controller;

import photos.model.Album;

/**
 * A lightweight session helper used to pass data between controllers.
 * <p>
 * Currently, this class only stores the album that the user selected
 * from the album list, allowing different views/controllers
 * (such as the Photos view) to access it without needing direct reference
 * passing from FXML.
 * </p>
 *
 * <p>
 * This class acts as a shared state container rather than a full
 * application-wide session manager.
 * </p>
 *
 * @author 
 *     Nancy Megali & Safa Gokdemir
 *
 * @see photos.model.Album
 */
public class Session {

    /**
     * The album currently selected by the user in the Albums view.
     * This is set by {@code AlbumsController} and read by other controllers.
     */
    public static Album selectedAlbum;
}
