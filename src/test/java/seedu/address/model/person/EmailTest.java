package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class EmailTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        //EP: null email
        assertThrows(NullPointerException.class, () -> new Email(null));
    }

    @Test
    public void constructor_invalidEmail_throwsIllegalArgumentException() {
        //EP: empty email
        String invalidEmail = "";
        assertThrows(IllegalArgumentException.class, Email.EMPTY_EMAIL_MESSAGE, () -> new Email(invalidEmail));
    }

    @Test
    public void constructor_validEmail_storesValue() {
        // EP: valid email
        Email email = new Email("valid@example.com");
        assertEquals("valid@example.com", email.value);
    }

    @Test
    public void constructor_emailAtMaxLength_success() {
        // BVA: exactly MAX_LENGTH (320) characters -> should succeed
        // local part = 310 chars, "@", domain = "gmail.com" (9 chars) → 310 + 1 + 9 = 320
        String localPart = "a".repeat(310);
        String email = localPart + "@gmail.com";
        assertEquals(Email.MAX_LENGTH, email.length());
        Email result = new Email(email);
        assertEquals(email, result.value);
    }

    @Test
    public void getDiagnosticMessage_invalidEmails_returnsSpecificErrorMessage() {
        // EP: Missing '@' symbol
        assertEquals(Email.MISSING_AT_SYMBOL_MESSAGE, Email.getDiagnosticMessage("peterjackexample.com"));

        // EP: Multiple '@' symbols
        assertEquals(Email.MULTIPLE_AT_SYMBOL_MESSAGE, Email.getDiagnosticMessage("peter@jack@example.com"));

        // EP: Missing local part but '@' is present
        assertEquals(Email.MESSAGE_MISSING_LOCAL_PART, Email.getDiagnosticMessage("@example.com"));

        // EP: Local part starts with '-'
        assertEquals(String.format(Email.MESSAGE_LOCAL_PART_INVALID_START, "-"),
                Email.getDiagnosticMessage("-peterjack@example.com"));

        //EP: Local part starts with space
        assertEquals(String.format(Email.MESSAGE_LOCAL_PART_INVALID_START, " "),
                Email.getDiagnosticMessage(" peterjack@example.com"));

        // Local part ends with hyphen
        assertEquals(String.format(Email.MESSAGE_LOCAL_PART_INVALID_END, "-"),
                Email.getDiagnosticMessage("peterjack-@example.com"));

        // EP: local part contains a space (disallowed character)
        assertEquals(Email.MESSAGE_LOCAL_PART_INVALID_CHARS, Email.getDiagnosticMessage("peter jack@example.com"));

        // EP: local part has consecutive special characters (two dots)
        assertEquals(Email.MESSAGE_LOCAL_PART_INVALID_CHARS, Email.getDiagnosticMessage("peter..jack@example.com"));

        // EP: local part contains a completely disallowed character ('!')
        assertEquals(Email.MESSAGE_LOCAL_PART_INVALID_CHARS, Email.getDiagnosticMessage("peter!jack@example.com"));

        // EP: Missing domain
        assertEquals(Email.MESSAGE_MISSING_DOMAIN, Email.getDiagnosticMessage("peterjack@"));

        // BVA: TLD too short
        assertEquals(Email.MESSAGE_DOMAIN_TLD_SHORT, Email.getDiagnosticMessage("peterjack@example.c"));
        assertEquals(Email.MESSAGE_DOMAIN_TLD_SHORT, Email.getDiagnosticMessage("peterjack@example.co.d"));

        // EP: Domain contains consecutive periods
        assertEquals(Email.MESSAGE_DOMAIN_CONSECUTIVE_PERIODS, Email.getDiagnosticMessage("peterjack@example..com"));

        // EP: domain label starts with hyphen
        assertEquals(String.format(Email.MESSAGE_DOMAIN_LABEL_INVALID, "-example"),
                Email.getDiagnosticMessage("peterjack@-example.com"));

        //EP: domain ends with hyphen
        assertEquals(String.format(Email.MESSAGE_DOMAIN_LABEL_INVALID, "example-"),
                Email.getDiagnosticMessage("peterjack@example-.com"));

        // EP: domain has underscores (invalid character in domain)
        assertEquals(String.format(Email.MESSAGE_DOMAIN_LABEL_INVALID, "exam_ple"),
                Email.getDiagnosticMessage("peterjack@exam_ple.com"));

        //EP: Domain ends with a dot
        assertEquals(Email.MESSAGE_DOMAIN_ENDS_WITH_PERIOD, Email.getDiagnosticMessage("peterjack@example.com."));

        // BVA: 321 characters (MAX_LENGTH + 1)
        String longLocalPart = "a".repeat(311); // 311 + @gmail.com = 321 char.
        String invalidEmail = longLocalPart + "@gmail.com";
        assertEquals(invalidEmail.length(), Email.MAX_LENGTH + 1);
        assertEquals(Email.MESSAGE_EMAIL_TOO_LONG, Email.getDiagnosticMessage(invalidEmail));
    }

    @Test
    public void getDiagnosticMessage_localPartEndsWithPeriod_returnsInvalidEndMessage() {
        // EP: local part ends with '.' — period is in SPECIAL_CHARACTERS, so triggers invalid-end message
        assertEquals(String.format(Email.MESSAGE_LOCAL_PART_INVALID_END, "."),
                Email.getDiagnosticMessage("peterjack.@example.com"));
    }

    @Test
    public void getDiagnosticMessage_localPartEndsWithPlus_returnsInvalidEndMessage() {
        // EP: local part ends with '+' — triggers invalid-end message
        assertEquals(String.format(Email.MESSAGE_LOCAL_PART_INVALID_END, "+"),
                Email.getDiagnosticMessage("peterjack+@example.com"));
    }

    @Test
    public void getDiagnosticMessage_localPartEndsWithUnderscore_returnsInvalidEndMessage() {
        // EP: local part ends with '_' — triggers invalid-end message
        assertEquals(String.format(Email.MESSAGE_LOCAL_PART_INVALID_END, "_"),
                Email.getDiagnosticMessage("peterjack_@example.com"));
    }

    @Test
    public void getDiagnosticMessage_domainLabelWithDollarSign_returnsLabelInvalidMessage() {
        // EP: domain label contains '$' (disallowed, not alphanumeric or hyphen)
        assertEquals(String.format(Email.MESSAGE_DOMAIN_LABEL_INVALID, "exam$ple"),
                Email.getDiagnosticMessage("peterjack@exam$ple.com"));
    }


    @Test
    public void isValidEmail() {
        // EP: null email
        assertThrows(NullPointerException.class, () -> Email.isValidEmail(null));

        // EP: blank email
        assertFalse(Email.isValidEmail("")); // empty string
        assertFalse(Email.isValidEmail(" ")); // spaces only

        // missing parts
        assertFalse(Email.isValidEmail("@example.com")); // missing local part
        assertFalse(Email.isValidEmail("peterjackexample.com")); // missing '@' symbol
        assertFalse(Email.isValidEmail("peterjack@")); // missing domain name

        // invalid parts
        assertFalse(Email.isValidEmail("peterjack@-")); // invalid domain name
        assertFalse(Email.isValidEmail("peterjack@exam_ple.com")); // underscore in domain name
        assertFalse(Email.isValidEmail("peter jack@example.com")); // spaces in local part
        assertFalse(Email.isValidEmail("peterjack@exam ple.com")); // spaces in domain name
        assertFalse(Email.isValidEmail(" peterjack@example.com")); // leading space
        assertFalse(Email.isValidEmail("peterjack@example.com ")); // trailing space
        assertFalse(Email.isValidEmail("peterjack@@example.com")); // double '@' symbol
        assertFalse(Email.isValidEmail("peter@jack@example.com")); // '@' symbol in local part
        assertFalse(Email.isValidEmail("-peterjack@example.com")); // local part starts with a hyphen
        assertFalse(Email.isValidEmail("peterjack-@example.com")); // local part ends with a hyphen
        assertFalse(Email.isValidEmail("peter..jack@example.com")); // local part has two consecutive periods
        assertFalse(Email.isValidEmail("peterjack@example@com")); // '@' symbol in domain name
        assertFalse(Email.isValidEmail("peterjack@.example.com")); // domain name starts with a period
        assertFalse(Email.isValidEmail("peterjack@example.com.")); // domain name ends with a period
        assertFalse(Email.isValidEmail("peterjack@-example.com")); // domain name starts with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example.com-")); // domain name ends with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example.c")); // top level domain has less than two chars

        // valid email
        assertTrue(Email.isValidEmail("PeterJack_1190@example.com")); // underscore in local part
        assertTrue(Email.isValidEmail("PeterJack.1190@example.com")); // period in local part
        assertTrue(Email.isValidEmail("PeterJack+1190@example.com")); // '+' symbol in local part
        assertTrue(Email.isValidEmail("PeterJack-1190@example.com")); // hyphen in local part
        assertTrue(Email.isValidEmail("a@bc")); // minimal
        assertTrue(Email.isValidEmail("test@localhost")); // alphabets only
        assertTrue(Email.isValidEmail("123@145")); // numeric local part and domain name
        assertTrue(Email.isValidEmail("a1+be.d@example1.com")); // mixture of alphanumeric and special characters
        assertTrue(Email.isValidEmail("peter_jack@very-very-very-long-example.com")); // long domain name
        assertTrue(Email.isValidEmail("if.you.dream.it_you.can.do.it@example.com")); // long local part
        assertTrue(Email.isValidEmail("e1234567@u.nus.edu")); // more than one period in domain
        assertTrue(Email.isValidEmail("peter@example.com.de.us.sg")); //multiple domains, all of valid length
    }

    @Test
    public void equals() {
        Email email = new Email("valid@email");

        // EP: same values -> returns true
        assertTrue(email.equals(new Email("valid@email")));

        // EP: same object -> returns true
        assertTrue(email.equals(email));

        // EP: null -> returns false
        assertFalse(email.equals(null));

        // EP: different types -> returns false
        assertFalse(email.equals(5.0f));

        // EP: different values -> returns false
        assertFalse(email.equals(new Email("other.valid@email")));
    }

    @Test
    public void hashCode_equalEmails_returnsSameHashCode() {
        // EP: two equal Email objects must produce the same hash code
        Email email1 = new Email("valid@email");
        Email email2 = new Email("valid@email");
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    public void hashCode_differentEmails_returnsDifferentHashCode() {
        // EP: two unequal emails — hash codes should differ
        Email email1 = new Email("valid@email");
        Email email2 = new Email("other@email");
        assertNotEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    public void hashCode_sameObject_isStable() {
        // EP: calling hashCode twice on the same object should return the same value
        Email email = new Email("valid@email");
        assertEquals(email.hashCode(), email.hashCode());
    }

    @Test
    public void constructor_mixedCaseEmail_normalisesToLowerCase() {
        // EP: mixed case input — should be stored as lowercase
        Email email = new Email("User@Example.COM");
        assertEquals("user@example.com", email.value);
    }

    @Test
    public void equals_sameAddressDifferentCase_returnsTrue() {
        // EP: case-insensitive for local part
        Email localPartLower = new Email("valid@email");
        Email localPartUpper = new Email("Valid@email");
        assertTrue(localPartLower.equals(localPartUpper));

        // EP: case-insensitivity for domain
        Email domainLower = new Email("valid@email");
        Email domainUpper = new Email("Valid@EmaiL");
        assertTrue(domainLower.equals(domainUpper));



    }

    @Test
    public void hashCode_sameAddressDifferentCase_returnsSameHashCode() {
        // EP: equal emails (case-insensitive) must produce the same hash code
        Email lower = new Email("valid@email");
        Email upper = new Email("Valid@Email");
        assertEquals(lower.hashCode(), upper.hashCode());
    }
}
