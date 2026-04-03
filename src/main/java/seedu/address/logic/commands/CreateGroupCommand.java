package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;

/**
 * Creates a new group.
 */
// @@author ongrussell
public class CreateGroupCommand extends Command {

    public static final String COMMAND_WORD = "creategroup";
    public static final String COMMAND_PARAMETERS = "g/GROUP_NAME";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Creates a group.\n"
            + "Parameters: " + COMMAND_PARAMETERS + "\n"
            + "Example: " + COMMAND_WORD + " g/T01";

    public static final String MESSAGE_SUCCESS = "Created group: %1$s";
    public static final String MESSAGE_DUPLICATE_GROUP = "This group already exists.";

    private static final Logger logger = LogsCenter.getLogger(CreateGroupCommand.class);

    private final Group group;

    public CreateGroupCommand(Group group) {
        this.group = group;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        logger.info("Attempting to create group: " + group.getGroupName().value);
        if (model.hasGroup(group)) {
            logger.warning("Duplicate group creation attempted: " + group.getGroupName().value);
            throw new CommandException(MESSAGE_DUPLICATE_GROUP);
        }
        model.addGroup(group);
        logger.info("Created group: " + group.getGroupName().value);
        return new CommandResult(String.format(MESSAGE_SUCCESS, group.getGroupName().value));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CreateGroupCommand)) {
            return false;
        }
        CreateGroupCommand otherCommand = (CreateGroupCommand) other;
        return group.equals(otherCommand.group);
    }
}
// @@author
