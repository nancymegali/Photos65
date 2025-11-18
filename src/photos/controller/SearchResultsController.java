package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import javafx.stage.Stage;
import photos.Photos;
import photos.model.Album;
import photos.model.Photo;
import photos.model.Store;


import java.util.List;

/**
 * Controller for the Search Results window.
 * <p>
 * This controller displays a list of photos returned by a search query.
 * Users may:
 * <ul>
 *     <li>View all matching photos</li>
 *     <li>Create a new album from the search results</li>
 *     <li>Close the search results window</li>
 * </ul>
 * </p>
 *
 * <p>
 * Results are supplied by {@link AlbumsController} through
 * {@link #setResults(List)} when the search dialog is completed.
 * </p>
 *
 * @author Nancy Megali & Safa Gokdemir
 */
public class SearchResultsController {

    /** ListView displaying all photos returned by the search. */
    @FXML private ListView<Photo> resultsList;

    /** The photo results passed from the search dialog. */
    private List<Photo> results;

    /**
     * Receives the search results and populates the ListView.
     *
     * @param photos a list of photos matching the user's search criteria
     */
    public void setResults(List<Photo> photos) {
        this.results = photos;
        resultsList.getItems().setAll(photos);
        configureResultsCellFactory();
    }

    /**
     * Creates a new album containing all photos from the search results.
     * <p>
     * Prompts the user for an album name, validates it, and then:
     * <ul>
     *     <li>Creates a new {@link Album}</li>
     *     <li>Adds all search result photos to the album</li>
     *     <li>Saves changes to persistent storage</li>
     * </ul>
     * </p>
     */
    @FXML
    private void handleCreateAlbum() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter new album name:");
        dialog.setContentText("Name:");

        var name = dialog.showAndWait();
        if (name.isEmpty()) return;

        String albumName = name.get().trim();
        if (albumName.isEmpty()) return;

        // Check for duplicate album name
        if (Photos.currentUser.getAlbum(albumName) != null) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "An album with that name already exists. Please choose another name.");
            alert.showAndWait();
            return;
        }

        // Create album via User API so everything stays consistent
        if (!Photos.currentUser.addAlbum(albumName)) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Failed to create album. Name might already exist.");
            alert.showAndWait();
            return;
        }

        Album a = Photos.currentUser.getAlbum(albumName);
        for (Photo p : results) {
            a.addPhoto(p);
        }

        Store.save(Photos.store);

        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "Album created successfully.");
        alert.showAndWait();
    }

    /**
    * Configures the ListView to display thumbnails and captions for results.
    */
    private void configureResultsCellFactory() {
        resultsList.setCellFactory(listView -> new ListCell<Photo>() {
            private final ImageView thumb = new ImageView();

            {
                thumb.setFitWidth(80);
                thumb.setFitHeight(60);
                thumb.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(Photo item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                File f = new File(item.getFilePath());
                if (f.exists()) {
                    thumb.setImage(new Image(f.toURI().toString(), 80, 60, true, true));
                    setGraphic(thumb);
                } else {
                    setGraphic(null);
                }

                String caption = item.getCaption();
                setText(caption == null || caption.isEmpty() ? f.getName() : caption);
            }
        });
    }

    /**
     * Closes the search results window.
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) resultsList.getScene().getWindow();
        stage.close();
    }
}
