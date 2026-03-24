package seedu.address.logic.commands;

import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.classspace.Group;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Person;

/**
 * Shared logic for assignment commands that operate within the active class-space context.
 */
abstract class ClassScopedAssignmentCommand extends Command {

    static final String MESSAGE_REQUIRE_ACTIVE_CLASS_SPACE =
            "Assignment commands can only be used when viewing a specific class space.";
    static final String MESSAGE_ASSIGNMENT_NOT_FOUND = "This assignment does not exist in the current class space.";
    static final String MESSAGE_DUPLICATE_ASSIGNMENT =
            "An assignment with that name already exists in the current class space.";
    static final String MESSAGE_INVALID_MAX_MARKS_FOR_EXISTING_GRADES =
            "New max marks cannot be lower than an existing grade for this assignment.";

    protected Group getActiveClassSpace(Model model) throws CommandException {
        ClassSpaceName activeClassSpaceName = model.getActiveClassSpaceName()
                .orElseThrow(() -> new CommandException(MESSAGE_REQUIRE_ACTIVE_CLASS_SPACE));
        return model.findClassSpaceByName(activeClassSpaceName)
                .orElseThrow(() -> new CommandException(MESSAGE_REQUIRE_ACTIVE_CLASS_SPACE));
    }

    protected Assignment getRequiredAssignment(Group group, AssignmentName assignmentName)
            throws CommandException {
        return group.findAssignmentByName(assignmentName)
                .orElseThrow(() -> new CommandException(MESSAGE_ASSIGNMENT_NOT_FOUND));
    }

    protected List<Person> getStudentsInClass(Model model, ClassSpaceName classSpaceName) {
        return model.getAddressBook().getPersonList().stream()
                .filter(person -> person.hasClassSpace(classSpaceName))
                .toList();
    }
}
