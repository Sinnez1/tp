package seedu.address.ui;

import java.time.LocalDate;
import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label matricNumber;
    @FXML
    private Label email;
    @FXML
    private Label attendance;
    @FXML
    private Label participation;
    @FXML
    private FlowPane tags;
    @FXML
    private FlowPane groups;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex, boolean showSessionDetails,
                      ClassSpaceName activeClassSpaceName, LocalDate activeSessionDate) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        matricNumber.setText(person.getMatricNumber().value);
        email.setText(person.getEmail().value);
        boolean canShowSessionDetails = showSessionDetails && activeClassSpaceName != null && activeSessionDate != null;
        if (canShowSessionDetails) {
            attendance.setText(formatAttendance(person, activeClassSpaceName, activeSessionDate));
            participation.setText("Participation: " + person.getParticipation(activeClassSpaceName, activeSessionDate));
        }
        attendance.setManaged(canShowSessionDetails);
        attendance.setVisible(canShowSessionDetails);
        participation.setManaged(canShowSessionDetails);
        participation.setVisible(canShowSessionDetails);
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
        person.getClassSpaces().stream()
                .sorted(Comparator.comparing(classSpaceName -> classSpaceName.value, String.CASE_INSENSITIVE_ORDER))
                .forEach(classSpaceName -> groups.getChildren().add(new Label(classSpaceName.value)));
    }

    private String formatAttendance(Person person, ClassSpaceName classSpaceName, LocalDate sessionDate) {
        Attendance sessionAttendance = person.getAttendance(classSpaceName, sessionDate);
        return switch (sessionAttendance.value) {
        case PRESENT -> "Attendance: [X] Present";
        case ABSENT -> "Attendance: [ ] Absent";
        case UNINITIALISED -> "Attendance: [-] Uninitialised";
        };
    }
}
