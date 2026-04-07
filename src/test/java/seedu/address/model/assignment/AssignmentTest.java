package seedu.address.model.assignment;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class AssignmentTest {

    private static final Assignment QUIZ_1 = new Assignment(new AssignmentName("Quiz 1"),
            LocalDate.of(2026, 4, 5), 20);

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Assignment(null, LocalDate.of(2026, 4, 5), 20));
        assertThrows(NullPointerException.class, () -> new Assignment(new AssignmentName("Quiz 1"), null, 20));
    }

    @Test
    public void constructor_invalidMaxMarks_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Assignment(new AssignmentName("Quiz 1"), LocalDate.of(2026, 4, 5), 0));
    }

    @Test
    public void isValidMaxMarks() {
        assertFalse(Assignment.isValidMaxMarks(0));
        assertFalse(Assignment.isValidMaxMarks(-1));
        assertTrue(Assignment.isValidMaxMarks(1));
        assertTrue(Assignment.isValidMaxMarks(100));
    }

    @Test
    public void isSameAssignment() {
        Assignment sameIdentityDifferentFields = new Assignment(new AssignmentName("quiz 1"),
                LocalDate.of(2026, 4, 12), 25);

        assertTrue(QUIZ_1.isSameAssignment(QUIZ_1));
        assertTrue(QUIZ_1.isSameAssignment(sameIdentityDifferentFields));
        assertFalse(QUIZ_1.isSameAssignment(null));
        assertFalse(QUIZ_1.isSameAssignment(new Assignment(new AssignmentName("Quiz 2"),
                LocalDate.of(2026, 4, 5), 20)));
    }

    @Test
    public void equals() {
        Assignment sameAssignment = new Assignment(new AssignmentName("quiz 1"),
                LocalDate.of(2026, 4, 5), 20);
        Assignment differentDate = new Assignment(new AssignmentName("Quiz 1"),
                LocalDate.of(2026, 4, 12), 20);

        assertTrue(QUIZ_1.equals(QUIZ_1));
        assertTrue(QUIZ_1.equals(sameAssignment));
        assertFalse(QUIZ_1.equals(null));
        assertFalse(QUIZ_1.equals(5));
        assertFalse(QUIZ_1.equals(differentDate));
    }
}
