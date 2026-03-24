package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classspace.Group;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Person;

/**
 * Deletes a class space.
 */
public class DeleteGroupCommand extends Command {

    public static final String COMMAND_WORD = "deletegroup";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes a group.\n"
            + "Parameters: g/GROUP_NAME\n"
            + "Example: " + COMMAND_WORD + " g/T01";

    public static final String MESSAGE_SUCCESS = "Deleted group: %1$s";
    public static final String MESSAGE_GROUP_NOT_FOUND = "This group does not exist.";

    private final ClassSpaceName classSpaceName;

    public DeleteGroupCommand(ClassSpaceName classSpaceName) {
        this.classSpaceName = classSpaceName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group group = model.findClassSpaceByName(classSpaceName)
                .orElseThrow(() -> new CommandException(MESSAGE_GROUP_NOT_FOUND));

        for (Person person : java.util.List.copyOf(model.getAddressBook().getPersonList())) {
            if (!person.hasClassSpace(classSpaceName)) {
                continue;
            }
            Person updatedPerson = person.withoutClassSpaceData(classSpaceName);
            model.setPerson(person, updatedPerson);
        }

        model.deleteClassSpace(group);
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
