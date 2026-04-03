package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEXES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRIC_NUMBER;

import java.util.List;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.RemoveFromGroupCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.MatricNumber;

/**
 * Parses input arguments and creates a new RemoveFromGroupCommand object.
 */
// @@author ongrussell
public class RemoveFromGroupCommandParser implements Parser<RemoveFromGroupCommand> {

    @Override
    public RemoveFromGroupCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_GROUP, PREFIX_MATRIC_NUMBER, PREFIX_INDEXES);
        if (!arePrefixesPresent(argMultimap, PREFIX_GROUP) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RemoveFromGroupCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GROUP, PREFIX_INDEXES);
        GroupName groupName = ParserUtil.parseGroupName(argMultimap.getValue(PREFIX_GROUP).get());
        boolean hasMatricTargets = !argMultimap.getAllValues(PREFIX_MATRIC_NUMBER).isEmpty();
        boolean hasIndexTargets = argMultimap.getValue(PREFIX_INDEXES).isPresent();

        if (hasMatricTargets == hasIndexTargets) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RemoveFromGroupCommand.MESSAGE_USAGE));
        }

        if (hasIndexTargets) {
            List<Index> indexes = ParserUtil.parseIndexes(argMultimap.getValue(PREFIX_INDEXES).get());
            return RemoveFromGroupCommand.forIndexes(groupName, indexes);
        }

        List<MatricNumber> matricNumbers = convertMatricNumbers(argMultimap);
        return RemoveFromGroupCommand.forMatricNumbers(groupName, matricNumbers);
    }

    private static List<MatricNumber> convertMatricNumbers(ArgumentMultimap argMultimap) throws ParseException {
        java.util.ArrayList<MatricNumber> matricNumbers = new java.util.ArrayList<>();
        for (String value : argMultimap.getAllValues(PREFIX_MATRIC_NUMBER)) {
            matricNumbers.add(ParserUtil.parseMatricNumber(value));
        }
        return matricNumbers;
    }

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
// @@author
