package photos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import photos.model.Store;
import photos.model.User;

/**
 * Entry point for the Photos application.
 * <p>
 * This class initializes the application by loading the saved data from disk,
 * starting the JavaFX interface at the login screen, and ensuring data is
 * saved when the application exits. It also holds static references to the
 * {@link Store} object and the currently logged-in {@link User}, allowing
 * controllers throughout the program to access session-wide data.
 * </p>
 *
 * @author Nancy Megali & Safa Gokdemir
 */
public class Photos extends Application {

    /** 
     * Global Store instance that contains all users and persistent data.
     * Loaded once on application startup.
     */
    public static Store store;

    /**
     * The currently logged-in user. This is set by the LoginController
     * and accessed throughout the application.
     */
    public static User currentUser;

    /**
     * Initializes the application UI and loads the persistent store.
     *
     * @param stage the primary stage for the JavaFX application
     * @throws Exception if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws Exception {

        // Load serialized application data from disk
        store = Store.load();

        // Load login screen
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/photos/view/LoginView.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setTitle("Photos Login");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Called automatically when the application is shutting down.
     * Saves all data in the store to disk to preserve user changes.
     */
    @Override
    public void stop() {
        Store.save(store);
    }

    /**
     * Application entry point. Launches JavaFX.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
