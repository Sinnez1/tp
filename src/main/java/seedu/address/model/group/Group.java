package seedu.address.model.group;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.assignment.UniqueAssignmentList;

/**
 * Group represents a real-world Group, such as Tutorial Group, Lab Group, and similar.
 * This concept is purely internal to the codebase, and all user-facing strings should use the term
 * "{@code GROUP_NAME}".
 */
// @@author ongrussell
public class Group {
    private final GroupName groupName;
    private final UniqueAssignmentList assignments;

    /**
     * Creates a {@code Group} with the given name.
     *
     * @param groupName Name of the group.
     */
    public Group(GroupName groupName) {
        this(groupName, List.of());
    }

    /**
     * Creates a {@code Group} with the given name and assignments.
     */
    public Group(GroupName groupName, List<Assignment> assignments) {
        requireNonNull(groupName);
        requireNonNull(assignments);
        this.groupName = groupName;
        this.assignments = new UniqueAssignmentList();
        this.assignments.setAssignments(assignments);
    }

    public GroupName getGroupName() {
        return groupName;
    }

    /**
     * Returns an unmodifiable view of the assignments belonging to this group.
     */
    public ObservableList<Assignment> getAssignments() {
        return assignments.asUnmodifiableObservableList();
    }

    /**
     * Returns whether this group contains an assignment with the given name.
     */
    public boolean hasAssignment(AssignmentName assignmentName) {
        requireNonNull(assignmentName);
        return findAssignmentByName(assignmentName).isPresent();
    }

    /**
     * Returns the assignment with the given name if present.
     */
    public Optional<Assignment> findAssignmentByName(AssignmentName assignmentName) {
        requireNonNull(assignmentName);
        return assignments.findAssignmentByName(assignmentName);
    }

    /**
     * Returns true if both groups have the same identity.
     */
    public boolean isSameGroup(Group otherGroup) {
        if (otherGroup == this) {
            return true;
        }

        return otherGroup != null
                && groupName.equals(otherGroup.groupName);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Group)) {
            return false;
        }

        Group otherGroup = (Group) other;
        return groupName.equals(otherGroup.groupName)
                && assignments.equals(otherGroup.assignments);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(groupName, assignments);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("groupName", groupName)
                .add("assignments", assignments)
                .toString();
    }

    /**
     * Returns name in GroupName as a String.
     *
     * @return GroupName as a String.
     */
    public String getGroupNameValue() {
        return groupName.value;
    }
}
// @@author
