package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEXES;

import java.time.LocalDate;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.GroupName;

/**
 * Parses input arguments and creates a new MarkCommand object.
 */
public class MarkCommandParser implements Parser<MarkCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the MarkCommand
     * and returns a MarkCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public MarkCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_INDEXES, PREFIX_DATE, PREFIX_GROUP);

        if (!argMultimap.getValue(PREFIX_INDEXES).isPresent() || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_INDEXES, PREFIX_DATE, PREFIX_GROUP);
        try {
            Index index = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_INDEXES).get());
            Optional<LocalDate> date = Optional.empty();
            if (argMultimap.getValue(PREFIX_DATE).isPresent()) {
                date = Optional.of(ParserUtil.parseSessionDate(argMultimap.getValue(PREFIX_DATE).get()));
            }

            Optional<GroupName> groupName = Optional.empty();
            if (argMultimap.getValue(PREFIX_GROUP).isPresent()) {
                groupName = Optional.of(
                        ParserUtil.parseGroupName(argMultimap.getValue(PREFIX_GROUP).get())
                );
            }

            return new MarkCommand(index, date, groupName);
        } catch (ParseException e) {
            throw e;
        }
    }
}
