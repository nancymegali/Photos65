package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import photos.Photos;
import photos.model.Album;
import photos.model.User;

import java.time.format.DateTimeFormatter;

public class AlbumsController {

    @FXML
    private ListView<String> albumList;

    @FXML
    private TextField newAlbumField;

    @FXML
    private Label countLabel, startDateLabel, endDateLabel;

    private User user;

    @FXML
    public void initialize() {
        user = Photos.currentUser;
        refreshList();

        albumList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            updateAlbumInfo();
        });
    }


    @FXML
    private void handleRefresh(ActionEvent event) {
        refreshList();
    }

    private void refreshList() {
        albumList.getItems().clear();

        for (Album a : user.getAlbums()) {
            albumList.getItems().add(a.getName());
        }

        updateAlbumInfo();
    }


    @FXML
    private void handleCreateAlbum(ActionEvent event) {
        String name = newAlbumField.getText().trim();

        if (name.isEmpty()) {
            showError("Album name cannot be empty.");
            return;
        }

        if (!user.addAlbum(name)) {
            showError("An album with that name already exists.");
            return;
        }

        newAlbumField.clear();
        refreshList();
    }

    @FXML
    private void handleDeleteAlbum(ActionEvent event) {
        String name = albumList.getSelectionModel().getSelectedItem();

        if (name == null) {
            showError("Select an album to delete.");
            return;
        }

        user.deleteAlbum(name);
        refreshList();
    }

    @FXML
    private void handleRenameAlbum(ActionEvent event) {
        String oldName = albumList.getSelectionModel().getSelectedItem();

        if (oldName == null) {
            showError("Select an album to rename.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(oldName);
        dialog.setHeaderText("Enter new album name:");
        dialog.setContentText("Name:");

        dialog.showAndWait().ifPresent(newName -> {
            if (!user.renameAlbum(oldName, newName.trim())) {
                showError("Could not rename album. A duplicate name may exist.");
            }
        });

        refreshList();
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
    private void handleOpenAlbum(ActionEvent event) {
        String name = albumList.getSelectionModel().getSelectedItem();

        if (name == null) {
            showError("Select an album to open.");
            return;
        }

        Session.selectedAlbum = user.getAlbum(name);

        try {
            FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/photos/view/PhotosView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Album: " + name);
            stage.show();

        } catch (Exception e) {
            showError("Could not load album view.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/photos/view/LoginView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Photos Login");
            stage.show();
        } catch (Exception e) {
            showError("Logout failed.");
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
