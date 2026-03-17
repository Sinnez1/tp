package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;

import java.time.LocalDate;

import seedu.address.logic.commands.AttViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Attendance;

/**
 * Parses input arguments and creates a new AttViewCommand object.
 */
public class AttViewCommandParser implements Parser<AttViewCommand> {
    public static final String MESSAGE_INVALID_ATTENDANCE_STATUS =
            "Attendance status must be one of: PRESENT, ABSENT, UNINITIALISED.";
    public static final String MESSAGE_TOO_MANY_ARGUMENTS =
            "attview accepts at most one attendance status and one g/GROUP_NAME.";

    /**
     * Parses the given {@code String} of arguments in the context of the AttViewCommand
     * and returns an AttViewCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public AttViewCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            return new AttViewCommand();
        }

        String tokenizableArgs = " " + trimmedArgs;
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(tokenizableArgs, PREFIX_GROUP, PREFIX_DATE);
        String preamble = argMultimap.getPreamble();
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GROUP, PREFIX_DATE);

        ClassSpaceName classSpaceName = null;
        if (argMultimap.getValue(PREFIX_GROUP).isPresent()) {
            classSpaceName = ParserUtil.parseClassSpaceName(argMultimap.getValue(PREFIX_GROUP).get());
        }

        LocalDate sessionDate = null;
        if (argMultimap.getValue(PREFIX_DATE).isPresent()) {
            sessionDate = ParserUtil.parseSessionDate(argMultimap.getValue(PREFIX_DATE).get());
        }

        if (preamble.isBlank()) {
            if (classSpaceName != null && sessionDate != null) {
                return new AttViewCommand(classSpaceName, sessionDate);
            }
            if (classSpaceName != null) {
                return new AttViewCommand(classSpaceName);
            }
            if (sessionDate != null) {
                return new AttViewCommand(sessionDate);
            }
            return new AttViewCommand();
        }

        String[] parts = preamble.trim().split("\\s+");
        if (parts.length != 1) {
            throw new ParseException(MESSAGE_TOO_MANY_ARGUMENTS + "\n" + AttViewCommand.MESSAGE_USAGE);
        }

        try {
            Attendance attendance = new Attendance(parts[0]);
            if (classSpaceName != null && sessionDate != null) {
                return new AttViewCommand(attendance, classSpaceName, sessionDate);
            }
            if (classSpaceName != null) {
                return new AttViewCommand(attendance, classSpaceName);
            }
            if (sessionDate != null) {
                return new AttViewCommand(attendance, sessionDate);
            }
            return new AttViewCommand(attendance);
        } catch (IllegalArgumentException e) {
            throw new ParseException(MESSAGE_INVALID_ATTENDANCE_STATUS + "\n" + AttViewCommand.MESSAGE_USAGE, e);
        }
    }
}
