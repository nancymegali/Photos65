package photos.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import photos.model.Tag;

/**
 * A helper class that displays the modal dialog used to create a new photo tag.
 * <p>
 * This class loads {@code TagDialog.fxml}, retrieves its controller, and
 * displays the dialog as a JavaFX {@link Dialog}. When the user confirms
 * their input, the dialog returns a {@link Tag} object built from the
 * entered name and value. If the user cancels the dialog, {@code null}
 * is returned.
 * </p>
 *
 * <p>
 * This dialog is used by {@link PhotosController} when adding tags to photos.
 * </p>
 *
 * @author
 *     Nancy Megali & Safa Gokdemir
 */
public class TagDialog {

    /**
     * Displays the tag creation dialog and returns a new {@link Tag}
     * if the user submits valid input.
     *
     * @return a constructed {@link Tag}, or {@code null} if the user cancelled
     */
    public Tag showAndGet() {
        try {
            FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/photos/view/TagDialog.fxml"));
            DialogPane pane = loader.load();

            TagDialogController controller = loader.getController();

            Dialog<Tag> dialog = new Dialog<>();
            dialog.setDialogPane(pane);

            var result = dialog.showAndWait();
            return result.isPresent() ? controller.buildTag() : null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
