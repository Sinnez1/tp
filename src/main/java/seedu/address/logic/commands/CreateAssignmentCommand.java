package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.Group;

/**
 * Creates an assignment in the current group.
 */
public class CreateAssignmentCommand extends ClassScopedAssignmentCommand {

    public static final String COMMAND_WORD = "createassignment";
    public static final String SHORT_COMMAND_WORD = "createa";
    public static final String COMMAND_PARAMETERS = "a/ASSIGNMENT_NAME d/DUE_DATE mm/MAX_MARKS";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " (alias: " + SHORT_COMMAND_WORD + ")"
            + ": Creates an assignment in the current group.\n"
            + "This command only works when viewing a specific group.\n"
            + "Parameters: a/ASSIGNMENT_NAME d/DUE_DATE mm/MAX_MARKS\n"
            + "Example: " + SHORT_COMMAND_WORD + " a/Quiz 1 d/2026-04-05 mm/20";

    public static final String MESSAGE_SUCCESS = "Created assignment %1$s in %2$s.";

    private final AssignmentName assignmentName;
    private final LocalDate dueDate;
    private final int maxMarks;

    /**
     * Creates a {@code CreateAssignmentCommand}.
     */
    public CreateAssignmentCommand(AssignmentName assignmentName, LocalDate dueDate, int maxMarks) {
        requireNonNull(assignmentName);
        requireNonNull(dueDate);
        this.assignmentName = assignmentName;
        this.dueDate = dueDate;
        this.maxMarks = maxMarks;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group activeGroup = getActiveGroup(model);
        if (activeGroup.hasAssignment(assignmentName)) {
            throw new CommandException(MESSAGE_DUPLICATE_ASSIGNMENT);
        }

        List<Assignment> updatedAssignments = new ArrayList<>(activeGroup.getAssignments());
        updatedAssignments.add(new Assignment(assignmentName, dueDate, maxMarks));
        Group updatedGroup = new Group(activeGroup.getGroupName(), updatedAssignments);
        model.setGroup(activeGroup, updatedGroup);
        return new CommandResult(String.format(MESSAGE_SUCCESS, assignmentName.value,
                activeGroup.getGroupName().value));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CreateAssignmentCommand)) {
            return false;
        }
        CreateAssignmentCommand otherCommand = (CreateAssignmentCommand) other;
        return assignmentName.equals(otherCommand.assignmentName)
                && dueDate.equals(otherCommand.dueDate)
                && maxMarks == otherCommand.maxMarks;
    }
}
