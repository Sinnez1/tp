package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.exceptions.DuplicateGroupException;
import seedu.address.model.group.exceptions.GroupNotFoundException;

public class UniqueGroupListTest {

    private static final Group T01 = new Group(new GroupName("T01"),
            List.of(new Assignment(new AssignmentName("Quiz 1"), LocalDate.of(2026, 4, 5), 20)));
    private static final Group T02 = new Group(new GroupName("T02"));

    private final UniqueGroupList uniqueGroupList = new UniqueGroupList();

    @Test
    public void contains_nullGroup_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueGroupList.contains(null));
    }

    @Test
    public void contains_groupNotInList_returnsFalse() {
        assertFalse(uniqueGroupList.contains(T01));
    }

    @Test
    public void contains_groupInList_returnsTrue() {
        uniqueGroupList.add(T01);
        assertTrue(uniqueGroupList.contains(T01));
    }

    @Test
    public void contains_groupWithSameIdentity_returnsTrue() {
        uniqueGroupList.add(T01);
        Group sameIdentity = new Group(new GroupName("t01"));
        assertTrue(uniqueGroupList.contains(sameIdentity));
    }

    @Test
    public void add_duplicateGroup_throwsDuplicateGroupException() {
        uniqueGroupList.add(T01);
        assertThrows(DuplicateGroupException.class, () -> uniqueGroupList.add(new Group(new GroupName("t01"))));
    }

    @Test
    public void setGroup_targetNotInList_throwsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () -> uniqueGroupList.setGroup(T01, T02));
    }

    @Test
    public void setGroup_editedGroupIsSameGroup_success() {
        uniqueGroupList.add(T01);
        uniqueGroupList.setGroup(T01, new Group(new GroupName("t01")));

        UniqueGroupList expectedUniqueGroupList = new UniqueGroupList();
        expectedUniqueGroupList.add(new Group(new GroupName("t01")));
        assertEquals(expectedUniqueGroupList, uniqueGroupList);
    }

    @Test
    public void setGroup_editedGroupHasDifferentIdentity_success() {
        uniqueGroupList.add(T01);
        uniqueGroupList.setGroup(T01, T02);

        UniqueGroupList expectedUniqueGroupList = new UniqueGroupList();
        expectedUniqueGroupList.add(T02);
        assertEquals(expectedUniqueGroupList, uniqueGroupList);
    }

    @Test
    public void setGroup_editedGroupHasNonUniqueIdentity_throwsDuplicateGroupException() {
        uniqueGroupList.add(T01);
        uniqueGroupList.add(T02);

        assertThrows(DuplicateGroupException.class, () ->
                uniqueGroupList.setGroup(T01, new Group(new GroupName("t02"))));
    }

    @Test
    public void remove_groupDoesNotExist_throwsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () -> uniqueGroupList.remove(T01));
    }

    @Test
    public void remove_existingGroup_removesGroup() {
        uniqueGroupList.add(T01);
        uniqueGroupList.remove(T01);

        assertEquals(new UniqueGroupList(), uniqueGroupList);
    }

    @Test
    public void setGroups_list_replacesOwnListWithProvidedList() {
        uniqueGroupList.add(T01);
        uniqueGroupList.setGroups(Collections.singletonList(T02));

        UniqueGroupList expectedUniqueGroupList = new UniqueGroupList();
        expectedUniqueGroupList.add(T02);
        assertEquals(expectedUniqueGroupList, uniqueGroupList);
    }

    @Test
    public void setGroups_listWithDuplicateGroups_throwsDuplicateGroupException() {
        assertThrows(DuplicateGroupException.class, () ->
                uniqueGroupList.setGroups(Arrays.asList(T01, new Group(new GroupName("t01")))));
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        uniqueGroupList.add(T01);
        assertThrows(UnsupportedOperationException.class, () ->
                uniqueGroupList.asUnmodifiableObservableList().remove(0));
    }
}
