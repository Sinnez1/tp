package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ListGroupsCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ListGroupsCommand object.
 */
public class ListGroupsCommandParser implements Parser<ListGroupsCommand> {

    @Override
    public ListGroupsCommand parse(String args) throws ParseException {
        if (!args.trim().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ListGroupsCommand.MESSAGE_USAGE));
        }
        return new ListGroupsCommand();
    }
}
