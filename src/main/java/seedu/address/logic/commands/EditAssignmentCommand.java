package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.Group;
import seedu.address.model.person.Person;

/**
 * Edits an existing assignment in the current group.
 */
public class EditAssignmentCommand extends ClassScopedAssignmentCommand {

    public static final String COMMAND_WORD = "editassignment";
    public static final String SHORT_COMMAND_WORD = "edita";
    public static final String COMMAND_PARAMETERS =
            "a/ASSIGNMENT_NAME [na/NEW_ASSIGNMENT_NAME] [d/NEW_DUE_DATE] [mm/NEW_MAX_MARKS]";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " (alias: " + SHORT_COMMAND_WORD + ")"
            + ": Edits an existing assignment in the current group.\n"
            + "This command only works when viewing a specific group.\n"
            + "Parameters: a/ASSIGNMENT_NAME [na/NEW_ASSIGNMENT_NAME] [d/NEW_DUE_DATE] [mm/NEW_MAX_MARKS]\n"
            + "Example: " + SHORT_COMMAND_WORD + " a/Quiz 1 na/Quiz 1 Revised d/2026-04-08 mm/25";

    public static final String MESSAGE_SUCCESS = "Edited assignment %1$s in %2$s.";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";

    private final AssignmentName targetAssignmentName;
    private final EditAssignmentDescriptor editAssignmentDescriptor;

    /**
     * Creates an {@code EditAssignmentCommand}.
     */
    public EditAssignmentCommand(AssignmentName targetAssignmentName,
                                 EditAssignmentDescriptor editAssignmentDescriptor) {
        requireNonNull(targetAssignmentName);
        requireNonNull(editAssignmentDescriptor);
        this.targetAssignmentName = targetAssignmentName;
        this.editAssignmentDescriptor = new EditAssignmentDescriptor(editAssignmentDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group activeGroup = getActiveGroup(model);
        Assignment assignmentToEdit = getRequiredAssignment(activeGroup, targetAssignmentName);
        Assignment editedAssignment = createEditedAssignment(assignmentToEdit, editAssignmentDescriptor);

        if (!assignmentToEdit.getAssignmentName().equals(editedAssignment.getAssignmentName())
                && activeGroup.hasAssignment(editedAssignment.getAssignmentName())) {
            throw new CommandException(MESSAGE_DUPLICATE_ASSIGNMENT);
        }

        if (editedAssignment.getMaxMarks() < findHighestExistingGrade(model, activeGroup, targetAssignmentName)) {
            throw new CommandException(MESSAGE_INVALID_MAX_MARKS_FOR_EXISTING_GRADES);
        }

        List<Assignment> updatedAssignments = new ArrayList<>(activeGroup.getAssignments());
        int index = updatedAssignments.indexOf(assignmentToEdit);
        updatedAssignments.set(index, editedAssignment);
        Group updatedGroup = new Group(activeGroup.getGroupName(), updatedAssignments);
        model.setGroup(activeGroup, updatedGroup);

        if (!targetAssignmentName.equals(editedAssignment.getAssignmentName())) {
            for (Person person : List.copyOf(model.getAddressBook().getPersonList())) {
                if (!person.hasGroup(activeGroup.getGroupName())) {
                    continue;
                }
                Person updatedPerson = person.withRenamedAssignmentGrade(activeGroup.getGroupName(),
                        targetAssignmentName, editedAssignment.getAssignmentName());
                if (!updatedPerson.equals(person)) {
                    model.setPerson(person, updatedPerson);
                }
            }
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, editedAssignment.getAssignmentName().value,
                activeGroup.getGroupName().value));
    }

    private Assignment createEditedAssignment(Assignment assignmentToEdit,
                                              EditAssignmentDescriptor editAssignmentDescriptor) {
        AssignmentName updatedAssignmentName = editAssignmentDescriptor.getNewAssignmentName()
                .orElse(assignmentToEdit.getAssignmentName());
        LocalDate updatedDueDate = editAssignmentDescriptor.getDueDate().orElse(assignmentToEdit.getDueDate());
        int updatedMaxMarks = editAssignmentDescriptor.getMaxMarks().orElse(assignmentToEdit.getMaxMarks());
        return new Assignment(updatedAssignmentName, updatedDueDate, updatedMaxMarks);
    }

    private int findHighestExistingGrade(Model model, Group activeGroup, AssignmentName assignmentName) {
        return getStudentsInClass(model, activeGroup.getGroupName()).stream()
                .flatMap(person -> person.getAssignmentGrade(activeGroup.getGroupName(),
                                assignmentName)
                        .stream())
                .max(Integer::compareTo)
                .orElse(0);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof EditAssignmentCommand)) {
            return false;
        }
        EditAssignmentCommand otherCommand = (EditAssignmentCommand) other;
        return targetAssignmentName.equals(otherCommand.targetAssignmentName)
                && editAssignmentDescriptor.equals(otherCommand.editAssignmentDescriptor);
    }

    /**
     * Stores the details to edit the assignment with.
     */
    public static class EditAssignmentDescriptor {
        private AssignmentName newAssignmentName;
        private LocalDate dueDate;
        private Integer maxMarks;

        /**
         * Creates an empty {@code EditAssignmentDescriptor}.
         */
        public EditAssignmentDescriptor() {}

        /**
         * Creates a {@code EditAssignmentDescriptor} as a copy of the given descriptor.
         */
        public EditAssignmentDescriptor(EditAssignmentDescriptor toCopy) {
            setNewAssignmentName(toCopy.newAssignmentName);
            setDueDate(toCopy.dueDate);
            setMaxMarks(toCopy.maxMarks);
        }

        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(newAssignmentName, dueDate, maxMarks);
        }

        public void setNewAssignmentName(AssignmentName newAssignmentName) {
            this.newAssignmentName = newAssignmentName;
        }

        public Optional<AssignmentName> getNewAssignmentName() {
            return Optional.ofNullable(newAssignmentName);
        }

        public void setDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
        }

        public Optional<LocalDate> getDueDate() {
            return Optional.ofNullable(dueDate);
        }

        public void setMaxMarks(Integer maxMarks) {
            this.maxMarks = maxMarks;
        }

        public Optional<Integer> getMaxMarks() {
            return Optional.ofNullable(maxMarks);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof EditAssignmentDescriptor)) {
                return false;
            }
            EditAssignmentDescriptor otherDescriptor = (EditAssignmentDescriptor) other;
            return Objects.equals(newAssignmentName, otherDescriptor.newAssignmentName)
                    && Objects.equals(dueDate, otherDescriptor.dueDate)
                    && Objects.equals(maxMarks, otherDescriptor.maxMarks);
        }
    }
}
