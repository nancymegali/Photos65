package photos.controller;

import photos.Photos;
import photos.model.Photo;
import photos.model.Album;
import photos.model.Tag;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class providing static helper methods for searching photos
 * across all albums belonging to the currently logged-in user.
 * <p>
 * Searches may be performed by date ranges or by tags, with support
 * for single-tag matching, AND-condition matching, or OR-condition matching.
 * </p>
 *
 * <p>
 * All search operations scan <i>every album</i> owned by
 * {@link Photos#currentUser} and collect matching photos without duplicates.
 * </p>
 *
 * @author 
 *     Nancy Megali & Safa Gokdemir
 */
public class SearchUtils {

    /**
     * Searches all photos belonging to the current user and returns
     * those whose timestamps fall within the specified date range (inclusive).
     *
     * @param start the earliest date to include
     * @param end   the latest date to include
     * @return a list of photos whose dates fall between {@code start} and {@code end}
     */
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

    /**
     * Searches for photos matching a single tag (name/value pair).
     *
     * @param name  the tag name to search for
     * @param value the tag value to search for
     * @return a list of photos containing the specified tag
     */
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

    /**
     * Searches for photos that contain <b>both</b> tag pairs (logical AND).
     *
     * @param n1 the first tag name
     * @param v1 the first tag value
     * @param n2 the second tag name
     * @param v2 the second tag value
     * @return a list of photos that contain both tags
     */
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

    /**
     * Searches for photos that contain <b>either</b> of two tag pairs (logical OR).
     *
     * @param n1 the first tag name
     * @param v1 the first tag value
     * @param n2 the second tag name
     * @param v2 the second tag value
     * @return a list of photos that contain at least one of the two tags
     */
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
