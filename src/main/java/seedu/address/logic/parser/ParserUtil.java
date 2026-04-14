package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Email;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Session;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be made up of a positive integer.";
    public static final String MESSAGE_INVALID_INDEX_EXPRESSION = "Index expression must be made up of positive "
            + "integers, comma-separated integers, or ranges like 1-3.";

    private static final String DATE_PATTERN_NO_HYPHENS = "\\d{8}";
    private static final String DATE_PATTERN_DAY_FIRST_SLASHES = "\\d{2}/\\d{2}/\\d{4}";
    private static final String DATE_PATTERN_DAY_FIRST_HYPHENS = "\\d{2}-\\d{2}-\\d{4}";
    private static final String DATE_PATTERN_YYYY_MM_DD = "\\d{4}-\\d{2}-\\d{2}";
    private static final String DATE_FIELD_SEPARATOR = "-";
    private static final int MONTH_MIN = 1;
    private static final int MONTH_MAX = 12;
    private static final int DAY_MIN = 1;

    private static final int DATE_NO_HYPHENS_YEAR_START = 0;
    private static final int DATE_NO_HYPHENS_YEAR_END = 4;
    private static final int DATE_NO_HYPHENS_MONTH_START = 4;
    private static final int DATE_NO_HYPHENS_MONTH_END = 6;
    private static final int DATE_NO_HYPHENS_DAY_START = 6;
    private static final int DATE_NO_HYPHENS_DAY_END = 8;

    private static final Logger logger = LogsCenter.getLogger(ParserUtil.class);

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses an index expression such as {@code 1}, {@code 1,2,3}, or {@code 1-3,5}.
     */
    public static List<Index> parseIndexes(String indexExpression) throws ParseException {
        requireNonNull(indexExpression);
        String trimmedExpression = indexExpression.trim();
        if (trimmedExpression.isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_INDEX_EXPRESSION);
        }

        TreeSet<Integer> resolvedIndexes = new TreeSet<>();
        String[] tokens = trimmedExpression.split(",");
        for (String token : tokens) {
            String trimmedToken = token.trim();
            if (trimmedToken.isEmpty()) {
                throw new ParseException(MESSAGE_INVALID_INDEX_EXPRESSION);
            }

            if (trimmedToken.contains("-")) {
                String[] bounds = trimmedToken.split("-");
                if (bounds.length != 2 || !StringUtil.isNonZeroUnsignedInteger(bounds[0].trim())
                        || !StringUtil.isNonZeroUnsignedInteger(bounds[1].trim())) {
                    throw new ParseException(MESSAGE_INVALID_INDEX_EXPRESSION);
                }

                int start = Integer.parseInt(bounds[0].trim());
                int end = Integer.parseInt(bounds[1].trim());
                if (start > end) {
                    throw new ParseException(MESSAGE_INVALID_INDEX_EXPRESSION);
                }
                for (int i = start; i <= end; i++) {
                    resolvedIndexes.add(i);
                }
            } else {
                if (!StringUtil.isNonZeroUnsignedInteger(trimmedToken)) {
                    throw new ParseException(MESSAGE_INVALID_INDEX_EXPRESSION);
                }
                resolvedIndexes.add(Integer.parseInt(trimmedToken));
            }
        }

        List<Index> result = new ArrayList<>();
        resolvedIndexes.forEach(index -> result.add(Index.fromOneBased(index)));
        return result;
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }


    /**
     * Parses an assignment name.
     */
    public static AssignmentName parseAssignmentName(String assignmentName) throws ParseException {
        requireNonNull(assignmentName);
        String trimmedAssignmentName = assignmentName.trim();
        if (!AssignmentName.isValidAssignmentName(trimmedAssignmentName)) {
            throw new ParseException(AssignmentName.MESSAGE_CONSTRAINTS);
        }
        return new AssignmentName(trimmedAssignmentName);
    }

    /**
     * Parses max marks as a positive integer.
     */
    public static int parseMaxMarks(String maxMarks) throws ParseException {
        requireNonNull(maxMarks);
        String trimmedMaxMarks = maxMarks.trim();
        try {
            int parsedMaxMarks = Integer.parseInt(trimmedMaxMarks);
            if (!Assignment.isValidMaxMarks(parsedMaxMarks)) {
                throw new ParseException(Assignment.MESSAGE_MAX_MARKS_CONSTRAINTS);
            }
            return parsedMaxMarks;
        } catch (NumberFormatException e) {
            throw new ParseException(Assignment.MESSAGE_MAX_MARKS_CONSTRAINTS);
        }
    }

    /**
     * Parses a grade as a non-negative integer.
     */
    public static int parseGrade(String grade) throws ParseException {
        requireNonNull(grade);
        String trimmedGrade = grade.trim();
        try {
            int parsedGrade = Integer.parseInt(trimmedGrade);
            if (parsedGrade < 0) {
                throw new ParseException("Grade should be a non-negative integer.");
            }
            return parsedGrade;
        } catch (NumberFormatException e) {
            throw new ParseException("Grade should be a non-negative integer.");
        }
    }
    /**
     * Parses a group name.
     */
    public static GroupName parseGroupName(String groupName) throws ParseException {
        requireNonNull(groupName);
        String trimmedGroupName = groupName.trim();
        if (!GroupName.isValidGroupName(trimmedGroupName)) {
            throw new ParseException(GroupName.MESSAGE_CONSTRAINTS);
        }
        return new GroupName(trimmedGroupName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String matriculation number} into an {@code MatricNumber}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code matriculation number} is invalid.
     */
    public static MatricNumber parseMatricNumber(String matricNumber) throws ParseException {
        requireNonNull(matricNumber);
        String trimmedMatricNumber = matricNumber.trim();
        try {
            return new MatricNumber(trimmedMatricNumber);
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage());
        }
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.getDiagnosticMessage(trimmedEmail));
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses a date string in yyyy-MM-dd format.
     *
     * @param date The date to parse.
     * @return the Date in {@code LocalDate}.
     * @throws ParseException if input is not in valid yyyy-mm-dd format.
     */
    public static LocalDate parseSessionDate(String date)
            throws ParseException {
        requireNonNull(date);
        String trimmedDate = date.trim();
        if (trimmedDate.isEmpty()) {
            throw new ParseException(Session.BLANK_DATE_MESSAGE);
        }
        try {
            return LocalDate.parse(trimmedDate, Session.DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            String errorMessage = getDateErrorMessage(trimmedDate);
            logger.warning("Invalid date input: " + trimmedDate + " -- " + errorMessage);
            throw new ParseException(errorMessage);
        }
    }

    /**
     * Rejects a date string that contains trailing tokens.
     *
     * @param value Date string value from argument multimap.
     * @param commandUsage Usage message of command, used for error message.
     * @throws ParseException If date string has a space after trimming.
     */
    public static void rejectDateExtraTokens(String value, String commandUsage) throws ParseException {
        if (value.trim().contains(" ")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, commandUsage));
        }
    }

    /**
     * Returns a specific error message explaining why the input is not a valid yyyy-MM-dd date.
     * Falls back to {@link Session#MESSAGE_CONSTRAINTS} if no specific reason applies.
     */
    private static String getDateErrorMessage(String input) {
        assert input != null;

        if (!hasCorrectDateStructure(input)) {
            return getStructuralErrorMessage(input);
        }
        return getCalendarErrorMessage(input);
    }

    /**
     * Returns true if the input matches the yyyy-MM-dd skeleton (digits and hyphens only).
     */
    private static boolean hasCorrectDateStructure(String input) {
        return input.matches(DATE_PATTERN_YYYY_MM_DD);
    }

    /**
     * Returns an error message for inputs that fail the structural yyyy-MM-dd check.
     * Recognizes formats like "dd-mm-yyyy" and "yyyymmdd" and suggests a correction where possible.
     */
    private static String getStructuralErrorMessage(String input) {
        if (hasMissingHyphens(input)) {
            return "Date format is incorrect. Did you mean " + insertHyphens(input) + "? "
                    + Session.MESSAGE_CONSTRAINTS;
        }
        if (hasDayAtStartOfDateString(input)) {
            return "Date appears to be in day-month-year order. "
                    + "Please use year-month-day instead. "
                    + Session.MESSAGE_CONSTRAINTS;
        }
        return Session.MESSAGE_CONSTRAINTS;
    }

    /**
     * Returns true if input looks like a date with hyphens omitted, e.g. {"20260316"}.
     */
    private static boolean hasMissingHyphens(String input) {
        return input.matches(DATE_PATTERN_NO_HYPHENS);
    }

    /**
     * Returns true if input looks like a dd/MM/yyyy or dd-MM-yyyy date.
     */
    private static boolean hasDayAtStartOfDateString(String input) {
        return input.matches(DATE_PATTERN_DAY_FIRST_HYPHENS) || input.matches(DATE_PATTERN_DAY_FIRST_SLASHES);
    }

    /**
     * Inserts hyphens into an 8-digit string to produce a yyyy-MM-dd suggestion.
     * Precondition: input matches a "yyyymmdd" format.
     */
    private static String insertHyphens(String input) {
        assert input.matches(DATE_PATTERN_NO_HYPHENS);
        return input.substring(DATE_NO_HYPHENS_YEAR_START, DATE_NO_HYPHENS_YEAR_END) + "-"
                + input.substring(DATE_NO_HYPHENS_MONTH_START, DATE_NO_HYPHENS_MONTH_END) + "-"
                + input.substring(DATE_NO_HYPHENS_DAY_START, DATE_NO_HYPHENS_DAY_END);
    }

    /**
     * Returns a specific error message explaining why the input is not a valid yyyy-MM-dd date.
     * Assumes input has already been trimmed.
     */
    private static String getCalendarErrorMessage(String input) {
        String[] parts = input.split(DATE_FIELD_SEPARATOR);
        try {
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            if (!isValidMonth(month)) {
                return getInvalidMonthMessage(month);
            }
            if (!isValidDay(year, month, day)) {
                return getInvalidDayMessage(year, month, day);
            }
        } catch (NumberFormatException | DateTimeException e) {
            assert false : "Unexpected parse failure for structurally valid date: " + input;
        }

        assert false : "Date passed structural and calendar checks but was still invalid: " + input;
        return Session.MESSAGE_CONSTRAINTS;
    }

    /**
     * Returns true if month is within the valid range 1–12.
     */
    private static boolean isValidMonth(int month) {
        return month >= MONTH_MIN && month <= MONTH_MAX;
    }

    /**
     * Returns true if day is a valid day for the given month and year.
     */
    private static boolean isValidDay(int year, int month, int day) {
        return day >= DAY_MIN && day <= YearMonth.of(year, month).lengthOfMonth();
    }

    /**
     * Returns an error message for an out-of-range month value.
     */
    private static String getInvalidMonthMessage(int month) {
        return String.format("Invalid month: %d. Month must be between %02d and %02d.",
                month, MONTH_MIN, MONTH_MAX);
    }

    /**
     * Returns an error message for an out-of-range day value, naming the month and its actual length.
     */
    private static String getInvalidDayMessage(int year, int month, int day) {
        YearMonth ym = YearMonth.of(year, month);
        int daysInMonth = ym.lengthOfMonth();
        String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return String.format("Invalid day: %d. %s %d only has %d days.", day, monthName, year, daysInMonth);
    }
}
