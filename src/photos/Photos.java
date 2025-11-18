package photos;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import photos.model.Persistence;
import photos.model.Store;
import photos.model.User;

public class Photos extends Application {

    public static Store store;

    public static User currentUser;

    @Override
    public void start(Stage stage) throws Exception {

        store = Persistence.loadStore();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/LoginView.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setTitle("Photos Login");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        Persistence.saveStore(store);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
