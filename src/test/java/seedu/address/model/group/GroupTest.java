package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;

public class GroupTest {

    private static final GroupName T01 = new GroupName("T01");
    private static final Assignment QUIZ_1 = new Assignment(new AssignmentName("Quiz 1"),
            LocalDate.of(2026, 4, 5), 20);
    private static final Assignment QUIZ_2 = new Assignment(new AssignmentName("Quiz 2"),
            LocalDate.of(2026, 4, 12), 25);

    @Test
    public void constructor_withAssignments_storesAssignments() {
        Group group = new Group(T01, List.of(QUIZ_1, QUIZ_2));

        assertEquals(T01, group.getGroupName());
        assertEquals(List.of(QUIZ_1, QUIZ_2), group.getAssignments());
    }

    @Test
    public void hasAssignmentAndFindAssignmentByName() {
        Group group = new Group(T01, List.of(QUIZ_1));

        assertTrue(group.hasAssignment(new AssignmentName("Quiz 1")));
        assertTrue(group.findAssignmentByName(new AssignmentName("Quiz 1")).isPresent());
        assertFalse(group.hasAssignment(new AssignmentName("Quiz 2")));
        assertTrue(group.findAssignmentByName(new AssignmentName("Quiz 2")).isEmpty());
    }

    @Test
    public void isSameGroup() {
        Group group = new Group(T01, List.of(QUIZ_1));
        Group sameIdentityDifferentAssignments = new Group(new GroupName("t01"), List.of(QUIZ_2));

        assertTrue(group.isSameGroup(sameIdentityDifferentAssignments));
        assertTrue(group.isSameGroup(group));
        assertFalse(group.isSameGroup(null));
        assertFalse(group.isSameGroup(new Group(new GroupName("T02"), List.of(QUIZ_1))));
    }

    @Test
    public void equals() {
        Group group = new Group(T01, List.of(QUIZ_1));
        Group sameGroup = new Group(new GroupName("t01"), List.of(QUIZ_1));
        Group differentAssignments = new Group(T01, List.of(QUIZ_2));

        assertTrue(group.equals(group));
        assertTrue(group.equals(sameGroup));
        assertFalse(group.equals(null));
        assertFalse(group.equals(5));
        assertFalse(group.equals(differentAssignments));
    }
}
