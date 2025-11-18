package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.Stage;

import photos.Photos;
import photos.model.User;
import photos.model.Album;
import photos.util.PhotoLoader;

/**
 * Controller for the login screen of the Photos application.
 * <p>
 * This class handles all user login interactions, including:
 * <ul>
 *     <li>Authenticating regular users</li>
 *     <li>Logging in as the special {@code admin} account</li>
 *     <li>Initializing and loading the {@code stock} user</li>
 *     <li>Navigating to the Admin or Albums views</li>
 * </ul>
 * User accounts are created dynamically if they do not already exist.
 * </p>
 *
 * <p>This controller is loaded by {@code LoginView.fxml}.</p>
 *
 * @author Nancy Megali & Safa Gokdemir
 */
public class LoginController {

    /** Text field where the user enters their username. */
    @FXML private TextField usernameField;

    /** Label used to display login error messages. */
    @FXML private Label errorLabel;

    /**
     * Handles the login action when the "Login" button is pressed.
     * <p>
     * This method:
     * <ul>
     *     <li>Validates the username</li>
     *     <li>Routes the {@code admin} user to the admin panel</li>
     *     <li>Initializes the {@code stock} user and loads stock photos</li>
     *     <li>Loads or creates a normal user account</li>
     *     <li>Transitions into the albums screen</li>
     * </ul>
     *
     * @param event the action event triggered by the Login button
     */
    @FXML
    private void handleLogin(javafx.event.ActionEvent event) {
        String username = usernameField.getText().trim().toLowerCase();

        if (username.isEmpty()) {
            showError("Username cannot be empty.");
            return;
        }

        // Special admin account
        if (username.equals("admin")) {
            Photos.currentUser = null; 
            loadAdminView(event);
            return;
        }

        // Special stock account
        if (username.equals("stock")) {

            User stock = Photos.store.getUser("stock");

            // First-time initialization of stock user
            if (stock == null) {
                Photos.store.createUser("stock");
                stock = Photos.store.getUser("stock");

                stock.addAlbum("stock");
                PhotoLoader.loadStockPhotos(stock);
            }

            Photos.currentUser = stock;
            loadAlbumsView(event);
            return;
        }

        // Normal user login
        User u = Photos.store.getUser(username);

        // Create user if they do not exist
        if (u == null) {
            Photos.store.createUser(username);
            u = Photos.store.getUser(username);
        }

        Photos.currentUser = u;
        loadAlbumsView(event);
    }

    /**
     * Loads and displays the Admin UI.
     *
     * @param event the triggering action event
     */
    private void loadAdminView(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/photos/view/AdminView.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Admin Panel");
            stage.show();

        } catch (Exception e) {
            showError("Could not load admin view.");
        }
    }

    /**
     * Loads and displays the main Albums UI for the logged-in user.
     *
     * @param event the triggering action event
     */
    private void loadAlbumsView(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/photos/view/AlbumsView.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Albums");
            stage.show();

        } catch (Exception e) {
            showError("Could not load albums view.");
        }
    }

    /**
     * Displays an error message below the login field.
     *
     * @param msg the error text to display
     */
    private void showError(String msg) {
        errorLabel.setText(msg);
    }
}
