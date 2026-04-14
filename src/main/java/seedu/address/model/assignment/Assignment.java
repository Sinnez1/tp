package seedu.address.model.assignment;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents an assignment belonging to a group.
 */
public class Assignment {

    public static final String MESSAGE_MAX_MARKS_CONSTRAINTS =
            "Max marks should be an integer between 1 and 2147483647.";

    private final AssignmentName assignmentName;
    private final LocalDate dueDate;
    private final int maxMarks;

    /**
     * Creates an {@code Assignment}.
     */
    public Assignment(AssignmentName assignmentName, LocalDate dueDate, int maxMarks) {
        requireNonNull(assignmentName);
        requireNonNull(dueDate);
        if (!isValidMaxMarks(maxMarks)) {
            throw new IllegalArgumentException(MESSAGE_MAX_MARKS_CONSTRAINTS);
        }
        this.assignmentName = assignmentName;
        this.dueDate = dueDate;
        this.maxMarks = maxMarks;
    }

    public static boolean isValidMaxMarks(int maxMarks) {
        return maxMarks > 0;
    }

    public AssignmentName getAssignmentName() {
        return assignmentName;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public int getMaxMarks() {
        return maxMarks;
    }

    /**
     * Returns true if both assignments have the same identity.
     */
    public boolean isSameAssignment(Assignment otherAssignment) {
        if (otherAssignment == this) {
            return true;
        }

        return otherAssignment != null
                && assignmentName.equals(otherAssignment.assignmentName);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Assignment)) {
            return false;
        }

        Assignment otherAssignment = (Assignment) other;
        return assignmentName.equals(otherAssignment.assignmentName)
                && dueDate.equals(otherAssignment.dueDate)
                && maxMarks == otherAssignment.maxMarks;
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignmentName, dueDate, maxMarks);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("assignmentName", assignmentName)
                .add("dueDate", dueDate)
                .add("maxMarks", maxMarks)
                .toString();
    }
}
