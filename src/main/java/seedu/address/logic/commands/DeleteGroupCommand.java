package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;

/**
 * Deletes a group.
 */
// @@author ongrussell
public class DeleteGroupCommand extends Command {

    public static final String COMMAND_WORD = "deletegroup";
    public static final String COMMAND_PARAMETERS = "g/GROUP_NAME";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes a group.\n"
            + "Parameters: " + COMMAND_PARAMETERS + "\n"
            + "Example: " + COMMAND_WORD + " g/T01";

    public static final String MESSAGE_SUCCESS = "Deleted group: %1$s";
    public static final String MESSAGE_GROUP_NOT_FOUND = "This group does not exist.";
    private static final Logger logger = LogsCenter.getLogger(DeleteGroupCommand.class);

    private final GroupName groupName;

    public DeleteGroupCommand(GroupName groupName) {
        this.groupName = groupName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        logger.info("Attempting to delete group: " + groupName.value);

        Group group = model.findGroupByName(groupName)
                .orElseThrow(() -> {
                    logger.warning("Attempted to delete missing group: " + groupName.value);
                    return new CommandException(MESSAGE_GROUP_NOT_FOUND);
                });

        int affectedStudents = 0;

        for (Person person : java.util.List.copyOf(model.getAddressBook().getPersonList())) {
            if (!person.hasGroup(groupName)) {
                continue;
            }
            Person updatedPerson = person.withoutGroupData(groupName);
            model.setPerson(person, updatedPerson);
            affectedStudents++;
        }

        model.deleteGroup(group);
        logger.info("Deleted group: " + groupName.value
                + ". Removed membership from " + affectedStudents + " student(s).");
        return new CommandResult(String.format(MESSAGE_SUCCESS, groupName.value));
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
        return groupName.equals(otherCommand.groupName);
    }
}
// @@author
