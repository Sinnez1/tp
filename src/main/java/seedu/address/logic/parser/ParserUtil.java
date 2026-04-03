package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";
    public static final String MESSAGE_INVALID_INDEX_EXPRESSION = "Index expression must be made up of positive "
            + "integers, comma-separated integers, or ranges like 1-3.";

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
     */
    public static LocalDate parseSessionDate(String date)
            throws ParseException {
        requireNonNull(date);
        String trimmedDate = date.trim();

        try {
            return LocalDate.parse(trimmedDate, Session.DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ParseException(Session.MESSAGE_CONSTRAINTS);
            // TODO: Show specific and nicer error messages for user
        }
    }
}
