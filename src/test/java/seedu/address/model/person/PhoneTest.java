package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void isValidPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // invalid: empty, whitespace, non-numeric, mixed alpha
        assertFalse(Phone.isValidPhone(""));
        assertFalse(Phone.isValidPhone(" "));
        assertFalse(Phone.isValidPhone("phone"));
        assertFalse(Phone.isValidPhone("9011p041"));

        // invalid: disallowed special characters
        assertFalse(Phone.isValidPhone("9123.4567"));
        assertFalse(Phone.isValidPhone("9123/4567"));

        // invalid: plus sign not at start
        assertFalse(Phone.isValidPhone("91234+567"));
        assertFalse(Phone.isValidPhone("++6591234567"));

        // invalid: leading/trailing separators
        assertFalse(Phone.isValidPhone(" 91234567"));
        assertFalse(Phone.isValidPhone("91234567 "));

        // invalid: BVA 3 consecutive separators
        assertFalse(Phone.isValidPhone("9123   4567"));
        assertFalse(Phone.isValidPhone("+1  (650) 253-0000"));

        // invalid: multiple numbers
        assertFalse(Phone.isValidPhone("91234567, 81234567"));
        assertFalse(Phone.isValidPhone("91234567 / 81234567"));

        // invalid: digit count BVA (min 3, max 20)
        assertFalse(Phone.isValidPhone("91"));
        assertFalse(Phone.isValidPhone("123456789012345678901")); // 21 digits

        // valid: digit count BVA
        assertTrue(Phone.isValidPhone("911")); // 3 digits (min)
        assertTrue(Phone.isValidPhone("12345678901234567890")); // 20 digits (max)

        // valid: BVA 2 consecutive separators
        assertTrue(Phone.isValidPhone("9123  4567"));
        assertTrue(Phone.isValidPhone("+1 (650) 253-0000"));

        // valid: local SG numbers
        assertTrue(Phone.isValidPhone("93121534"));
        assertTrue(Phone.isValidPhone("9312 1534"));
        assertTrue(Phone.isValidPhone("9312-1534"));

        // valid: international numbers
        assertTrue(Phone.isValidPhone("+6591234567"));
        assertTrue(Phone.isValidPhone("+65 9123 4567"));
        assertTrue(Phone.isValidPhone("+44 20 7946 0958"));
    }

    @Test
    public void equals() {
        Phone phone = new Phone("999");

        assertTrue(phone.equals(new Phone("999"))); // same values
        assertTrue(phone.equals(phone)); // same object
        assertFalse(phone.equals(null)); // null
        assertFalse(phone.equals(5.0f)); // different types
        assertFalse(phone.equals(new Phone("995"))); // different values
    }
}
