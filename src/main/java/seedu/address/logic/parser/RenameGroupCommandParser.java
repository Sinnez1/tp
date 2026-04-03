package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEW_NAME;

import java.util.stream.Stream;

import seedu.address.logic.commands.RenameGroupCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.GroupName;

/**
 * Parses input arguments and creates a new RenameGroupCommand object.
 */
// @@author ongrussell
public class RenameGroupCommandParser implements Parser<RenameGroupCommand> {

    @Override
    public RenameGroupCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_GROUP, PREFIX_NEW_NAME);
        if (!arePrefixesPresent(argMultimap, PREFIX_GROUP, PREFIX_NEW_NAME) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RenameGroupCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GROUP, PREFIX_NEW_NAME);
        GroupName targetName = ParserUtil.parseGroupName(argMultimap.getValue(PREFIX_GROUP).get());
        GroupName newName = ParserUtil.parseGroupName(argMultimap.getValue(PREFIX_NEW_NAME).get());
        return new RenameGroupCommand(targetName, newName);
    }

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
// @@author
