package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEXES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARTICIPATION;

import java.time.LocalDate;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.PartCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Participation;

/**
 * Parses input arguments and creates a new PartCommand object.
 */
public class PartCommandParser implements Parser<PartCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the PartCommand
     * and returns a PartCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public PartCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_INDEXES, PREFIX_DATE, PREFIX_GROUP, PREFIX_PARTICIPATION);

        if (!argMultimap.getValue(PREFIX_INDEXES).isPresent()
                || !argMultimap.getValue(PREFIX_PARTICIPATION).isPresent()
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PartCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_INDEXES, PREFIX_DATE, PREFIX_GROUP, PREFIX_PARTICIPATION);

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

        try {
            Participation participation =
                    new Participation(argMultimap.getValue(PREFIX_PARTICIPATION).get());
            return new PartCommand(index, date, groupName, participation);
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}
