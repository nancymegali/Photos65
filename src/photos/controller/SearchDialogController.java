package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import photos.model.Photo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the advanced search dialog in the Photos application.
 * <p>
 * This dialog allows users to search for photos by:
 * <ul>
 *     <li>Date range</li>
 *     <li>Single tag</li>
 *     <li>Two tags using AND / OR conditions</li>
 * </ul>
 *
 * The controller collects user-selected search criteria and delegates the
 * actual search logic to {@code SearchUtils}. It returns a list of matching
 * {@link Photo} objects to the caller.
 * </p>
 *
 * <p>This controller is paired with {@code SearchDialog.fxml}.</p>
 *
 * @author 
 *     Nancy Megali & Safa Gokdemir
 *
 * @see SearchUtils
 * @see photos.controller.PhotosController
 */
public class SearchDialogController {

    /** Date picker for selecting the start of a date range filter. */
    @FXML private DatePicker startDate;

    /** Date picker for selecting the end of a date range filter. */
    @FXML private DatePicker endDate;

    /** Text field for the name of the first tag filter. */
    @FXML private TextField tagName1;

    /** Text field for the value of the first tag filter. */
    @FXML private TextField tagValue1;

    /** Text field for the name of the second tag filter (optional). */
    @FXML private TextField tagName2;

    /** Text field for the value of the second tag filter (optional). */
    @FXML private TextField tagValue2;

    /** Radio button indicating AND logic for dual-tag search. */
    @FXML private RadioButton andRadio;

    /** Radio button indicating OR logic for dual-tag search. */
    @FXML private RadioButton orRadio;

    /**
     * Processes the user's search inputs (date range and/or tag filters)
     * and returns a list of matching {@link Photo} objects.
     *
     * <p>
     * The logic works in this order:
     * <ol>
     *     <li>If both dates are selected → add all photos in that date range</li>
     *     <li>If tag 1 is provided → include photos with tag 1</li>
     *     <li>If tag 2 is also provided:
     *         <ul>
     *             <li>AND → only photos containing both tags</li>
     *             <li>OR → photos containing either tag</li>
     *         </ul>
     *     </li>
     * </ol>
     * </p>
     *
     * @return a list of {@link Photo} objects that match the search criteria
     */
    public List<Photo> processSearch() {
        List<Photo> output = new ArrayList<>();

        // Date range search
        LocalDate s = startDate.getValue();
        LocalDate e = endDate.getValue();
        if (s != null && e != null) {
            output.addAll(SearchUtils.searchByDateRange(s, e));
        }

        // Tag search
        String n1 = tagName1.getText().trim();
        String v1 = tagValue1.getText().trim();

        String n2 = tagName2.getText().trim();
        String v2 = tagValue2.getText().trim();

        if (!n1.isEmpty() && !v1.isEmpty()) {

            // Two tags provided
            if (!n2.isEmpty() && !v2.isEmpty()) {

                if (andRadio.isSelected()) {
                    output.addAll(SearchUtils.searchTagAnd(n1, v1, n2, v2));

                } else if (orRadio.isSelected()) {
                    output.addAll(SearchUtils.searchTagOr(n1, v1, n2, v2));
                }

            } else {
                // Only one tag
                output.addAll(SearchUtils.searchTagSingle(n1, v1));
            }
        }

        return output;
    }
}
