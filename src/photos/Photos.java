package photos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import photos.model.Store;
import photos.model.User;

public class Photos extends Application {

    public static Store store;
    public static User currentUser;

    @Override
    public void start(Stage stage) throws Exception {

        store = Store.load();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/photos/view/LoginView.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setTitle("Photos Login");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        Store.save(store);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
