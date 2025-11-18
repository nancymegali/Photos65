package photos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import photos.Photos;
import photos.model.Album;
import photos.model.User;
import photos.model.Store;

import java.time.format.DateTimeFormatter;

/**
 * Controller for the album management screen of the Photos application.
 * <p>
 * This controller allows a logged-in user to:
 * <ul>
 *     <li>View all albums</li>
 *     <li>Create new albums</li>
 *     <li>Delete existing albums</li>
 *     <li>Rename albums</li>
 *     <li>Open a selected album</li>
 *     <li>View album metadata (photo count, earliest/latest dates)</li>
 *     <li>Logout to the login screen</li>
 * </ul>
 * The controller reads user information from {@link Photos#currentUser}
 * and is associated with {@code AlbumsView.fxml}.
 * </p>
 *
 * @author Nancy Megali & Safa Gokdemir
 */
public class AlbumsController {

    /** ListView displaying all album names for the logged-in user. */
    @FXML private ListView<String> albumList;

    /** Text field used to enter a new album name when creating albums. */
    @FXML private TextField newAlbumField;

    /** Displays total number of photos in the selected album. */
    @FXML private Label countLabel;

    /** Displays the earliest photo timestamp in the selected album. */
    @FXML private Label startDateLabel;

    /** Displays the latest photo timestamp in the selected album. */
    @FXML private Label endDateLabel;

    /** The currently logged-in user, loaded from {@link Photos#currentUser}. */
    private User user;

    /**
     * Initializes the album screen after FXML loading.
     * Loads all albums belonging to the logged-in user.
     */
    @FXML
    public void initialize() {
        user = Photos.currentUser;
        loadAlbums();
    }

    /**
     * Re-initializes the album list when navigating back from another view.
     * Called by {@link photos.controller.PhotosController}.
     */
    public void initializeList() {
        loadAlbums();
    }

    /**
     * Loads all albums for the logged-in user into the ListView
     * and attaches listeners to update album metadata when selection changes.
     */
    private void loadAlbums() {
        albumList.getItems().clear();

        for (Album a : user.getAlbums()) {
            albumList.getItems().add(a.getName());
        }

        albumList.getSelectionModel()
                 .selectedItemProperty()
                 .addListener((obs, oldV, newV) -> updateAlbumInfo());

        updateAlbumInfo();
    }

    /**
     * Updates the album information panel (photo count and date range)
     * based on the currently selected album.
     */
    private void updateAlbumInfo() {
        String name = albumList.getSelectionModel().getSelectedItem();

        if (name == null) {
            countLabel.setText("-");
            startDateLabel.setText("-");
            endDateLabel.setText("-");
            return;
        }

        Album a = user.getAlbum(name);
        countLabel.setText(String.valueOf(a.getNumPhotos()));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (a.getEarliestDate() != null)
            startDateLabel.setText(a.getEarliestDate().format(fmt));
        else
            startDateLabel.setText("-");

        if (a.getLatestDate() != null)
            endDateLabel.setText(a.getLatestDate().format(fmt));
        else
            endDateLabel.setText("-");
    }

    /**
     * Creates a new album with the name entered in the text field.
     * Validates input and prevents duplicate album names.
     */
    @FXML
    private void handleCreateAlbum() {
        String name = newAlbumField.getText().trim();

        if (name.isEmpty()) {
            error("Album name cannot be empty.");
            return;
        }

        if (!user.addAlbum(name)) {
            error("Album already exists.");
            return;
        }

        newAlbumField.clear();
        loadAlbums();
        Store.save(Photos.store);
    }

    /**
     * Deletes the selected album from the user's collection.
     * Displays an error if no album is selected.
     */
    @FXML
    private void handleDeleteAlbum() {
        String selected = albumList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            error("Select an album to delete.");
            return;
        }

        user.deleteAlbum(selected);
        loadAlbums();
        Store.save(Photos.store);
    }

    /**
     * Renames the selected album using a dialog prompt.
     * Validates the new name and prevents duplicates.
     */
    @FXML
    private void handleRenameAlbum() {
        String oldName = albumList.getSelectionModel().getSelectedItem();

        if (oldName == null) {
            error("Select an album to rename.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(oldName);
        dialog.setHeaderText("Enter new album name:");
        dialog.setContentText("New name:");

        dialog.showAndWait().ifPresent(newName -> {
            newName = newName.trim();
            if (newName.isEmpty()) {
                error("Album name cannot be empty.");
            } else if (!user.renameAlbum(oldName, newName)) {
                error("Album with that name already exists.");
            }
        });

        loadAlbums();
        Store.save(Photos.store);
    }

    /**
     * Opens the selected album and loads the Photos view.
     *
     * @param event the button click event
     */
    @FXML
    private void handleOpenAlbum(ActionEvent event) {
        String name = albumList.getSelectionModel().getSelectedItem();

        if (name == null) {
            error("Select an album to open.");
            return;
        }

        Album selected = user.getAlbum(name);

        try {
            FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/photos/view/PhotosView.fxml"));
            Scene scene = new Scene(loader.load());

            PhotosController controller = loader.getController();
            controller.setAlbum(selected);

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Album: " + name);
            stage.show();

        } catch (Exception e) {
            error("Could not open album.");
            e.printStackTrace();
        }
    }

    /**
     * Logs the user out, saves data, and returns to the login screen.
     *
     * @param event the logout action
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        Store.save(Photos.store);
        Photos.currentUser = null;

        try {
            FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/photos/view/LoginView.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Photos Login");
            stage.show();

        } catch (Exception e) {
            error("Logout failed.");
            e.printStackTrace();
        }
    }

    /**
     * Shows an error dialog with the specified message.
     *
     * @param msg the error message to display
     */
    private void error(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
