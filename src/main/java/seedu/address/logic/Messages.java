package seedu.address.logic;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d persons listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
            "Multiple values specified for the following single-valued field(s): ";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        if (duplicateFields.size() == 1) {
            return "There should only be one of this prefix: "
                    + duplicateFields.iterator().next();
        }

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for generic display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("\nPhone: ")
                .append(person.getPhone())
                .append("\nMatric Number: ")
                .append(person.getMatricNumber())
                .append("\nEmail: ")
                .append(person.getEmail());
        return appendTagsAndGroups(builder, person);
    }

    /**
     * Formats the {@code person} for a specific group and session date.
     */
    public static String format(Person person, GroupName groupName, LocalDate date) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("\nMatric Number: ")
                .append(person.getMatricNumber())
                .append("\nAttendance: ")
                .append(person.getAttendance(groupName, date))
                .append("\nParticipation: ")
                .append(person.getParticipation(groupName, date));
        return builder.toString();
    }

    private static String appendTagsAndGroups(StringBuilder builder, Person person) {
        builder.append("\nTags: ");
        person.getTags().forEach(builder::append);

        builder.append("\nGroups: ");
        builder.append(person.getGroups().stream()
                .sorted(Comparator.comparing(groupName -> groupName.value, String.CASE_INSENSITIVE_ORDER))
                .map(groupName -> groupName.value)
                .collect(Collectors.joining(", ")));
        return builder.toString();
    }

}
