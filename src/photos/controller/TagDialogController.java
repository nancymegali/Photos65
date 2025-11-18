package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import photos.model.Tag;

public class TagDialogController {

    @FXML private TextField nameField;
    @FXML private TextField valueField;

    public Tag getTag() {
        String name = nameField.getText().trim();
        String value = valueField.getText().trim();

        if (name.isEmpty() || value.isEmpty())
            return null;

        return new Tag(name, value);
    }
}
