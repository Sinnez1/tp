package seedu.address.storage;

import java.util.Map;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;

/**
 * Validates a {@code Person} against the AddressBook during JSON loading.
 */
public class PersonLoadValidator {

    private static final String GROUP_DOES_NOT_EXIST =
            "Contact references group '%s' which does not exist yet.";

    private static final String HAS_SESSION_BUT_NOT_IN_GROUP =
            "Contact has sessions for group '%s' but is not a member of it.";
    private static final String GRADE_EXCEEDS_MAX_MARKS =
            "Grade %d for assignment '%s' in group '%s' exceeds max marks of %d.";

    private static final String ASSIGNMENT_DOES_NOT_EXIST =
            "Contact has a grade for assignment '%s' in group '%s', but that assignment does not exist.";

    private static final String HAS_GRADES_BUT_NOT_IN_GROUP =
            "Contact has grades for group '%s' but is not a member of it.";

    private PersonLoadValidator() {}

    /**
     * Validates that every group referenced in the person's group list exists in the address book.
     *
     * @throws IllegalValueException if any referenced group does not exist.
     */
    public static void validateGroupsExist(AddressBook addressBook, Person person) throws IllegalValueException {
        for (GroupName groupName : person.getGroups()) {
            Group group = new Group(groupName);
            if (!addressBook.hasGroup(group)) {
                throw new IllegalValueException(String.format(GROUP_DOES_NOT_EXIST, groupName.value));
            }
        }
    }

    /**
     * Validates assignment grades stored on the person against the groups and assignments defined
     * in the address book.
     *
     * @throws IllegalValueException if any grade is inconsistent with the group's assignment definitions.
     */
    public static void validateAssignmentGrades(AddressBook addressBook, Person person) throws IllegalValueException {
        for (var groupEntry : person.getAssignmentGrades().entrySet()) {
            GroupName groupName = groupEntry.getKey();
            Map<AssignmentName, Integer> grades = groupEntry.getValue();

            validatePersonIsMemberOfGroup(person, groupName);
            /*
            This should never be reached as validateGroupsExist guarantees the group
            exists before this method is called. The orElseThrow is a defensive guard against
            future errors in the load sequence.
             */
            Group group = addressBook.getGroupList().stream()
                    .filter(cs -> cs.getGroupName().equals(groupName))
                    .findFirst()
                    .orElseThrow(() ->
                            new AssertionError("Group '" + groupName.value
                                    + "' should exist after validateGroupsExist"));
            validateAssignmentAgainstGroup(group, groupName, grades);
        }
    }

    /**
     * Validates that every group for which the person has session data is also in the person's
     * group membership list.
     *
     * @throws IllegalValueException if session data references a group the person is not a member of.
     */
    public static void validateGroupSessions(Person person) throws IllegalValueException {
        for (GroupName groupName : person.getGroupSessions().keySet()) {
            if (!person.getGroups().contains(groupName)) {
                throw new IllegalValueException(String.format(
                        HAS_SESSION_BUT_NOT_IN_GROUP,
                        groupName.value));
            }
        }
    }

    private static void validatePersonIsMemberOfGroup(Person person, GroupName groupName)
            throws IllegalValueException {
        if (!person.getGroups().contains(groupName)) {
            throw new IllegalValueException(String.format(
                    HAS_GRADES_BUT_NOT_IN_GROUP,
                    groupName.value));
        }
    }

    private static void validateAssignmentAgainstGroup(Group group, GroupName groupName,
                                                       Map<AssignmentName,
                                                               Integer> grades) throws IllegalValueException {
        for (var gradeEntry : grades.entrySet()) {
            AssignmentName assignmentName = gradeEntry.getKey();
            int grade = gradeEntry.getValue();

            if (!group.hasAssignment(assignmentName)) {
                throw new IllegalValueException(String.format(
                        ASSIGNMENT_DOES_NOT_EXIST,
                        assignmentName.value, groupName.value));
            }

            Assignment assignment = group.findAssignmentByName(assignmentName)
                    .orElseThrow(() -> new AssertionError(
                    "Assignment '" + assignmentName.value + "' should exist after hasAssignment check"));

            if (grade > assignment.getMaxMarks()) {
                throw new IllegalValueException(String.format(
                        GRADE_EXCEEDS_MAX_MARKS,
                        grade, assignmentName.value, groupName.value, assignment.getMaxMarks()));
            }
        }
    }
}

