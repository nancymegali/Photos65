package photos.controller;

import photos.Photos;
import photos.model.Photo;
import photos.model.Album;
import photos.model.Tag;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SearchUtils {

    public static List<Photo> searchByDateRange(LocalDate start, LocalDate end) {
        List<Photo> results = new ArrayList<>();

        for (Album a : Photos.currentUser.getAlbums()) {
            for (Photo p : a.getPhotos()) {
                LocalDate d = p.getDateTime().toLocalDate();
                if (!d.isBefore(start) && !d.isAfter(end)) {
                    if (!results.contains(p)) results.add(p);
                }
            }
        }
        return results;
    }

    public static List<Photo> searchTagSingle(String name, String value) {
        List<Photo> results = new ArrayList<>();

        for (Album a : Photos.currentUser.getAlbums()) {
            for (Photo p : a.getPhotos()) {
                if (p.getTags().contains(new Tag(name, value))) {
                    if (!results.contains(p)) results.add(p);
                }
            }
        }
        return results;
    }

    public static List<Photo> searchTagAnd(String n1, String v1, String n2, String v2) {
        List<Photo> results = new ArrayList<>();

        for (Album a : Photos.currentUser.getAlbums()) {
            for (Photo p : a.getPhotos()) {
                boolean first = p.getTags().contains(new Tag(n1, v1));
                boolean second = p.getTags().contains(new Tag(n2, v2));

                if (first && second && !results.contains(p)) {
                    results.add(p);
                }
            }
        }
        return results;
    }

    public static List<Photo> searchTagOr(String n1, String v1, String n2, String v2) {
        List<Photo> results = new ArrayList<>();

        for (Album a : Photos.currentUser.getAlbums()) {
            for (Photo p : a.getPhotos()) {
                boolean first = p.getTags().contains(new Tag(n1, v1));
                boolean second = p.getTags().contains(new Tag(n2, v2));

                if ((first || second) && !results.contains(p)) {
                    results.add(p);
                }
            }
        }
        return results;
    }
}
