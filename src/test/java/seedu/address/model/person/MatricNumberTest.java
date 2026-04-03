package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class MatricNumberTest {

    private static final String VALID_MATRIC_NUMBER_1 = "A1111111M";
    private static final String VALID_MATRIC_NUMBER_1_LOWERCASE = "a1111111m";
    private static final String VALID_MATRIC_NUMBER_2 = "A4455667L";

    private static final String INVALID_MATRIC_NUMBER_WRONG_FORMAT_START = "B1234567M";
    private static final String INVALID_MATRIC_NUMBER_WRONG_CHECKSUM = "A1111111A";
    private static final char EXPECTED_CHECKSUM_FOR_INVALID_MATRIC_NUMBER = 'M';

    @Test
    public void constructor_null_throwsNullPointerException() {
        // EP: null input
        assertThrows(NullPointerException.class, () -> new MatricNumber(null));
    }

    @Test
    public void constructor_invalidMatricNumber_throwsIllegalArgumentException() {
        // EP: empty string
        String invalidMatricNumber = "";
        assertThrows(IllegalArgumentException.class, () -> new MatricNumber(invalidMatricNumber));
    }

    @Test
    public void constructor_storesUpperCase_success() {
        // EP: valid matric number in lower case
        MatricNumber matricNumber = new MatricNumber(VALID_MATRIC_NUMBER_1_LOWERCASE);
        assertEquals(VALID_MATRIC_NUMBER_1, matricNumber.value);
    }

    @Test
    public void constructor_invalidChecksum_throwsIllegalArgumentExceptionWithSpecificMessage() {
        // EP: correct format but with wrong checksum
        String expectedMessage = String.format(MatricNumber.MESSAGE_INVALID_CHECKSUM,
                EXPECTED_CHECKSUM_FOR_INVALID_MATRIC_NUMBER);
        assertThrows(IllegalArgumentException.class,
                expectedMessage, () -> new MatricNumber(INVALID_MATRIC_NUMBER_WRONG_CHECKSUM));
    }

    @Test
    public void hasValidMatricNumber() {
        // EP: null matriculation number
        assertThrows(NullPointerException.class, () -> MatricNumber.hasValidFormat(null));
        assertFalse(MatricNumber.hasValidMatricNumber(null));

        // invalid matriculation numbers

        // EP: empty string
        assertFalse(MatricNumber.hasValidFormat(""));
        // EP: whitespace
        assertFalse(MatricNumber.hasValidFormat(" "));

        // EP: starts with `B`, instead of 'A' or 'a'
        assertFalse(MatricNumber.hasValidFormat(INVALID_MATRIC_NUMBER_WRONG_FORMAT_START));
        assertFalse(MatricNumber.hasValidMatricNumber(INVALID_MATRIC_NUMBER_WRONG_FORMAT_START));

        // EP: numbers only
        assertFalse(MatricNumber.hasValidFormat("1234567"));

        // BVA: has 8 digits
        assertFalse(MatricNumber.hasValidFormat("A12345678M"));

        // BVA: has 6 digits
        assertFalse(MatricNumber.hasValidFormat("A123456Z"));

        // BVA: only 1 digit
        assertFalse(MatricNumber.hasValidFormat("A0N"));

        // BVA: no digits
        assertFalse(MatricNumber.hasValidFormat("AZ"));

        // EP: valid numbers but with trailing / leading / internal space -> is invalid
        assertFalse(MatricNumber.hasValidFormat(VALID_MATRIC_NUMBER_1 + " ")); // trailing space
        assertFalse(MatricNumber.hasValidFormat(" " + VALID_MATRIC_NUMBER_1)); // leading space
        assertFalse(MatricNumber.hasValidFormat("A1234 567N")); // space in the middle

        // EP: correct format but with wrong checksums
        assertFalse(MatricNumber.hasValidMatricNumber("A1111111A")); // wrong checksum, should be 'M'
        assertFalse(MatricNumber.hasValidMatricNumber("A0388420K")); // wrong checksum, should be 'B'
        assertFalse(MatricNumber.hasValidMatricNumber("A2222222A")); // wrong checksum, should be 'B'

        // valid matriculation numbers

        // EP: valid in lowercase
        assertTrue(MatricNumber.hasValidMatricNumber(VALID_MATRIC_NUMBER_1_LOWERCASE));

        // EP: valid in uppercase
        assertTrue(MatricNumber.hasValidMatricNumber(VALID_MATRIC_NUMBER_1));
        assertTrue(MatricNumber.hasValidMatricNumber(VALID_MATRIC_NUMBER_2));

        // EP: valid format only (checksum may be incorrect)
        assertTrue(MatricNumber.hasValidFormat("A4433221B"));
        assertTrue(MatricNumber.hasValidFormat("A0000000A")); // BVA: all digits are 0

        // EP: format is correct if it starts with lower capital `a`
        assertTrue(MatricNumber.hasValidFormat("a4455667L"));

        // EP: format is correct if it ends with lower capital `l'
        assertTrue(MatricNumber.hasValidFormat("A4455667l"));

        // EP: format is correct if it starts and ends with lower capitals
        assertTrue(MatricNumber.hasValidFormat("a4455667l"));
    }

    @Test
    public void hasValidMatricNumber_null_returnsFalse() {
        // EP: null matric number
        assertFalse(MatricNumber.hasValidMatricNumber(null));
    }

    @Test
    public void calculateChecksum_sumIsZero_givesCorrectChecksum() {
        // BVA: all digits 0 with correct checksum
        assertTrue(MatricNumber.hasValidMatricNumber("A0000000Y"));
    }

    @Test
    public void calculateChecksum_allNines_givesCorrectChecksum() {
        // BVA: all digits 9 with correct checksum
        assertTrue(MatricNumber.hasValidMatricNumber("A9999999W"));
    }

    @Test
    public void calculateCheckSum_lowerCaseStart_givesCorrectCheckSum() {
        // EP: starts with lower capital 'a' with is a valid matric number
        assertTrue(MatricNumber.hasValidMatricNumber("a0000000Y"));
    }

    @Test
    public void calculateCheckSum_lowerCaseEnd_givesCorrectCheckSum() {
        // EP: ends with lower capital 'y' with is a valid matric number
        assertTrue(MatricNumber.hasValidMatricNumber("A0000000y"));
    }



    @Test
    public void validateMatricNumber_triggersLoggingMethods() {
        // EP: wrong format (starts with B) but has correct checksum
        assertThrows(IllegalArgumentException.class, () -> new MatricNumber("B1234567X")); // Triggers logFormatError.

        // EP: correct format but with wrong checksum
        assertThrows(IllegalArgumentException.class, () -> new MatricNumber("A1111111A")); // Triggers logChecksumError.
    }

    @Test
    public void equals() {
        MatricNumber matricNumber = new MatricNumber(VALID_MATRIC_NUMBER_1);

        // EP: same values -> returns true
        assertTrue(matricNumber.equals(new MatricNumber(VALID_MATRIC_NUMBER_1)));

        // EP: different case -> returns true
        assertTrue(matricNumber.equals(new MatricNumber(VALID_MATRIC_NUMBER_1_LOWERCASE)));

        // EP: same object -> returns true
        assertTrue(matricNumber.equals(matricNumber));

        // EP: null -> returns false
        assertFalse(matricNumber.equals(null));

        // EP: different types -> returns false
        assertFalse(matricNumber.equals(5.0f));

        // EP: different values -> returns false
        assertFalse(matricNumber.equals(new MatricNumber(VALID_MATRIC_NUMBER_2)));
    }

    @Test
    public void hashCode_test() {
        MatricNumber matricNumber1 = new MatricNumber(VALID_MATRIC_NUMBER_1);
        MatricNumber matricNumber2 = new MatricNumber(VALID_MATRIC_NUMBER_1_LOWERCASE);

        // EP: same matric number, different case -> same hashcode
        assertEquals(matricNumber1.hashCode(), matricNumber2.hashCode());

        // EP: different matric number with different hashcode
        MatricNumber matricNumber3 = new MatricNumber(VALID_MATRIC_NUMBER_2);
        assertNotEquals(matricNumber1.hashCode(), matricNumber3.hashCode());
    }
}
