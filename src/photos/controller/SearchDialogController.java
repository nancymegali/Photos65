package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import photos.model.Photo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SearchDialogController {

    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;

    @FXML private TextField tagName1, tagValue1;
    @FXML private TextField tagName2, tagValue2;

    @FXML private RadioButton andRadio;
    @FXML private RadioButton orRadio;

    public List<Photo> processSearch() {
        List<Photo> output = new ArrayList<>();

        // Date search
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
            if (!n2.isEmpty() && !v2.isEmpty()) {
                if (andRadio.isSelected()) {
                    output.addAll(SearchUtils.searchTagAnd(n1, v1, n2, v2));
                } else if (orRadio.isSelected()) {
                    output.addAll(SearchUtils.searchTagOr(n1, v1, n2, v2));
                }
            } else {
                output.addAll(SearchUtils.searchTagSingle(n1, v1));
            }
        }

        return output;
    }
}
