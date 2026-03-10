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
 * Renames an existing class space.
 */
public class RenameGroupCommand extends Command {

    public static final String COMMAND_WORD = "renamegroup";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Renames a class space.\n"
            + "Parameters: g/OLD_GROUP_NAME new/NEW_GROUP_NAME\n"
            + "Example: " + COMMAND_WORD + " g/T01 new/Tutorial-01";

    public static final String MESSAGE_GROUP_NOT_FOUND = "This class space does not exist.";
    public static final String MESSAGE_DUPLICATE_GROUP = "Another class space with that name already exists.";
    public static final String MESSAGE_SUCCESS = "Renamed class space %1$s to %2$s";

    private final ClassSpaceName targetName;
    private final ClassSpaceName newName;

    /**
     * Creates a RenameGroupCommand to rename the specified class space.
     *
     * @param targetName The existing class space name.
     * @param newName The new class space name.
     */
    public RenameGroupCommand(ClassSpaceName targetName, ClassSpaceName newName) {
        this.targetName = targetName;
        this.newName = newName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        ClassSpace target = model.findClassSpaceByName(targetName)
                .orElseThrow(() -> new CommandException(MESSAGE_GROUP_NOT_FOUND));
        if (!targetName.equals(newName) && model.findClassSpaceByName(newName).isPresent()) {
            throw new CommandException(MESSAGE_DUPLICATE_GROUP);
        }

        for (Person person : java.util.List.copyOf(model.getAddressBook().getPersonList())) {
            if (!person.hasClassSpace(targetName)) {
                continue;
            }
            Set<ClassSpaceName> updatedClassSpaces = new HashSet<>(person.getClassSpaces());
            updatedClassSpaces.remove(targetName);
            updatedClassSpaces.add(newName);
            Person updatedPerson = new Person(person.getName(), person.getPhone(), person.getEmail(),
                    person.getMatricNumber(), person.getParticipation(), person.getTags(), updatedClassSpaces);
            model.setPerson(person, updatedPerson);
        }

        model.setClassSpace(target, new ClassSpace(newName));
        return new CommandResult(String.format(MESSAGE_SUCCESS, targetName.value, newName.value));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RenameGroupCommand)) {
            return false;
        }
        RenameGroupCommand otherCommand = (RenameGroupCommand) other;
        return targetName.equals(otherCommand.targetName)
                && newName.equals(otherCommand.newName);
    }
}
