package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    private static final GroupName T01 = new GroupName("T01");

    @BeforeAll
    public static void setUpFxToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            latch.countDown();
        }
        latch.await();
    }

    @Test
    public void constructor_activeGroup_showsAssignmentsNewestFirstWithCorrectStyleClasses() throws Exception {
        Assignment quiz1 = new Assignment(new AssignmentName("Quiz 1"), LocalDate.of(2026, 4, 5), 20);
        Assignment quiz2 = new Assignment(new AssignmentName("Quiz 2"), LocalDate.of(2026, 4, 12), 25);
        ObservableList<Group> groups = FXCollections.observableArrayList(new Group(T01, List.of(quiz1, quiz2)));
        Person person = new PersonBuilder().withName("Alice")
                .withMatricNumber("A1234567X")
                .withEmail("alice@example.com")
                .withPhone("91234567")
                .withGroups("T01")
                .withAssignmentGrade("T01", "Quiz 2", 18)
                .build();

        PersonCard card = runOnFxThread(() -> new PersonCard(person, 1, false, T01, null, groups));
        FlowPane assignmentsPane = getPrivateField(card, "assignments", FlowPane.class);

        assertTrue(assignmentsPane.isManaged());
        assertTrue(assignmentsPane.isVisible());
        assertEquals(2, assignmentsPane.getChildren().size());

        Label firstChip = (Label) assignmentsPane.getChildren().get(0);
        Label secondChip = (Label) assignmentsPane.getChildren().get(1);

        assertEquals("Quiz 2: 18/25", firstChip.getText());
        assertTrue(firstChip.getStyleClass().contains("assignment-chip"));
        assertTrue(firstChip.getStyleClass().contains("assignment-chip-graded"));

        assertEquals("Quiz 1: -", secondChip.getText());
        assertTrue(secondChip.getStyleClass().contains("assignment-chip"));
        assertTrue(secondChip.getStyleClass().contains("assignment-chip-ungraded"));
    }

    @Test
    public void constructor_allStudentsView_hidesAssignmentsPane() throws Exception {
        Assignment quiz1 = new Assignment(new AssignmentName("Quiz 1"), LocalDate.of(2026, 4, 5), 20);
        ObservableList<Group> groups = FXCollections.observableArrayList(new Group(T01, List.of(quiz1)));
        Person person = new PersonBuilder().withName("Alice")
                .withMatricNumber("A1234567X")
                .withEmail("alice@example.com")
                .withPhone("91234567")
                .build();

        PersonCard card = runOnFxThread(() -> new PersonCard(person, 1, false, null, null, groups));
        FlowPane assignmentsPane = getPrivateField(card, "assignments", FlowPane.class);

        assertFalse(assignmentsPane.isManaged());
        assertFalse(assignmentsPane.isVisible());
        assertEquals(0, assignmentsPane.getChildren().size());
    }

    private static <T> T runOnFxThread(Callable<T> callable) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                result.set(callable.call());
            } catch (Throwable throwable) {
                error.set(throwable);
            } finally {
                latch.countDown();
            }
        });

        latch.await();

        if (error.get() != null) {
            throw new AssertionError(error.get());
        }
        return result.get();
    }

    private static <T extends Node> T getPrivateField(PersonCard card, String fieldName, Class<T> fieldType)
            throws Exception {
        Field field = PersonCard.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return fieldType.cast(field.get(card));
    }
}
