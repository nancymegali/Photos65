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


    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim().toLowerCase();

        if (username.isEmpty()) {
            showError("Username cannot be empty.");
            return;
        }

        if (username.equals("admin")) {
            loadAdminView(event);
            return;
        }

  
        if (username.equals("stock")) {
            User stock = Photos.store.getUser("stock");
            if (stock == null) {
                stock = Photos.store.createUser("stock");
                stock.addAlbum("stock");

                PhotoLoader.loadStockPhotos(stock);
            }

            Photos.currentUser = stock;
            loadAlbumsView(event);
            return;
        }

        User u = Photos.store.getUser(username);

        if (u == null) {
            u = Photos.store.createUser(username);
        }

        Photos.currentUser = u;
        loadAlbumsView(event);
    }


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
        System.out.println("ERROR: " + message);
    }
}
