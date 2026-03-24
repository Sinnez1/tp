package seedu.address.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.classspace.Group;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

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
    @FXML
    private FlowPane assignments;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex, boolean showSessionDetails,
                      ClassSpaceName activeClassSpaceName, LocalDate activeSessionDate,
                      ObservableList<Group> groups) {
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
                .sorted(Comparator.comparing(classSpaceName -> classSpaceName.value,
                        String.CASE_INSENSITIVE_ORDER))
                .forEach(classSpaceName -> this.groups.getChildren().add(new Label(classSpaceName.value)));

        populateAssignments(activeClassSpaceName, groups);
    }

    private void populateAssignments(ClassSpaceName activeClassSpaceName, ObservableList<Group> groups) {
        boolean shouldShowAssignments = activeClassSpaceName != null;
        assignments.setManaged(shouldShowAssignments);
        assignments.setVisible(shouldShowAssignments);
        if (!shouldShowAssignments) {
            return;
        }

        Optional<Group> activeClassSpace = groups.stream()
                .filter(classSpace -> classSpace.getClassSpaceName().equals(activeClassSpaceName))
                .findFirst();
        if (activeClassSpace.isEmpty()) {
            return;
        }

        List<Assignment> assignmentList = new ArrayList<>(activeClassSpace.get().getAssignments().stream().toList());
        Collections.reverse(assignmentList);

        assignmentList.forEach(assignment -> assignments.getChildren().add(
                createAssignmentLabel(assignment, activeClassSpaceName)));
    }

    private Label createAssignmentLabel(Assignment assignment, ClassSpaceName activeClassSpaceName) {
        Optional<Integer> grade = person.getAssignmentGrade(activeClassSpaceName, assignment.getAssignmentName());
        String gradeText = grade.map(value -> value + "/" + assignment.getMaxMarks())
                .orElse("-");

        Label assignmentLabel = new Label(assignment.getAssignmentName().value + ": " + gradeText);
        assignmentLabel.getStyleClass().add("assignment-chip");

        if (grade.isPresent()) {
            assignmentLabel.getStyleClass().add("assignment-chip-graded");
        } else {
            assignmentLabel.getStyleClass().add("assignment-chip-ungraded");
        }

        return assignmentLabel;
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
