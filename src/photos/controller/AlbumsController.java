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
import photos.model.Photo;
import photos.model.User;
import photos.model.Store;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for the album management view in the Photos application.
 * <p>
 * This screen allows a logged-in user to manage their personal collection
 * of photo albums. Available functionality includes:
 * <ul>
 *     <li>Viewing all albums</li>
 *     <li>Creating new albums</li>
 *     <li>Deleting albums</li>
 *     <li>Renaming existing albums</li>
 *     <li>Opening an album to view its photos</li>
 *     <li>Viewing album metadata (photo count, earliest/latest dates)</li>
 *     <li>Searching across all albums</li>
 *     <li>Logging out</li>
 * </ul>
 *
 * This controller is bound to {@code AlbumsView.fxml} and works in
 * conjunction with {@link Photos#currentUser}, which identifies the
 * user currently logged in.
 * </p>
 *
 * @author Nancy Megali & Safa Gokdemir
 */
public class AlbumsController {

    /** Displays the list of album names owned by the logged-in user. */
    @FXML private ListView<String> albumList;

    /** Text field for entering the name of a new album. */
    @FXML private TextField newAlbumField;

    /** Label showing the number of photos in the selected album. */
    @FXML private Label countLabel;

    /** Label showing the earliest timestamp among photos in the selected album. */
    @FXML private Label startDateLabel;

    /** Label showing the latest timestamp among photos in the selected album. */
    @FXML private Label endDateLabel;

    /** The user currently logged in; set from {@link Photos#currentUser}. */
    private User user;

    /**
     * Called after FXML loading. Initializes state by loading the user's albums.
     */
    @FXML
    public void initialize() {
        user = Photos.currentUser;
        loadAlbums();
    }

    /**
     * Refreshes the album list. Called when navigating back
     * from the Photos view.
     */
    public void initializeList() {
        loadAlbums();
    }

    /**
     * Loads all of the user's albums into the ListView and installs
     * a listener to update album metadata based on selection.
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
     * Updates displayed album metadata (photo count and date range)
     * for the currently selected album. Displays hyphens when no album
     * is selected.
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

        startDateLabel.setText(a.getEarliestDate() != null
                ? a.getEarliestDate().format(fmt)
                : "-");

        endDateLabel.setText(a.getLatestDate() != null
                ? a.getLatestDate().format(fmt)
                : "-");
    }

    /**
     * Opens the search dialog, processes search criteria, and displays results.
     * Shows an error if no photos match.
     */
    @FXML
    private void handleSearch() {
        try {
            FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/photos/view/SearchDialog.fxml"));
            DialogPane pane = loader.load();

            SearchDialogController controller = loader.getController();

            Dialog<List<Photo>> dialog = new Dialog<>();
            dialog.setDialogPane(pane);

            var result = dialog.showAndWait();
            if (result.isEmpty()) return;

            List<Photo> photos = controller.processSearch();

            if (photos.isEmpty()) {
                error("No photos match the search criteria.");
                return;
            }

            showSearchResults(photos);

        } catch (Exception e) {
            error("Failed to open search dialog.");
            e.printStackTrace();
        }
    }

    /**
     * Displays the given list of photos in a dedicated search results window.
     *
     * @param photos list of photos returned by the search system
     */
    private void showSearchResults(List<Photo> photos) {
        try {
            FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/photos/view/SearchResultsView.fxml"));
            Scene scene = new Scene(loader.load());

            SearchResultsController controller = loader.getController();
            controller.setResults(photos);

            Stage stage = new Stage();
            stage.setTitle("Search Results");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            error("Could not display search results.");
            e.printStackTrace();
        }
    }

    /**
     * Creates a new album using the text in the input field.
     * Validates uniqueness and prevents empty names.
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
     * Deletes the currently selected album. Displays an error if none is selected.
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
     * Prompts the user for a new album name and applies the rename
     * if valid and not conflicting with another album.
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
     * Opens the selected album in the Photos view.
     *
     * @param event the event triggered by clicking the Open button
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
     * Logs out the current user, saves all data, and returns to the login screen.
     *
     * @param event logout button event
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
     * Shows an error message in a modal dialog.
     *
     * @param msg text to display
     */
    private void error(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
