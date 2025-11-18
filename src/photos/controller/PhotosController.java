package photos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import photos.Photos;
import photos.model.*;
import photos.util.PhotoLoader;

import java.io.File;
import java.util.*;

public class PhotosController {

    @FXML private Label albumNameLabel;

    @FXML private ListView<Photo> photoList;
    @FXML private ImageView photoView;

    @FXML private TextField captionField;
    @FXML private ListView<Tag> tagList;

    @FXML private Label countLabel;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;

    private Album album;
    private int index = -1;

    /** Called by AlbumsController after loading the view */
    public void setAlbum(Album album) {
        this.album = album;
        albumNameLabel.setText(album.getName());

        refreshPhotoList();
    }

    /** Reload ListView contents */
    private void refreshPhotoList() {
        photoList.getItems().setAll(album.getPhotos());

        updateAlbumInfo();

        if (!album.getPhotos().isEmpty()) {
            index = 0;
            photoList.getSelectionModel().select(0);
            loadPhoto(0);
        } else {
            index = -1;
            clearPhotoDisplay();
        }
    }

    /** Clears preview area when album empty */
    private void clearPhotoDisplay() {
        photoView.setImage(null);
        captionField.setText("");
        tagList.getItems().clear();
    }

    /** Updates right side album summary */
    private void updateAlbumInfo() {
        countLabel.setText("" + album.getNumPhotos());

        var s = album.getEarliestDate();
        var e = album.getLatestDate();

        startDateLabel.setText(s == null ? "-" : s.toString().replace('T',' '));
        endDateLabel.setText(e == null ? "-" : e.toString().replace('T',' '));
    }

    /** Loads selected photo into the large ImageView */
    @FXML
    private void handleSelectPhoto() {
        int i = photoList.getSelectionModel().getSelectedIndex();
        if (i >= 0) {
            index = i;
            loadPhoto(i);
        }
    }

    /** Load photo by index */
    private void loadPhoto(int i) {
        if (i < 0 || i >= album.getPhotos().size())
            return;

        Photo p = album.getPhotos().get(i);

        File f = new File(p.getFilePath());
        if (f.exists()) {
            photoView.setImage(new Image(f.toURI().toString()));
        } else {
            photoView.setImage(null);
        }

        captionField.setText(p.getCaption());
        tagList.getItems().setAll(p.getTags());
    }

    /** Save caption */
    @FXML
    private void handleSaveCaption() {
        if (index < 0) return;
        Photo p = album.getPhotos().get(index);

        p.setCaption(captionField.getText().trim());
        photoList.refresh();
    }

    /** Add photo (using file chooser) */
    @FXML
    private void handleAddPhoto() {
        try {
            Photo p = PhotoLoader.choosePhoto(photoList.getScene().getWindow());

            if (p == null) return;

            if (!album.addPhoto(p)) {
                showError("Photo already exists in album.");
                return;
            }

            refreshPhotoList();
            updateAlbumInfo();

        } catch (Exception e) {
            showError("Error adding photo.");
            e.printStackTrace();
        }
    }

    /** Delete selected photo */
    @FXML
    private void handleDeletePhoto() {
        if (index < 0) return;

        Photo p = album.getPhotos().get(index);
        album.removePhoto(p);

        refreshPhotoList();
    }

    /** Slideshow Previous */
    @FXML
    private void handlePrev() {
        if (album.getPhotos().isEmpty()) return;

        index = (index - 1 + album.getPhotos().size()) % album.getPhotos().size();
        photoList.getSelectionModel().select(index);
        loadPhoto(index);
    }

    /** Slideshow Next */
    @FXML
    private void handleNext() {
        if (album.getPhotos().isEmpty()) return;

        index = (index + 1) % album.getPhotos().size();
        photoList.getSelectionModel().select(index);
        loadPhoto(index);
    }

    /** ADD tag */
    @FXML
    private void handleAddTag() {
        if (index < 0) return;

        var dialog = new TagDialog();
        Tag result = dialog.showAndGet();

        if (result != null) {
            Photo p = album.getPhotos().get(index);
            if (!p.addTag(result.getName(), result.getValue())) {
                showError("Duplicate tag.");
                return;
            }
            tagList.getItems().setAll(p.getTags());
        }
    }

    /** DELETE tag */
    @FXML
    private void handleDeleteTag() {
        if (index < 0) return;

        Tag t = tagList.getSelectionModel().getSelectedItem();
        if (t == null) return;

        Photo p = album.getPhotos().get(index);
        p.removeTag(t.getName(), t.getValue());

        tagList.getItems().setAll(p.getTags());
    }

    /** Copy Photo to another album */
    @FXML
    private void handleCopyPhoto() {
        handleCopyOrMove(false);
    }

    /** Move Photo to another album */
    @FXML
    private void handleMovePhoto() {
        handleCopyOrMove(true);
    }

    private void handleCopyOrMove(boolean move) {
        if (index < 0) return;

        List<String> choices = new ArrayList<>();
        for (Album a : Photos.currentUser.getAlbums()) {
            if (!a.getName().equals(album.getName()))
                choices.add(a.getName());
        }

        if (choices.isEmpty()) {
            showError("No other albums available.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle(move ? "Move Photo" : "Copy Photo");
        dialog.setHeaderText(null);
        dialog.setContentText("Select target album:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String targetName = result.get();
        Album target = Photos.currentUser.getAlbum(targetName);

        Photo selected = album.getPhotos().get(index);

        if (!target.addPhoto(selected)) {
            showError("Photo already exists in target album.");
            return;
        }

        if (move) {
            album.removePhoto(selected);
            refreshPhotoList();
        }
    }

    /** Return to album list screen */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/AlbumsView.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((Node)albumNameLabel).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Albums");
            stage.show();

        } catch (Exception e) {
            showError("Could not return to albums.");
            e.printStackTrace();
        }
    }

    /** Helper error dialog */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
