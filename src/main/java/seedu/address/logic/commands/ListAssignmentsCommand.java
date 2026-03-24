package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;

import seedu.address.model.Model;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.classspace.Group;
import seedu.address.model.person.Person;

/**
 * Lists all assignments in the current class space.
 */
public class ListAssignmentsCommand extends ClassScopedAssignmentCommand {

    public static final String COMMAND_WORD = "listassignments";
    public static final String SHORT_COMMAND_WORD = "lista";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " (alias: " + SHORT_COMMAND_WORD + ")"
            + ": Lists all assignments in the current class space.\n"
            + "Example: " + SHORT_COMMAND_WORD;

    @Override
    public CommandResult execute(Model model) throws seedu.address.logic.commands.exceptions.CommandException {
        requireNonNull(model);
        Group activeGroup = getActiveClassSpace(model);
        List<Assignment> assignments = activeGroup.getAssignments();
        if (assignments.isEmpty()) {
            return new CommandResult(String.format("No assignments in %s.",
                    activeGroup.getClassSpaceName().value));
        }

        List<Person> studentsInClass = getStudentsInClass(model, activeGroup.getClassSpaceName());
        String assignmentSummary = assignments.stream()
                .map(assignment -> formatAssignment(assignment, studentsInClass, activeGroup))
                .collect(Collectors.joining("\n"));
        return new CommandResult(String.format("Assignments in %s:\n%s",
                activeGroup.getClassSpaceName().value, assignmentSummary));
    }

    private String formatAssignment(Assignment assignment, List<Person> studentsInClass, Group group) {
        long gradedCount = studentsInClass.stream()
                .filter(person -> person.getAssignmentGrade(group.getClassSpaceName(),
                                assignment.getAssignmentName())
                        .isPresent())
                .count();
        return String.format("%s | due %s | max %d | %d/%d graded",
                assignment.getAssignmentName().value,
                assignment.getDueDate(),
                assignment.getMaxMarks(),
                gradedCount,
                studentsInClass.size());
    }
}
