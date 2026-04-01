package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.Group;
import seedu.address.model.person.Person;

/**
 * Deletes an assignment from the current group.
 */
public class DeleteAssignmentCommand extends ClassScopedAssignmentCommand {

    public static final String COMMAND_WORD = "deleteassignment";
    public static final String SHORT_COMMAND_WORD = "deletea";
    public static final String COMMAND_PARAMETERS = "a/ASSIGNMENT_NAME";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " (alias: " + SHORT_COMMAND_WORD + ")"
            + ": Deletes an assignment from the current group.\n"
            + "Parameters: " + COMMAND_PARAMETERS + "\n"
            + "Example: " + SHORT_COMMAND_WORD + " a/Quiz 1";

    public static final String MESSAGE_SUCCESS = "Deleted assignment %1$s from %2$s.";

    private final AssignmentName assignmentName;

    /**
     * Creates a {@code DeleteAssignmentCommand}.
     */
    public DeleteAssignmentCommand(AssignmentName assignmentName) {
        requireNonNull(assignmentName);
        this.assignmentName = assignmentName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group activeGroup = getActiveGroup(model);
        Assignment assignmentToDelete = getRequiredAssignment(activeGroup, assignmentName);

        List<Assignment> updatedAssignments = new ArrayList<>(activeGroup.getAssignments());
        updatedAssignments.remove(assignmentToDelete);
        Group updatedGroup = new Group(activeGroup.getGroupName(), updatedAssignments);
        model.setGroup(activeGroup, updatedGroup);

        for (Person person : List.copyOf(model.getAddressBook().getPersonList())) {
            if (!person.hasGroup(activeGroup.getGroupName())) {
                continue;
            }
            Person updatedPerson = person.withoutAssignmentGrade(activeGroup.getGroupName(), assignmentName);
            if (!updatedPerson.equals(person)) {
                model.setPerson(person, updatedPerson);
            }
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, assignmentName.value,
                activeGroup.getGroupName().value));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteAssignmentCommand)) {
            return false;
        }
        DeleteAssignmentCommand otherCommand = (DeleteAssignmentCommand) other;
        return assignmentName.equals(otherCommand.assignmentName);
    }
}
