package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;

/**
 * Renames an existing group.
 */
// @@author ongrussell
public class RenameGroupCommand extends Command {

    public static final String COMMAND_WORD = "renamegroup";
    public static final String COMMAND_PARAMETERS = "g/OLD_GROUP_NAME new/NEW_GROUP_NAME";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Renames a group.\n"
            + "Parameters: " + COMMAND_PARAMETERS + "\n"
            + "Example: " + COMMAND_WORD + " g/T01 new/Tutorial-01";

    public static final String MESSAGE_GROUP_NOT_FOUND = "This group does not exist.";
    public static final String MESSAGE_DUPLICATE_GROUP = "Another group with that name already exists.";
    public static final String MESSAGE_SUCCESS = "Renamed group %1$s to %2$s";

    private final GroupName targetName;
    private final GroupName newName;

    /**
     * Creates a RenameGroupCommand to rename the specified group.
     *
     * @param targetName The existing group name.
     * @param newName The new group name.
     */
    public RenameGroupCommand(GroupName targetName, GroupName newName) {
        this.targetName = targetName;
        this.newName = newName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group target = model.findGroupByName(targetName)
                .orElseThrow(() -> new CommandException(MESSAGE_GROUP_NOT_FOUND));
        if (!targetName.equals(newName) && model.findGroupByName(newName).isPresent()) {
            throw new CommandException(MESSAGE_DUPLICATE_GROUP);
        }

        for (Person person : java.util.List.copyOf(model.getAddressBook().getPersonList())) {
            if (!person.hasGroup(targetName)) {
                continue;
            }
            Person updatedPerson = person.withRenamedGroup(targetName, newName);
            model.setPerson(person, updatedPerson);
        }

        model.setGroup(target, new Group(newName, java.util.List.copyOf(target.getAssignments())));
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
// @@author
