package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import photos.model.Tag;

/**
 * Controller for the tag creation dialog used in the Photos application.
 * <p>
 * This controller is associated with {@code TagDialog.fxml} and provides
 * access to the text fields where the user enters a tag name and value.
 * It is used internally by {@link TagDialog} to construct a new
 * {@link Tag} object when the user confirms the dialog.
 * </p>
 *
 * <p>The controller validates input and only returns a tag if both fields
 * contain non-empty text.</p>
 *
 * @see TagDialog
 * @see photos.model.Tag
 * @see photos.controller.PhotosController
 * 
 * author Nancy Megali & Safa Gokdemir
 */
public class TagDialogController {

    /** Text field where the user enters the tag's name. */
    @FXML private TextField nameField;

    /** Text field where the user enters the tag's value. */
    @FXML private TextField valueField;

    /**
     * Builds and returns a new {@link Tag} using the user-entered name and value.
     * <p>
     * Returns {@code null} if either field is empty, allowing the caller to
     * handle invalid or cancelled input appropriately.
     * </p>
     *
     * @return a new {@link Tag}, or {@code null} if input was incomplete
     */
    public Tag buildTag() {
        String name = nameField.getText().trim();
        String value = valueField.getText().trim();

        if (name.isEmpty() || value.isEmpty()) {
            return null;
        }

        return new Tag(name, value);
    }
}
