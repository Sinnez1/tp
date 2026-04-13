package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import seedu.address.logic.commands.DeleteSessionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.GroupName;

/**
 * Parses input arguments and creates a new DeleteSessionCommand object.
 */
public class DeleteSessionCommandParser implements Parser<DeleteSessionCommand> {

    @Override
    public DeleteSessionCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_DATE, PREFIX_GROUP);
        String preamble = argMultimap.getPreamble().trim();
        if (!arePrefixesPresent(argMultimap, PREFIX_DATE) || !preamble.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteSessionCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_DATE, PREFIX_GROUP);
        String dateValue = argMultimap.getValue(PREFIX_DATE).get();
        ParserUtil.rejectDateExtraTokens(dateValue, DeleteSessionCommand.MESSAGE_USAGE);
        LocalDate sessionDate = ParserUtil.parseSessionDate(dateValue);
        if (argMultimap.getValue(PREFIX_GROUP).isPresent()) {
            GroupName groupName = ParserUtil.parseGroupName(argMultimap.getValue(PREFIX_GROUP).get());
            return new DeleteSessionCommand(sessionDate, Optional.of(groupName));
        }
        return new DeleteSessionCommand(sessionDate, Optional.empty());
    }

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
