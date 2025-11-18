package photos.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import photos.model.Tag;

public class TagDialog {

    public Tag showAndGet() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/TagDialog.fxml"));
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
