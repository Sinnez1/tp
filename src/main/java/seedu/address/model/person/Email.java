package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;

/**
 * Represents a Person's email in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidEmail(String)}
 */
public class Email {
    public static final String MULTIPLE_AT_SYMBOL_MESSAGE = "Email contains more than one '@'. "
            + "There should only be one.";
    public static final String MESSAGE_MISSING_LOCAL_PART = "Email is missing a local-part before '@'. "
            + "It should be in the format local-part@domain.";
    public static final String MESSAGE_LOCAL_PART_INVALID_START = "The local-part (before '@') must start with an "
            + "alphanumeric character, not '%s'.";
    public static final String MESSAGE_LOCAL_PART_INVALID_END = "The local-part (before '@') must not end with a "
            + "special character like '%s'.";
    public static final String MESSAGE_DOMAIN_CONSECUTIVE_PERIODS = "The domain contains consecutive '.' separators, "
            + "which is not allowed.";
    public static final String MESSAGE_DOMAIN_LABEL_INVALID = "The domain label '%s' is invalid. "
            + "Each label must start and end with "
            + "an alphanumeric character and may only contain hyphens in between.";
    public static final char AT_SYMBOL = '@';
    public static final int MIN_DOMAIN_LAST_PART_LENGTH = 2;
    public static final int MAX_LENGTH = 320;
    public static final String SPECIAL_CHARACTERS = "+_.-";

    public static final String EMAIL_FORMAT_EXAMPLE = "(e.g. johndoe@example.com)";
    public static final String EMAIL_FORMAT_HINT = "It should be in the format local-part@domain "
            + EMAIL_FORMAT_EXAMPLE;
    // Error messages
    public static final String MESSAGE_EMAIL_TOO_LONG = "Email must not exceed " + MAX_LENGTH + " characters.";
    public static final String EMPTY_EMAIL_MESSAGE = "Email cannot be empty. " + EMAIL_FORMAT_HINT;
    public static final String MESSAGE_MISSING_DOMAIN = "Email is missing a domain after '@'. " + EMAIL_FORMAT_HINT;
    public static final String MESSAGE_DOMAIN_ENDS_WITH_PERIOD = "The domain must not end with a '.'.";

    public static final String MISSING_AT_SYMBOL_MESSAGE = "Email is missing '@'. " + EMAIL_FORMAT_HINT;
    public static final String MESSAGE_LOCAL_PART_INVALID_CHARS = "The local-part (before '@') contains invalid "
            + "characters. Only alphanumeric characters and "
            + SPECIAL_CHARACTERS + " are allowed, and special characters cannot be consecutive.";
    public static final String MESSAGE_DOMAIN_TLD_SHORT = "The domain's last part "
            + "(e.g. 'com' in 'example.com') must be at least "
            + MIN_DOMAIN_LAST_PART_LENGTH + " characters long.";

    public static final String MESSAGE_CONSTRAINTS = "Emails should be of the format local-part@domain "
            + "and adhere to the following constraints:\n"
            + "1. The local-part should only contain alphanumeric characters and these special characters, excluding "
            + "the parentheses, (" + SPECIAL_CHARACTERS + "). The local-part may not start or end with any special "
            + "characters.\n"
            + "2. This is followed by a '@' and then a domain name. The domain name is made up of domain labels "
            + "separated by periods.\n"
            + "The domain name must:\n"
            + "    - end with a domain label at least 2 characters long\n"
            + "    - have each domain label start and end with alphanumeric characters\n"
            + "    - have each domain label consist of alphanumeric characters, separated only by hyphens, if any.";

    // alphanumeric and special characters
    private static final String ALPHANUMERIC_NO_UNDERSCORE = "[^\\W_]+"; // alphanumeric characters except underscore
    private static final String LOCAL_PART_REGEX = "^" + ALPHANUMERIC_NO_UNDERSCORE + "([" + SPECIAL_CHARACTERS + "]"
            + ALPHANUMERIC_NO_UNDERSCORE + ")*";
    private static final String DOMAIN_PART_REGEX = ALPHANUMERIC_NO_UNDERSCORE
            + "(-" + ALPHANUMERIC_NO_UNDERSCORE + ")*";
    private static final String DOMAIN_LAST_PART_REGEX = "(" + DOMAIN_PART_REGEX + "){2,}$"; // At least two chars
    private static final String DOMAIN_REGEX = "(" + DOMAIN_PART_REGEX + "\\.)*" + DOMAIN_LAST_PART_REGEX;

    public static final String VALIDATION_REGEX = LOCAL_PART_REGEX + "@" + DOMAIN_REGEX;

    private static final Logger logger = LogsCenter.getLogger(Email.class);
    public final String value;
    /**
     * Constructs an {@code Email}.
     *
     * @param email A valid email address.
     */
    public Email(String email) {
        requireNonNull(email);
        checkArgument(isValidEmail(email), getDiagnosticMessage(email));
        value = email.toLowerCase();
        logger.fine("Created Email: " + value);
    }

