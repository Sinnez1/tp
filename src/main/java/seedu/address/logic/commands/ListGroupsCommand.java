package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.model.Model;
import seedu.address.model.classspace.Group;

/**
 * Lists all class spaces.
 */
public class ListGroupsCommand extends Command {

    public static final String COMMAND_WORD = "listgroups";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all groups.";
    public static final String MESSAGE_NO_GROUPS = "There are no groups yet.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        List<Group> groups = model.getClassSpaceList().stream()
                .sorted(Comparator.comparing(classSpace -> classSpace.getClassSpaceName().value,
                        String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        if (groups.isEmpty()) {
            return new CommandResult(MESSAGE_NO_GROUPS);
        }

        StringBuilder builder = new StringBuilder("Groups:\n");
        for (int i = 0; i < groups.size(); i++) {
            builder.append(i + 1)
                    .append(". ")
                    .append(groups.get(i).getClassSpaceName().value);
            if (i < groups.size() - 1) {
                builder.append("\n");
            }
        }
        return new CommandResult(builder.toString());
    }
}
