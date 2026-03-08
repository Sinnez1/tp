package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class MatricNumberTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MatricNumber(null));
    }

    @Test
    public void constructor_invalidMatricNumber_throwsIllegalArgumentException() {
        String invalidMatricNumber = "";
        assertThrows(IllegalArgumentException.class, () -> new MatricNumber(invalidMatricNumber));
    }

    @Test
    public void isValidMatricNumber() {
        // null matriculation number
        assertThrows(NullPointerException.class, () -> MatricNumber.isValidMatricNumber(null));

        // invalid matriculation numbers
        assertFalse(MatricNumber.isValidMatricNumber("")); // empty string
        assertFalse(MatricNumber.isValidMatricNumber(" ")); // spaces only
        assertFalse(MatricNumber.isValidMatricNumber("B1234567N")); //starts with `B`
        assertFalse(MatricNumber.isValidMatricNumber("1234567")); //only numbers
        assertFalse(MatricNumber.isValidMatricNumber("A12345678Z")); //has 8 digits
        assertFalse(MatricNumber.isValidMatricNumber("A123456Z")); //has 6 digits
        assertFalse(MatricNumber.isValidMatricNumber("A0N")); // 1 digit
        assertFalse(MatricNumber.isValidMatricNumber("AZ")); //no digits
        assertFalse(MatricNumber.isValidMatricNumber("A1234567N ")); // trailing space
        assertFalse(MatricNumber.isValidMatricNumber(" A1234567N")); // leading space
        assertFalse(MatricNumber.isValidMatricNumber("A1234 567N")); // space in the middle

        // valid matriculation numbers
        assertTrue(MatricNumber.isValidMatricNumber("A4433221B"));
        assertTrue(MatricNumber.isValidMatricNumber("A0000000A")); // all same digits
        assertTrue(MatricNumber.isValidMatricNumber("A4455667A")); // ends with `A`
        assertTrue(MatricNumber.isValidMatricNumber("a4455667Z")); // starts with lower capital `a`
        assertTrue(MatricNumber.isValidMatricNumber("A4455667z")); // ends with lower capital
        assertTrue(MatricNumber.isValidMatricNumber("a4455667z")); // characters are both in lower capital
    }

    @Test
    public void equals() {
        MatricNumber matricNumber = new MatricNumber("A1111111Z");

        // same values -> returns true
        assertTrue(matricNumber.equals(new MatricNumber("A1111111Z")));

        //different case -> returns true
        assertTrue(matricNumber.equals(new MatricNumber("a1111111z")));

        // same object -> returns true
        assertTrue(matricNumber.equals(matricNumber));

        // null -> returns false
        assertFalse(matricNumber.equals(null));

        // different types -> returns false
        assertFalse(matricNumber.equals(5.0f));

        // different values -> returns false
        assertFalse(matricNumber.equals(new MatricNumber("A1111111X")));
    }
}
