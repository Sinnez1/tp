package seedu.address.model.classspace;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.assignment.UniqueAssignmentList;

/**
 * Class Space represents a real-world Group, such as Tutorial Group, Lab Group, and similar.
 * This concept is purely internal to the codebase, and all user-facing strings should use the term
 * "{@code GROUP_NAME}".
 */
public class Group {
    private final ClassSpaceName classSpaceName;
    private final UniqueAssignmentList assignments;

    /**
     * Creates a {@code Group} with the given name.
     *
     * @param classSpaceName Name of the class space.
     */
    public Group(ClassSpaceName classSpaceName) {
        this(classSpaceName, List.of());
    }

    /**
     * Creates a {@code Group} with the given name and assignments.
     */
    public Group(ClassSpaceName classSpaceName, List<Assignment> assignments) {
        requireNonNull(classSpaceName);
        requireNonNull(assignments);
        this.classSpaceName = classSpaceName;
        this.assignments = new UniqueAssignmentList();
        this.assignments.setAssignments(assignments);
    }

    public ClassSpaceName getClassSpaceName() {
        return classSpaceName;
    }

    /**
     * Returns an unmodifiable view of the assignments belonging to this class space.
     */
    public ObservableList<Assignment> getAssignments() {
        return assignments.asUnmodifiableObservableList();
    }

    /**
     * Returns whether this class space contains an assignment with the given name.
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
     * Returns true if both class spaces have the same identity.
     */
    public boolean isSameClassSpace(Group otherGroup) {
        if (otherGroup == this) {
            return true;
        }

        return otherGroup != null
                && classSpaceName.equals(otherGroup.classSpaceName);
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
        return classSpaceName.equals(otherGroup.classSpaceName)
                && assignments.equals(otherGroup.assignments);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(classSpaceName, assignments);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("groupName", classSpaceName)
                .add("assignments", assignments)
                .toString();
    }
}
