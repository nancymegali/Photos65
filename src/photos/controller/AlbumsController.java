package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class AlbumsController {
    private photos.model.User user;
    private photos.model.Store store;

    public void setUser(photos.model.User user) {
        this.user = user;
    }

    public void setStore(photos.model.Store store) {
        this.store = store;
    }

    @FXML
    private void handleLogout(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/LoginView.fxml"));
            Parent root = loader.load();

            // Get the current stage from the button that triggered the event
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Photos Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
