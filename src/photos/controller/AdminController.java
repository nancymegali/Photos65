package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

import photos.Photos;
import photos.model.User;

public class AdminController {

    @FXML
    private ListView<String> userList;

    @FXML
    private TextField newUserField;

    @FXML
    public void initialize() {
        refreshList();
    }

    /** Loads all users into the ListView */
    private void refreshList() {
        userList.getItems().clear();

        for (User u : Photos.store.getAllUsers()) {
            userList.getItems().add(u.getUsername());
        }
    }

    // ---------------------------
    // CREATE USER
    // ---------------------------
    @FXML
    private void handleCreateUser(ActionEvent event) {
        String username = newUserField.getText().trim().toLowerCase();

        if (username.isEmpty()) {
            showError("Username cannot be empty.");
            return;
        }

        if (username.equals("admin")) {
            showError("Cannot create admin user.");
            return;
        }

        if (Photos.store.getUser(username) != null) {
            showError("User already exists.");
            return;
        }

        Photos.store.createUser(username);
        newUserField.clear();
        refreshList();
    }

    // ---------------------------
    // DELETE USER
    // ---------------------------
    @FXML
    private void handleDeleteUser(ActionEvent event) {
        String selected = userList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Please select a user to delete.");
            return;
        }

        if (selected.equals("admin")) {
            showError("Cannot delete admin.");
            return;
        }

        Photos.store.deleteUser(selected);
        refreshList();
    }

    // ---------------------------
    // REFRESH
    // ---------------------------
    @FXML
    private void handleRefresh(ActionEvent event) {
        refreshList();
    }

    // ---------------------------
    // LOGOUT
    // ---------------------------
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
            showError("Unable to load login screen.");
            e.printStackTrace();
        }
    }

    // ---------------------------
    // UTILITY: SHOW ERROR
    // ---------------------------
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
