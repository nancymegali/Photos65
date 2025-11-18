package photos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

import photos.Photos;
import photos.model.User;

/**
 * Controller for the administrator subsystem of the Photos application.
 * <p>
 * The admin interface allows the special {@code admin} user to:
 * <ul>
 *     <li>View all existing users</li>
 *     <li>Create new users</li>
 *     <li>Delete users (except {@code admin} and {@code stock})</li>
 *     <li>Return to the login screen</li>
 * </ul>
 * This controller is associated with {@code AdminView.fxml}.
 * </p>
 *
 * <p>
 * All user account changes are immediately written to the in-memory
 * {@link photos.model.Store} managed by {@link Photos}.
 * </p>
 *
 * @author Nancy Megali & Safa Gokdemir
 */
public class AdminController {

    /** ListView displaying all usernames in the system. */
    @FXML private ListView<String> userList;

    /** TextField for entering a new username to create. */
    @FXML private TextField newUserField;

    /**
     * Initializes the admin screen by populating the list of users.
     * Called automatically after FXML loading.
     */
    @FXML
    public void initialize() {
        refreshUsers();
    }

    /**
     * Refreshes the contents of the user list by reading the current
     * users stored in the global {@link Photos#store}.
     */
    private void refreshUsers() {
        userList.getItems().clear();

        for (User u : Photos.store.getUsers()) {
            userList.getItems().add(u.getUsername());
        }
    }

    /**
     * Handles creation of a new user when the "Create User" button is pressed.
     * <p>
     * This method:
     * <ul>
     *     <li>Validates username input</li>
     *     <li>Prevents creating the reserved {@code admin} user</li>
     *     <li>Ensures no duplicate usernames exist</li>
     *     <li>Updates the displayed user list</li>
     * </ul>
     */
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

    /**
     * Deletes the selected user when the "Delete" button is pressed.
     * <p>
     * Prevents deletion of special users:
     * <ul>
     *     <li>{@code admin}</li>
     *     <li>{@code stock}</li>
     * </ul>
     */
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

    /**
     * Logs the admin user out and returns to the login screen.
     *
     * @param event the action event that triggered the logout
     */
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

    /**
     * Displays an error dialog containing the specified message.
     *
     * @param msg the message to display
     */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
