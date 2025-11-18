package photos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import photos.Photos;
import photos.model.*;
import photos.util.PhotoLoader;

import java.io.File;
import java.util.*;

/**
 * Controller for the photo view within a specific album.
 * <p>
 * This controller manages all interactions that occur while viewing
 * or modifying the contents of a single album, including:
 * <ul>
 *     <li>Viewing photos and navigating through them</li>
 *     <li>Adding, deleting, copying, and moving photos</li>
 *     <li>Editing captions</li>
 *     <li>Adding and removing tags</li>
 *     <li>Displaying album metadata (photo count, earliest/latest date)</li>
 *     <li>Returning to the album list view</li>
 * </ul>
 * The controller is connected to {@code PhotosView.fxml}.
 * </p>
 *
 * @author 
 *     Nancy Megali & Safa Gokdemir
 */
public class PhotosController {

    /** Label displaying the name of the album currently being viewed. */
    @FXML private Label albumNameLabel;

    /** ListView containing all photos in the album. */
    @FXML private ListView<Photo> photoList;

    /** Large preview area for displaying the currently selected photo. */
    @FXML private ImageView photoView;

    /** Text field for editing a photo's caption. */
    @FXML private TextField captionField;

    /** ListView displaying all tags belonging to the selected photo. */
    @FXML private ListView<Tag> tagList;

    /** Displays the count of photos in the album. */
    @FXML private Label countLabel;

    /** Displays the earliest date among all photos in the album. */
    @FXML private Label startDateLabel;

    /** Displays the latest date among all photos in the album. */
    @FXML private Label endDateLabel;

    /** The album currently being viewed. */
    private Album album;

    /** Index of the selected photo within the album's photo list. */
    private int index = -1;

    /**
     * Sets the active album when this view is loaded.
     * Called by {@link AlbumsController} after FXML loading.
     *
     * @param album the album to display
     */
    public void setAlbum(Album album) {
        this.album = album;
        albumNameLabel.setText(album.getName());
        refreshPhotoList();
    }

    /**
     * Reloads the ListView contents and resets preview information.
     */
    private void refreshPhotoList() {
        photoList.getItems().setAll(album.getPhotos());

        updateAlbumInfo();

        if (!album.getPhotos().isEmpty()) {
            index = 0;
            photoList.getSelectionModel().select(0);
            loadPhoto(0);
        } else {
            index = -1;
            clearPhotoDisplay();
        }
    }

    /**
     * Clears the preview area (image, caption, and tags).
     * Used when an album contains no photos.
     */
    private void clearPhotoDisplay() {
        photoView.setImage(null);
        captionField.setText("");
        tagList.getItems().clear();
    }

    /**
     * Updates the metadata panel on the right side of the UI,
     * displaying photo count and earliest/latest timestamps.
     */
    private void updateAlbumInfo() {
        countLabel.setText("" + album.getNumPhotos());

        var s = album.getEarliestDate();
        var e = album.getLatestDate();

        startDateLabel.setText(s == null ? "-" : s.toString().replace('T',' '));
        endDateLabel.setText(e == null ? "-" : e.toString().replace('T',' '));
    }

    /**
     * Handles selection of a photo from the ListView.
     * Updates preview to display the selected image.
     */
    @FXML
    private void handleSelectPhoto() {
        int i = photoList.getSelectionModel().getSelectedIndex();
        if (i >= 0) {
            index = i;
            loadPhoto(i);
        }
    }

    /**
     * Loads a specific photo into the preview area based on index.
     *
     * @param i index of the photo to load
     */
    private void loadPhoto(int i) {
        if (i < 0 || i >= album.getPhotos().size())
            return;

        Photo p = album.getPhotos().get(i);
        File f = new File(p.getFilePath());

        if (f.exists()) {
            photoView.setImage(new Image(f.toURI().toString()));
        } else {
            photoView.setImage(null);
        }

        captionField.setText(p.getCaption());
        tagList.getItems().setAll(p.getTags());
    }

