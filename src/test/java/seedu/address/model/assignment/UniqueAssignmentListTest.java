package seedu.address.model.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import seedu.address.model.assignment.exceptions.AssignmentNotFoundException;
import seedu.address.model.assignment.exceptions.DuplicateAssignmentException;

public class UniqueAssignmentListTest {

    private static final Assignment QUIZ_1 = new Assignment(new AssignmentName("Quiz 1"),
            LocalDate.of(2026, 4, 5), 20);
    private static final Assignment QUIZ_2 = new Assignment(new AssignmentName("Quiz 2"),
            LocalDate.of(2026, 4, 12), 25);

    private final UniqueAssignmentList uniqueAssignmentList = new UniqueAssignmentList();

    @Test
    public void contains_nullAssignment_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueAssignmentList.contains(null));
    }

    @Test
    public void contains_assignmentNotInList_returnsFalse() {
        assertFalse(uniqueAssignmentList.contains(QUIZ_1));
    }

    @Test
    public void contains_assignmentInList_returnsTrue() {
        uniqueAssignmentList.add(QUIZ_1);
        assertTrue(uniqueAssignmentList.contains(QUIZ_1));
    }

    @Test
    public void contains_assignmentWithSameIdentity_returnsTrue() {
        uniqueAssignmentList.add(QUIZ_1);
        Assignment sameIdentity = new Assignment(new AssignmentName("quiz 1"),
                LocalDate.of(2026, 4, 12), 30);
        assertTrue(uniqueAssignmentList.contains(sameIdentity));
    }

    @Test
    public void add_duplicateAssignment_throwsDuplicateAssignmentException() {
        uniqueAssignmentList.add(QUIZ_1);
        assertThrows(DuplicateAssignmentException.class, () ->
                uniqueAssignmentList.add(new Assignment(new AssignmentName("quiz 1"),
                        LocalDate.of(2026, 4, 12), 25)));
    }

    @Test
    public void findAssignmentByName_assignmentExists_returnsAssignment() {
        uniqueAssignmentList.add(QUIZ_1);
        assertEquals(QUIZ_1, uniqueAssignmentList.findAssignmentByName(new AssignmentName("Quiz 1")).orElseThrow());
    }

    @Test
    public void setAssignment_targetNotInList_throwsAssignmentNotFoundException() {
        assertThrows(AssignmentNotFoundException.class, () -> uniqueAssignmentList.setAssignment(QUIZ_1, QUIZ_2));
    }

    @Test
    public void setAssignment_editedAssignmentIsSameAssignment_success() {
        uniqueAssignmentList.add(QUIZ_1);
        uniqueAssignmentList.setAssignment(QUIZ_1, new Assignment(new AssignmentName("quiz 1"),
                LocalDate.of(2026, 4, 12), 25));

        UniqueAssignmentList expectedUniqueAssignmentList = new UniqueAssignmentList();
        expectedUniqueAssignmentList.add(new Assignment(new AssignmentName("quiz 1"),
                LocalDate.of(2026, 4, 12), 25));
        assertEquals(expectedUniqueAssignmentList, uniqueAssignmentList);
    }

    @Test
    public void setAssignment_editedAssignmentHasDifferentIdentity_success() {
        uniqueAssignmentList.add(QUIZ_1);
        uniqueAssignmentList.setAssignment(QUIZ_1, QUIZ_2);

        UniqueAssignmentList expectedUniqueAssignmentList = new UniqueAssignmentList();
        expectedUniqueAssignmentList.add(QUIZ_2);
        assertEquals(expectedUniqueAssignmentList, uniqueAssignmentList);
    }

    @Test
    public void setAssignment_editedAssignmentHasNonUniqueIdentity_throwsDuplicateAssignmentException() {
        uniqueAssignmentList.add(QUIZ_1);
        uniqueAssignmentList.add(QUIZ_2);

        assertThrows(DuplicateAssignmentException.class, () ->
                uniqueAssignmentList.setAssignment(QUIZ_1, new Assignment(new AssignmentName("quiz 2"),
                        LocalDate.of(2026, 4, 20), 30)));
    }

    @Test
    public void remove_assignmentDoesNotExist_throwsAssignmentNotFoundException() {
        assertThrows(AssignmentNotFoundException.class, () -> uniqueAssignmentList.remove(QUIZ_1));
    }

    @Test
    public void remove_existingAssignment_removesAssignment() {
        uniqueAssignmentList.add(QUIZ_1);
        uniqueAssignmentList.remove(QUIZ_1);

        assertEquals(new UniqueAssignmentList(), uniqueAssignmentList);
    }

    @Test
    public void setAssignments_list_replacesOwnListWithProvidedList() {
        uniqueAssignmentList.add(QUIZ_1);
        uniqueAssignmentList.setAssignments(Collections.singletonList(QUIZ_2));

        UniqueAssignmentList expectedUniqueAssignmentList = new UniqueAssignmentList();
        expectedUniqueAssignmentList.add(QUIZ_2);
        assertEquals(expectedUniqueAssignmentList, uniqueAssignmentList);
    }

    @Test
    public void setAssignments_listWithDuplicateAssignments_throwsDuplicateAssignmentException() {
        assertThrows(DuplicateAssignmentException.class, () ->
                uniqueAssignmentList.setAssignments(Arrays.asList(QUIZ_1,
                        new Assignment(new AssignmentName("quiz 1"), LocalDate.of(2026, 4, 12), 25))));
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        uniqueAssignmentList.add(QUIZ_1);
        assertThrows(UnsupportedOperationException.class, () ->
                uniqueAssignmentList.asUnmodifiableObservableList().remove(0));
    }
}
