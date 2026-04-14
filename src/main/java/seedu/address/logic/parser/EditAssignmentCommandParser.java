package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MAX_MARKS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEW_ASSIGNMENT;

import seedu.address.logic.commands.EditAssignmentCommand;
import seedu.address.logic.commands.EditAssignmentCommand.EditAssignmentDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.assignment.AssignmentName;

/**
 * Parses input arguments and creates a new EditAssignmentCommand object.
 */
public class EditAssignmentCommandParser implements Parser<EditAssignmentCommand> {

    @Override
    public EditAssignmentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ASSIGNMENT, PREFIX_NEW_ASSIGNMENT,
                PREFIX_DATE, PREFIX_MAX_MARKS);
        if (!argMultimap.getValue(PREFIX_ASSIGNMENT).isPresent() || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditAssignmentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ASSIGNMENT, PREFIX_NEW_ASSIGNMENT, PREFIX_DATE,
                PREFIX_MAX_MARKS);

        AssignmentName targetAssignmentName =
                ParserUtil.parseAssignmentName(argMultimap.getValue(PREFIX_ASSIGNMENT).get());
        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();

        if (argMultimap.getValue(PREFIX_NEW_ASSIGNMENT).isPresent()) {
            descriptor.setNewAssignmentName(
                    ParserUtil.parseAssignmentName(argMultimap.getValue(PREFIX_NEW_ASSIGNMENT).get()));
        }
        if (argMultimap.getValue(PREFIX_DATE).isPresent()) {
            String dateValue = argMultimap.getValue(PREFIX_DATE).get();
            ParserUtil.rejectDateExtraTokens(dateValue, EditAssignmentCommand.MESSAGE_USAGE);
            descriptor.setDueDate(ParserUtil.parseSessionDate(dateValue));
        }
        if (argMultimap.getValue(PREFIX_MAX_MARKS).isPresent()) {
            descriptor.setMaxMarks(ParserUtil.parseMaxMarks(argMultimap.getValue(PREFIX_MAX_MARKS).get()));
        }

        if (!descriptor.isAnyFieldEdited()) {
            throw new ParseException(EditAssignmentCommand.MESSAGE_NOT_EDITED);
        }

        return new EditAssignmentCommand(targetAssignmentName, descriptor);
    }
}