    /**
     * Saves a caption edit for the currently selected photo.
     */
    @FXML
    private void handleSaveCaption() {
        if (index < 0) return;

        Photo p = album.getPhotos().get(index);
        p.setCaption(captionField.getText().trim());
        photoList.refresh();
    }

    /**
     * Opens a file chooser and allows the user to add a new photo.
     */
    @FXML
    private void handleAddPhoto() {
        try {
            Photo p = PhotoLoader.choosePhoto(photoList.getScene().getWindow());
            if (p == null) return;

            if (!album.addPhoto(p)) {
                showError("Photo already exists in album.");
                return;
            }

            refreshPhotoList();
            updateAlbumInfo();

        } catch (Exception e) {
            showError("Error adding photo.");
            e.printStackTrace();
        }
    }

    /**
     * Deletes the currently selected photo from the album.
     */
    @FXML
    private void handleDeletePhoto() {
        if (index < 0) return;

        Photo p = album.getPhotos().get(index);
        album.removePhoto(p);

        refreshPhotoList();
    }

    /**
     * Displays the previous photo as part of manual slideshow navigation.
     */
    @FXML
    private void handlePrev() {
        if (album.getPhotos().isEmpty()) return;

        index = (index - 1 + album.getPhotos().size()) % album.getPhotos().size();
        photoList.getSelectionModel().select(index);
        loadPhoto(index);
    }

    /**
     * Displays the next photo as part of manual slideshow navigation.
     */
    @FXML
    private void handleNext() {
        if (album.getPhotos().isEmpty()) return;

        index = (index + 1) % album.getPhotos().size();
        photoList.getSelectionModel().select(index);
        loadPhoto(index);
    }

    /**
     * Opens a TagDialog to create a new tag, then adds it to the selected photo.
     */
    @FXML
    private void handleAddTag() {
        if (index < 0) return;

        TagDialog dialog = new TagDialog();
        Tag result = dialog.showAndGet();

        if (result != null) {
            Photo p = album.getPhotos().get(index);

            if (!p.addTag(result.getName(), result.getValue())) {
                showError("Duplicate tag.");
                return;
            }

            tagList.getItems().setAll(p.getTags());
        }
    }

    /**
     * Removes the selected tag from the selected photo.
     */
    @FXML
    private void handleDeleteTag() {
        if (index < 0) return;

        Tag t = tagList.getSelectionModel().getSelectedItem();
        if (t == null) return;

        Photo p = album.getPhotos().get(index);
        p.removeTag(t.getName(), t.getValue());

        tagList.getItems().setAll(p.getTags());
    }

    /**
     * Initiates a copy operation for the selected photo.
     */
    @FXML
    private void handleCopyPhoto() {
        handleCopyOrMove(false);
    }

    /**
     * Initiates a move operation for the selected photo.
     */
    @FXML
    private void handleMovePhoto() {
        handleCopyOrMove(true);
    }

    /**
     * Shared logic for copying or moving a photo to another album.
     *
     * @param move true if moving, false if copying
     */
    private void handleCopyOrMove(boolean move) {
        if (index < 0) return;

        List<String> choices = new ArrayList<>();
        for (Album a : Photos.currentUser.getAlbums()) {
            if (!a.getName().equals(album.getName()))
                choices.add(a.getName());
        }

        if (choices.isEmpty()) {
            showError("No other albums available.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle(move ? "Move Photo" : "Copy Photo");
        dialog.setHeaderText(null);
        dialog.setContentText("Select target album:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String targetName = result.get();
        Album target = Photos.currentUser.getAlbum(targetName);

        Photo selected = album.getPhotos().get(index);

        if (!target.addPhoto(selected)) {
            showError("Photo already exists in target album.");
            return;
        }

        if (move) {
            album.removePhoto(selected);
            refreshPhotoList();
        }
    }

    /**
     * Returns to the album list screen.
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/AlbumsView.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) albumNameLabel).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Albums");
            stage.show();

        } catch (Exception e) {
            showError("Could not return to albums.");
            e.printStackTrace();
        }
    }

    /**
     * Displays an error alert dialog with the given message.
     *
     * @param msg message to display
     */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
