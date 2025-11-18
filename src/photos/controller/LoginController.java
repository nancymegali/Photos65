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

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin(javafx.event.ActionEvent event) {
        String username = usernameField.getText().trim().toLowerCase();

        if (username.isEmpty()) {
            showError("Username cannot be empty.");
            return;
        }

        if (username.equals("admin")) {
            Photos.currentUser = null; 
            loadAdminView(event);
            return;
        }

        if (username.equals("stock")) {

            User stock = Photos.store.getUser("stock");

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

        User u = Photos.store.getUser(username);

        if (u == null) {
            Photos.store.createUser(username);
            u = Photos.store.getUser(username);
        }

        Photos.currentUser = u;
        loadAlbumsView(event);
    }

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

    private void showError(String msg) {
        errorLabel.setText(msg);
    }
}
