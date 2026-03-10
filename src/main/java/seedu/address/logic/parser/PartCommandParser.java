package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.PartCommand;
import seedu.address.logic.parser.exceptions.ParseException;
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
        String trimmedArgs = args.trim();
        String[] parts = trimmedArgs.split("\\s+");

        if (parts.length != 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PartCommand.MESSAGE_USAGE));
        }

        try {
            Index index = ParserUtil.parseIndex(parts[0]);
            Participation participation = new Participation(parts[1]);
            return new PartCommand(index, participation);
        } catch (IllegalArgumentException | ParseException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PartCommand.MESSAGE_USAGE), e);
        }
    }
}