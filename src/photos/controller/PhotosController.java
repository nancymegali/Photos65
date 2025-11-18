package photos.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import photos.Photos;
import photos.model.*;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PhotosController {

    @FXML private TilePane thumbnailPane;
    @FXML private ImageView previewImage;
    @FXML private Label captionLabel;
    @FXML private Label dateLabel;
    @FXML private Label albumNameLabel;
    @FXML private ListView<String> tagList;

    private Album album;
    private List<Photo> photos;
    private int currentIndex = -1;

    @FXML
    public void initialize() {
        album = Session.selectedAlbum;
        albumNameLabel.setText(album.getName());

        photos = album.getPhotos();

        loadThumbnails();

        if (!photos.isEmpty()) {
            selectPhoto(0);
        }
    }


    private void loadThumbnails() {
        thumbnailPane.getChildren().clear();

        for (int i = 0; i < photos.size(); i++) {
            Photo p = photos.get(i);
            ImageView thumb = makeThumbnail(p, i);
            thumbnailPane.getChildren().add(thumb);
        }
    }

    private ImageView makeThumbnail(Photo p, int index) {
        Image img = new Image(new File(p.getFilePath()).toURI().toString(), 120, 0, true, true);

        ImageView thumb = new ImageView(img);
        thumb.setFitWidth(120);
        thumb.setPreserveRatio(true);

        thumb.setOnMouseClicked(e -> selectPhoto(index));

        return thumb;
    }

 
    private void selectPhoto(int index) {
        currentIndex = index;

        Photo p = photos.get(index);

        previewImage.setImage(new Image(new File(p.getFilePath()).toURI().toString()));

        captionLabel.setText("Caption: " + p.getCaption());

        dateLabel.setText("Date: " +
                p.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        tagList.getItems().clear();
        for (Tag t : p.getTags()) {
            tagList.getItems().add(t.toString());
        }
    }

    @FXML
    private void handleNext() {
        if (photos.isEmpty()) return;

        currentIndex = (currentIndex + 1) % photos.size();
        selectPhoto(currentIndex);
    }

    @FXML
    private void handlePrev() {
        if (photos.isEmpty()) return;

        currentIndex = (currentIndex - 1 + photos.size()) % photos.size();
        selectPhoto(currentIndex);
    }


    @FXML
    private void handleAddPhoto() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Image");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png"));

        File file = fc.showOpenDialog(null);

        if (file == null) return;

        Photo newPhoto = new Photo(file.getAbsolutePath());

        if (!album.addPhoto(newPhoto)) {
            showError("Photo already exists in this album.");
            return;
        }

        loadThumbnails();
        photos = album.getPhotos();
        selectPhoto(photos.size() - 1);
    }

    @FXML
    private void handleRemovePhoto() {
        if (currentIndex < 0) return;

        album.removePhoto(photos.get(currentIndex));
        photos = album.getPhotos();

        loadThumbnails();

        if (!photos.isEmpty()) selectPhoto(0);
        else clearPreview();
    }

    private void clearPreview() {
        previewImage.setImage(null);
        captionLabel.setText("");
        dateLabel.setText("");
        tagList.getItems().clear();
        currentIndex = -1;
    }


    @FXML
    private void handleCaption() {
        if (currentIndex < 0) return;

        TextInputDialog dialog = new TextInputDialog(photos.get(currentIndex).getCaption());
        dialog.setHeaderText("Enter new caption:");
        dialog.showAndWait().ifPresent(text -> {
            photos.get(currentIndex).setCaption(text);
            selectPhoto(currentIndex);
        });
    }

 
    @FXML
    private void handleAddTag() {
        if (currentIndex < 0) return;

        TagDialog dialog = new TagDialog();
        Tag result = dialog.showAndGet();

        if (result != null) {
            Photo p = photos.get(currentIndex);
            if (!p.addTag(result.getName(), result.getValue())) {
                showError("Duplicate tag.");
            }
            selectPhoto(currentIndex);
        }
    }

    @FXML
    private void handleDeleteTag() {
        if (currentIndex < 0) return;
        String selected = tagList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String[] parts = selected.split("=");
        photos.get(currentIndex).removeTag(parts[0], parts[1]);
        selectPhoto(currentIndex);
    }

 
    @FXML
    private void handleCopyPhoto() {
        handlePhotoTransfer(false); 
    }

    @FXML
    private void handleMovePhoto() {
        handlePhotoTransfer(true); 
    }

    private void handlePhotoTransfer(boolean move) {
        if (currentIndex < 0) return;

        List<String> albumNames = new ArrayList<>();
        for (Album a : Photos.currentUser.getAlbums()) {
            if (!a.getName().equals(album.getName()))
                albumNames.add(a.getName());
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(albumNames.get(0), albumNames);
        dialog.setHeaderText(move ? "Move photo to:" : "Copy photo to:");

        dialog.showAndWait().ifPresent(targetName -> {
            Album target = Photos.currentUser.getAlbum(targetName);
            Photo selected = photos.get(currentIndex);

            if (move) {
                album.removePhoto(selected);
                target.addPhoto(selected);
                photos = album.getPhotos();
                loadThumbnails();
                if (!photos.isEmpty()) selectPhoto(0);
                else clearPreview();
            } else {
                target.addPhoto(selected);
            }
        });
    }


    @FXML
    private void handleSearch() {
        try {
            FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/photos/view/SearchDialog.fxml"));
            DialogPane pane = loader.load();

            SearchDialogController controller = loader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.setTitle("Search Photos");

            var result = dialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {

                List<Photo> photos = controller.processSearch();

                if (photos != null && !photos.isEmpty()) {
                    createAlbumFromResults(photos);
                }
            }

        } catch (Exception e) {
            showError("Search failed.");
            e.printStackTrace();
        }
    }


    private void createAlbumFromResults(List<Photo> results) {
        TextInputDialog dialog = new TextInputDialog("SearchResults");
        dialog.setHeaderText("Enter name for new album:");
        dialog.showAndWait().ifPresent(name -> {
            if (!Photos.currentUser.addAlbum(name)) {
                showError("Album name already exists.");
                return;
            }

            Album newAlbum = Photos.currentUser.getAlbum(name);
            for (Photo p : results) newAlbum.addPhoto(p);
        });
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/AlbumsView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Albums");
            stage.show();
        } catch (Exception e) {
            showError("Could not return to albums.");
            e.printStackTrace();
        }
    }


    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
