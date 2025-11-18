package photos.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import photos.Photos;
import photos.model.User;
import photos.model.Store;

public class LoginController {

    @FXML
    private TextField usernameField;

    /**
     * Handles login when the user clicks the Login button.
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim().toLowerCase();

        if (username.isEmpty()) {
            showError("Username cannot be empty.");
            return;
        }

        // ----------------------
        // ADMIN LOGIN
        // ----------------------
        if (username.equals("admin")) {
            loadAdminView(event);
            return;
        }

        // ----------------------
        // STOCK USER LOGIN
        // ----------------------
        if (username.equals("stock")) {
            User stock = Photos.store.getUser("stock");
            if (stock == null) {
                stock = Photos.store.createUser("stock");
                stock.addAlbum("stock");

                // load your stock photos from /data
                PhotoLoader.loadStockPhotos(stock);
            }

            Photos.currentUser = stock;
            loadAlbumsView(event);
            return;
        }

        // ----------------------
        // NORMAL USER LOGIN
        // ----------------------
        User u = Photos.store.getUser(username);

        if (u == null) {
            // Create user on first login (this is allowed per assignment)
            u = Photos.store.createUser(username);
        }

        Photos.currentUser = u;
        loadAlbumsView(event);
    }

    // ----------------------
    //     HELPER METHODS
    // ----------------------

    private void loadAdminView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/AdminView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Admin Subsystem");
            stage.show();
        } catch (Exception e) {
            showError("Could not load admin view.");
            e.printStackTrace();
        }
    }

    private void loadAlbumsView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/AlbumsView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Albums - " + Photos.currentUser.getUsername());
            stage.show();
        } catch (Exception e) {
            showError("Could not load albums view.");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        // You will replace this with a GUI dialog later
        System.out.println("ERROR: " + message);
    }
}
