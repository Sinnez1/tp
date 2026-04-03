package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;

import seedu.address.logic.commands.SwitchGroupCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.GroupName;

/**
 * Parses input arguments and creates a new SwitchGroupCommand object.
 */
// @@author ongrussell
public class SwitchGroupCommandParser implements Parser<SwitchGroupCommand> {

    @Override
    public SwitchGroupCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.equalsIgnoreCase(SwitchGroupCommand.ALL_VIEW_KEYWORD)) {
            return new SwitchGroupCommand();
        }

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_GROUP);
        if (!argMultimap.getValue(PREFIX_GROUP).isPresent() || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SwitchGroupCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GROUP);
        GroupName groupName = ParserUtil.parseGroupName(argMultimap.getValue(PREFIX_GROUP).get());
        return new SwitchGroupCommand(groupName);
    }
}
// @@author
