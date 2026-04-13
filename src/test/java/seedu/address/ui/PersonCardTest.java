package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    @BeforeAll
    public static void initFxRuntime() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // JavaFX runtime already started.
        }
    }

    @Test
    public void constructor_assignmentLabels_formatWholeDecimalAndUngradedCorrectly() throws Exception {
        GroupName activeGroup = new GroupName("2026-S1-T01");

        Assignment decimalAssignment = new Assignment(
                new AssignmentName("Quiz Decimal"), LocalDate.of(2026, 5, 8), 10);
        Assignment wholeAssignment = new Assignment(
                new AssignmentName("Quiz Whole"), LocalDate.of(2026, 5, 9), 10);
        Assignment ungradedAssignment = new Assignment(
                new AssignmentName("Quiz Ungraded"), LocalDate.of(2026, 5, 10), 10);

        ObservableList<Group> groups = FXCollections.observableArrayList(
                new Group(activeGroup, List.of(decimalAssignment, wholeAssignment, ungradedAssignment)));

        Person person = new PersonBuilder()
                .withName("Alice")
                .withPhone("91234567")
                .withEmail("alice@example.com")
                .withMatricNumber("A1234567X")
                .withGroups("2026-S1-T01")
                .build();

        person = person.withUpdatedAssignmentGrade(activeGroup, decimalAssignment.getAssignmentName(), 8.5);
        person = person.withUpdatedAssignmentGrade(activeGroup, wholeAssignment.getAssignmentName(), 8.0);

        Person finalPerson = person;
        PersonCard card = runOnFxThread(() ->
                new PersonCard(finalPerson, 1, false, activeGroup, null, groups));

        FlowPane assignmentsPane = getPrivateField(card, "assignments", FlowPane.class);
        List<String> assignmentTexts = assignmentsPane.getChildren().stream()
                .map(node -> (Label) node)
                .map(Label::getText)
                .collect(Collectors.toList());

        assertTrue(assignmentTexts.contains("Quiz Decimal: 8.5/10"));
        assertTrue(assignmentTexts.contains("Quiz Whole: 8/10"));
        assertTrue(assignmentTexts.contains("Quiz Ungraded: -"));
    }

    private static <T> T runOnFxThread(FxSupplier<T> supplier) throws Exception {
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                result.set(supplier.get());
            } catch (Throwable t) {
                error.set(t);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        if (error.get() != null) {
            throw new RuntimeException(error.get());
        }
        return result.get();
    }

    private static <T> T getPrivateField(Object target, String fieldName, Class<T> type) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return type.cast(field.get(target));
    }

    @FunctionalInterface
    private interface FxSupplier<T> {
        T get() throws Exception;
    }
}
