package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import photos.model.*;

import java.nio.file.*;
import java.util.*;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private Label errorLabel;

    private Map<String, User> users;
    private Store store;

    @FXML
    public void initialize() {
        try {
            store = new Store(Paths.get("data-store"));
            users = store.loadAllUsers();

            if (!users.containsKey("stock")) {
                User stock = new User("stock");
                stock.createAlbum("stock");
                store.saveUser(stock);
                users.put("stock", stock);
            }
        } catch (Exception e) {
            errorLabel.setText("Error loading users.");
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim().toLowerCase();
        if (username.isEmpty()) {
            errorLabel.setText("Please enter a username.");
            return;
        }

        if (username.equals("admin")) {
            // Load AdminView
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/photos/view/AdminView.fxml"));
                javafx.scene.Parent root = loader.load();
                javafx.stage.Stage stage = (javafx.stage.Stage) usernameField.getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(root));
                stage.setTitle("Admin Dashboard");
                stage.show();
            } catch (Exception e) {
                errorLabel.setText("Error loading Admin view.");
                e.printStackTrace();
            }
            return;
        }

        User user = users.get(username);
        if (user == null) {
            errorLabel.setText("User does not exist.");
            return;
        }

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/photos/view/AlbumsView.fxml"));
            javafx.scene.Parent root = loader.load();

            // Pass user + store data to AlbumsController
            photos.controller.AlbumsController albumsController = loader.getController();
            albumsController.setUser(user);
            albumsController.setStore(store);

            javafx.stage.Stage stage = (javafx.stage.Stage) usernameField.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Albums - " + username);
            stage.show();
        } catch (Exception e) {
            errorLabel.setText("Error loading albums view.");
            e.printStackTrace();
        }
    }

}
