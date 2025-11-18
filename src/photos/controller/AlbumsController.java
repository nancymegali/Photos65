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

public class AlbumsController {

    @FXML private ListView<String> albumList;
    @FXML private TextField newAlbumField;

    @FXML private Label countLabel;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;

    private User user;

    @FXML
    public void initialize() {
        user = Photos.currentUser;
        loadAlbums();
    }

    public void initializeList() {
        loadAlbums();
    }


  
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

    private void error(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