    /**
     * Returns if a given string is a valid email.
     *
     * @param test Email to be tested.
     */
    public static boolean isValidEmail(String test) {
        if (isExceedingMaxLength(test)) {
            return false;
        }
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns a specific error message describing why the given email is invalid.
     * Assumes the email has already failed {@link #isValidEmail(String)}.
     *
     * @param email {@code Email} that is input by user.
     * @return Error message if email is invalid.
     */
    public static String getDiagnosticMessage(String email) {
        assert email != null : "getDiagnosticMessage should not be called with null";
        logger.fine("Diagnosing invalid email: '" + email + "'");

        if (isExceedingMaxLength(email)) {
            return MESSAGE_EMAIL_TOO_LONG;
        }

        String atSymbolError = checkAtSymbolErrors(email);
        if (atSymbolError != null) {
            return atSymbolError;
        }
        return checkEmailComponents(email);
    }

    private static String checkEmailComponents(String email) {
        String localPart = extractLocalPart(email);

        String localPartError = checkLocalPart(localPart);
        if (localPartError != null) {
            return localPartError;
        }
        String domain = extractDomain(email);
        String domainError = checkDomain(domain);
        if (domainError != null) {
            return domainError;
        }

        return MESSAGE_CONSTRAINTS;
    }

    private static boolean isExceedingMaxLength(String email) {
        return email.length() > MAX_LENGTH;
    }

    private static boolean isEmailEmpty(String email) {
        return email.isEmpty();
    }

    private static boolean isMissingAtSymbol(String email) {
        return email.indexOf(AT_SYMBOL) == -1;
    }

    private static boolean hasMultipleAtSymbols(String email) {
        return email.chars().filter(c -> c == AT_SYMBOL).count() > 1;
    }

    private static String checkAtSymbolErrors(String email) {
        if (isEmailEmpty(email)) {
            return EMPTY_EMAIL_MESSAGE;
        }
        if (isMissingAtSymbol(email)) {
            return MISSING_AT_SYMBOL_MESSAGE;
        }
        if (hasMultipleAtSymbols(email)) {
            return MULTIPLE_AT_SYMBOL_MESSAGE;
        }
        return null;
    }

    private static String extractLocalPart(String email) {
        assert email.indexOf(AT_SYMBOL) != -1 : "extractLocalPart called before @ presence was confirmed";
        return email.substring(0, email.indexOf(AT_SYMBOL));
    }

    private static String extractDomain(String email) {
        assert email.indexOf(AT_SYMBOL) != -1 : "extractDomain called before @ presence was confirmed";
        return email.substring(email.indexOf(AT_SYMBOL) + 1);
    }

    private static String checkLocalPart(String localPart) {
        if (isLocalPartEmpty(localPart)) {
            return MESSAGE_MISSING_LOCAL_PART;
        }
        if (!startsWithAlphanumeric(localPart)) {
            return String.format(MESSAGE_LOCAL_PART_INVALID_START, getFirstCharacter(localPart));
        }
        if (endsWithSpecialCharacter(localPart)) {
            return String.format(MESSAGE_LOCAL_PART_INVALID_END, getLastCharacter(localPart));
        }
        if (!isValidLocalPart(localPart)) {
            return MESSAGE_LOCAL_PART_INVALID_CHARS;
        }
        return null;
    }

    private static boolean isLocalPartEmpty(String localPart) {
        return localPart.isEmpty();
    }

    private static boolean isValidLocalPart(String localPart) {
        return localPart.matches(LOCAL_PART_REGEX);
    }

    private static char getFirstCharacter(String string) {
        return string.charAt(0);
    }

    private static char getLastCharacter(String string) {
        return string.charAt(string.length() - 1);
    }

    private static boolean startsWithAlphanumeric(String localPart) {
        return String.valueOf(getFirstCharacter(localPart)).matches(ALPHANUMERIC_NO_UNDERSCORE);
    }
    private static boolean endsWithSpecialCharacter(String localPart) {
        return SPECIAL_CHARACTERS.contains(String.valueOf(getLastCharacter(localPart)));
    }

    private static String checkDomain(String domain) {
        if (isDomainEmpty(domain)) {
            return MESSAGE_MISSING_DOMAIN;
        }
        if (isDomainEndingWithADot(domain)) {
            return MESSAGE_DOMAIN_ENDS_WITH_PERIOD;
        }
        return checkDomainLabels(splitIntoDomain(domain));
    }

    private static boolean isDomainEmpty(String domain) {
        return domain.isEmpty();
    }

    private static boolean isDomainEndingWithADot(String domain) {
        return domain.endsWith(".");
    }

    private static String[] splitIntoDomain(String domain) {
        return domain.split("\\.", -1);
    }

    private static String checkDomainLabels(String[] labels) {
        assert labels.length > 0 : "checkDomainLabels should not be called with empty labels array";
        if (isTldShort(labels)) {
            return MESSAGE_DOMAIN_TLD_SHORT;
        }
        for (String label : labels) {
            String err = checkSingleDomainLabel(label);
            if (err != null) {
                return err;
            }
        }
        return null;
    }

    private static boolean isTldShort(String[] labels) {
        return labels[labels.length - 1].length() < MIN_DOMAIN_LAST_PART_LENGTH;
    }

    private static String checkSingleDomainLabel(String label) {
        if (label.isEmpty()) {
            return MESSAGE_DOMAIN_CONSECUTIVE_PERIODS;
        }
        if (!label.matches(DOMAIN_PART_REGEX)) {
            return String.format(MESSAGE_DOMAIN_LABEL_INVALID, label);
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Email)) {
            return false;
        }

        Email otherEmail = (Email) other;
        return value.equalsIgnoreCase(otherEmail.value);
    }

    @Override
    public int hashCode() {
        return value.toLowerCase().hashCode();
    }

}
