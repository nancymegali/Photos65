package photos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

import photos.Photos;
import photos.model.User;

public class AdminController {

    @FXML private ListView<String> userList;
    @FXML private TextField newUserField;

    @FXML
    public void initialize() {
        refreshUsers();
    }

    private void refreshUsers() {
        userList.getItems().clear();

        for (User u : Photos.store.getUsers()) {
            userList.getItems().add(u.getUsername());
        }
    }

    @FXML
    private void handleCreateUser() {
        String name = newUserField.getText().trim().toLowerCase();

        if (name.isEmpty()) {
            showError("Username cannot be empty.");
            return;
        }

        if (name.equals("admin")) {
            showError("Cannot create a second 'admin' user.");
            return;
        }

        if (!Photos.store.createUser(name)) {
            showError("User already exists.");
            return;
        }

        newUserField.clear();
        refreshUsers();
    }

    @FXML
    private void handleDeleteUser() {
        String selected = userList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Select a user to delete.");
            return;
        }

        if (selected.equals("admin")) {
            showError("Cannot delete the admin account.");
            return;
        }

        if (selected.equals("stock")) {
            showError("Cannot delete the stock user.");
            return;
        }

        Photos.store.deleteUser(selected);
        refreshUsers();
    }

    @FXML
    private void handleLogout(javafx.event.ActionEvent event) {
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
