package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;

/**
 * Represents a Person's matriculation number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #hasValidMatricNumber(String)}
 */
public class MatricNumber {

    public static final String MESSAGE_CONSTRAINTS = "Matriculation numbers should start with `A`,"
            + " followed by 7 digits and end with a valid checksum letter.";

    public static final String MESSAGE_INVALID_CHECKSUM =
            "The matriculation number checksum letter is incorrect. For the given digits, it should be '%c'.";

    /*
     * The first character of the matriculation number must be the alphabet 'A'.
     */
    public static final String VALIDATION_REGEX = "^[aA]\\d{7}[a-zA-Z]$";
    private static final int CHECKSUM_POSITION = 8;
    private static final int DIGITS_START_INDEX = 2;
    private static final int DIGITS_END_INDEX = 8;
    private static final int EXPECTED_CHECKSUM_DIGITS_LENGTH = 6;
    private static final String CHECKSUM_LETTERS = "YXWURNMLJHEAB";
    private static final int EXPECTED_CHECKSUM_LETTERS_LENGTH = 13;
    private static final int EXPECTED_MATRIC_NUMBER_LENGTH = 9;
    private static final Logger logger = LogsCenter.getLogger(MatricNumber.class);
    public final String value;

    /**
     * Constructs a {@code MatricNumber}.
     *
     * @param matricNumber A valid matriculation number.
     */
    public MatricNumber(String matricNumber) {
        requireNonNull(matricNumber);
        String upperCaseMatricNumber = matricNumber.toUpperCase();
        validateMatricNumber(upperCaseMatricNumber);
        value = upperCaseMatricNumber;
        logCreationSuccess();
    }

    /**
     * Returns true if matriculation number has a valid format.
     *
     * @param matricNumber Matriculation number to be tested.
     * @return True if matriculation number is valid.
     */
    public static boolean hasValidFormat(String matricNumber) {
        return matricNumber.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if matriculation number has a valid checksum letter and format.
     *
     * @param matricNumber Matriculation number to be tested.
     * @return True if matriculation number is valid.
     */
    public static boolean hasValidMatricNumber(String matricNumber) {
        return hasNonNullMatricNumber(matricNumber) && hasValidFormat(matricNumber) && hasValidChecksum(matricNumber);
    }

    private static boolean hasNonNullMatricNumber(String matricNumber) {
        return matricNumber != null;
    }

    private static boolean hasValidChecksum(String matricNumber) {
        return calculateChecksum(matricNumber) == extractProvidedChecksum(matricNumber);
    }

    private static char extractProvidedChecksum(String matricNumber) {
        return Character.toUpperCase(matricNumber.charAt(CHECKSUM_POSITION));
    }

    private static String getChecksumErrorMessage(String matricNumber) {
        return String.format(MESSAGE_INVALID_CHECKSUM, calculateChecksum(matricNumber));
    }

    private static void validateMatricNumber(String matricNumber) {
        if (!hasValidFormat(matricNumber)) {
            logFormatError(matricNumber);
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        if (!hasValidChecksum(matricNumber)) {
            logChecksumError(matricNumber);
            throw new IllegalArgumentException(getChecksumErrorMessage(matricNumber));
        }
    }
    /**
     * Calculates the checksum character for NUS matriculation numbers.
     *
     * @param matricNumber Matriculation number with "A" and 7 digits.
     * @return Checksum character for matriculation number.
     */
    private static char calculateChecksum(String matricNumber) {
        assert matricNumber.length() == EXPECTED_MATRIC_NUMBER_LENGTH
                : "Matriculation number length should be 9";
        String digits = matricNumber.substring(DIGITS_START_INDEX, DIGITS_END_INDEX);
        assert digits.length() == EXPECTED_CHECKSUM_DIGITS_LENGTH
                : "Mismatch in length between expected length and extracted digits";
        int length = CHECKSUM_LETTERS.length();
        assert length == EXPECTED_CHECKSUM_LETTERS_LENGTH : "Checksum letters string has 13 characters";
        int sum = 0;

        for (int i = 0; i < digits.length(); i++) {
            sum += Character.getNumericValue(digits.charAt(i));
        }

        int remainder = sum % length;
        assert remainder >= 0 && remainder < length : "Calculated remainder is out of bounds";

        return CHECKSUM_LETTERS.charAt(remainder);
    }

    private void logCreationSuccess() {
        logger.fine("Successfully created MatricNumber: " + value);
    }

    private static void logFormatError(String matricNumber) {
        logger.warning("Failed creation: Invalid format for MatricNumber: '" + matricNumber + "'");
    }

    private static void logChecksumError(String matricNumber) {
        logger.warning("Failed creation: Checksum mismatch for MatricNumber: '" + matricNumber + "'");
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
        if (!(other instanceof MatricNumber)) {
            return false;
        }

        MatricNumber otherMatricNumber = (MatricNumber) other;
        return value.equals(otherMatricNumber.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
