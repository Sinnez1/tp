package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Set;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classspace.ClassSpace;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Person;

/**
 * Deletes a class space.
 */
public class DeleteGroupCommand extends Command {

    public static final String COMMAND_WORD = "deletegroup";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes a class space.\n"
            + "Parameters: g/GROUP_NAME\n"
            + "Example: " + COMMAND_WORD + " g/T01";

    public static final String MESSAGE_SUCCESS = "Deleted class space: %1$s";
    public static final String MESSAGE_GROUP_NOT_FOUND = "This class space does not exist.";

    private final ClassSpaceName classSpaceName;

    public DeleteGroupCommand(ClassSpaceName classSpaceName) {
        this.classSpaceName = classSpaceName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        ClassSpace classSpace = model.findClassSpaceByName(classSpaceName)
                .orElseThrow(() -> new CommandException(MESSAGE_GROUP_NOT_FOUND));

        for (Person person : java.util.List.copyOf(model.getAddressBook().getPersonList())) {
            if (!person.hasClassSpace(classSpaceName)) {
                continue;
            }
            Set<ClassSpaceName> updatedClassSpaces = new HashSet<>(person.getClassSpaces());
            updatedClassSpaces.remove(classSpaceName);
            Person updatedPerson = new Person(person.getName(), person.getPhone(), person.getEmail(),
                    person.getMatricNumber(), person.getParticipation(), person.getTags(), updatedClassSpaces);
            model.setPerson(person, updatedPerson);
        }

        model.deleteClassSpace(classSpace);
        return new CommandResult(String.format(MESSAGE_SUCCESS, classSpaceName.value));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteGroupCommand)) {
            return false;
        }
        DeleteGroupCommand otherCommand = (DeleteGroupCommand) other;
        return classSpaceName.equals(otherCommand.classSpaceName);
    }
}
