package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO_DATE;

import java.time.LocalDate;
import java.util.Optional;

import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Attendance;

/**
 * Parses input arguments and creates a new ViewCommand object.
 */
public class ViewCommandParser implements Parser<ViewCommand> {
    public static final String MESSAGE_INVALID_ATTENDANCE_STATUS =
            "Attendance status must be one of: PRESENT, ABSENT, UNINITIALISED.";
    public static final String MESSAGE_TOO_MANY_ARGUMENTS =
            "view accepts at most one attendance status and one g/GROUP_NAME.";

    @Override
    public ViewCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            return new ViewCommand();
        }

        String tokenizableArgs = " " + trimmedArgs;
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(tokenizableArgs,
                PREFIX_GROUP, PREFIX_DATE, PREFIX_FROM_DATE, PREFIX_TO_DATE);
        String preamble = argMultimap.getPreamble();
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GROUP, PREFIX_DATE, PREFIX_FROM_DATE, PREFIX_TO_DATE);

        GroupName groupName = null;
        if (argMultimap.getValue(PREFIX_GROUP).isPresent()) {
            groupName = ParserUtil.parseGroupName(argMultimap.getValue(PREFIX_GROUP).get());
        }

        LocalDate sessionDate = null;
        if (argMultimap.getValue(PREFIX_DATE).isPresent()) {
            String dateValue = argMultimap.getValue(PREFIX_DATE).get();
            ParserUtil.rejectDateExtraTokens(dateValue, ViewCommand.MESSAGE_USAGE);
            sessionDate = ParserUtil.parseSessionDate(dateValue);
        }
        Optional<LocalDate> rangeStartDate = Optional.empty();
        if (argMultimap.getValue(PREFIX_FROM_DATE).isPresent()) {
            String fromDateValue = argMultimap.getValue(PREFIX_FROM_DATE).get();
            ParserUtil.rejectDateExtraTokens(fromDateValue, ViewCommand.MESSAGE_USAGE);
            rangeStartDate = Optional.of(ParserUtil.parseSessionDate(fromDateValue));
        }
        Optional<LocalDate> rangeEndDate = Optional.empty();
        if (argMultimap.getValue(PREFIX_TO_DATE).isPresent()) {
            String toDateValue = argMultimap.getValue(PREFIX_TO_DATE).get();
            ParserUtil.rejectDateExtraTokens(toDateValue, ViewCommand.MESSAGE_USAGE);
            rangeEndDate = Optional.of(ParserUtil.parseSessionDate(toDateValue));
        }
        if (rangeStartDate.isPresent() && rangeEndDate.isPresent()
                && rangeStartDate.get().isAfter(rangeEndDate.get())) {
            throw new ParseException("from/ date cannot be after to/ date.\n" + ViewCommand.MESSAGE_USAGE);
        }

        if (preamble.isBlank()) {
            return new ViewCommand(Optional.empty(), Optional.ofNullable(groupName),
                    Optional.ofNullable(sessionDate), rangeStartDate, rangeEndDate);
        }

        String[] parts = preamble.trim().split("\\s+");
        if (parts.length != 1) {
            throw new ParseException(MESSAGE_TOO_MANY_ARGUMENTS + "\n" + ViewCommand.MESSAGE_USAGE);
        }

        try {
            Attendance attendance = new Attendance(parts[0]);
            return new ViewCommand(Optional.of(attendance), Optional.ofNullable(groupName),
                    Optional.ofNullable(sessionDate), rangeStartDate, rangeEndDate);
        } catch (IllegalArgumentException e) {
            throw new ParseException(MESSAGE_INVALID_ATTENDANCE_STATUS + "\n" + ViewCommand.MESSAGE_USAGE, e);
        }
    }
}
