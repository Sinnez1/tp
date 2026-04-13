package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+()651234";
    private static final String INVALID_MATRIC_NUMBER = "A12345678N";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_MATRIC_NUMBER = "A3214567B";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        long maxIntPlusOne = (long) Integer.MAX_VALUE + 1;
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
                -> ParserUtil.parseIndex(Long.toString(maxIntPlusOne)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parseMatricNumber_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseMatricNumber((String) null));
    }

    @Test
    public void parseMatricNumber_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseMatricNumber(INVALID_MATRIC_NUMBER));
    }

    @Test
    public void parseMatricNumber_validValueWithoutWhitespace_returnsMatricNumber() throws Exception {
        MatricNumber expectedMatricNumber = new MatricNumber(VALID_MATRIC_NUMBER);
        assertEquals(expectedMatricNumber, ParserUtil.parseMatricNumber(VALID_MATRIC_NUMBER));
    }

    @Test
    public void parseMatricNumber_validValueWithWhitespace_returnsTrimmedMatricNumber() throws Exception {
        String matricNumberWithWhitespace = WHITESPACE + VALID_MATRIC_NUMBER + WHITESPACE;
        MatricNumber expectedMatricNumber = new MatricNumber(VALID_MATRIC_NUMBER);
        assertEquals(expectedMatricNumber, ParserUtil.parseMatricNumber(matricNumberWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(Arrays.asList(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(Collections.emptyList()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(Arrays.asList(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = new HashSet<Tag>(Arrays.asList(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2)));

        assertEquals(expectedTagSet, actualTagSet);
    }

    @Test
    public void parseSessionDate_null_throwsNullPointerException() {
        // EP: null date
        assertThrows(NullPointerException.class, () -> ParserUtil.parseSessionDate(null));
    }

    @Test
    public void parseSessionDate_emptyDate_throwsParseException() {
        // EP: empty date
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionDate(""));
    }

    @Test
    public void parseSessionDate_whiteSpaceDate_throwsParseException() {
        // EP: white space
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionDate("    "));
    }

    @Test
    public void parseSessionDate_validDate_success() throws Exception {
        // EP: valid date
        assertEquals(LocalDate.of(2026, 3, 16), ParserUtil.parseSessionDate("2026-03-16"));
    }

    @Test
    public void parseSessionDate_validDateWithWhitespace_returnsTrimmedDate() throws Exception {
        // EP: date with leading and trailing space
        assertEquals(LocalDate.of(2026, 3, 16),
                ParserUtil.parseSessionDate(WHITESPACE + "2026-03-16" + WHITESPACE));
    }

    @Test
    public void parseSessionDate_firstDayOfYear_success() throws Exception {
        // BVA: 1st Jan
        assertEquals(LocalDate.of(2026, 1, 1), ParserUtil.parseSessionDate("2026-01-01"));
    }

    @Test
    public void parseSessionDate_lastDayOfYear_success() throws Exception {
        // BVA: 31 Dec
        assertEquals(LocalDate.of(2026, 12, 31), ParserUtil.parseSessionDate("2026-12-31"));
    }

    @Test
    public void parseSessionDate_boundaryDayJanuary31_success() throws Exception {
        // BVA: 31-day month
        assertEquals(LocalDate.of(2026, 1, 31), ParserUtil.parseSessionDate("2026-01-31"));
    }

    @Test
    public void parseSessionDate_jan32_throwsParseException() {
        // BVA: 32 Jan
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionDate("2026-01-32"));
    }

    @Test
    public void parseSessionDate_boundaryDayApril30_success() throws Exception {
        // BVA: 30-day month
        assertEquals(LocalDate.of(2026, 4, 30), ParserUtil.parseSessionDate("2026-04-30"));
    }

    @Test
    public void parseSessionDate_apr31_throwsParseException() {
        // BVA: 31 April
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionDate("2026-04-31"));
    }

    @Test
    public void parseSessionDate_feb28NonLeapYear_success() throws Exception {
        // BVA: feb 28 on non leap-year
        assertEquals(LocalDate.of(2025, 2, 28), ParserUtil.parseSessionDate("2025-02-28"));
    }

    @Test
    public void parseSessionDate_feb29NonLeapYear_throwsParseException() {
        // BVA: feb 29 on non leap-year
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionDate("2025-02-29"));
    }

    @Test
    public void parseSessionDate_feb29LeapYear_success() throws Exception {
        // BVA: feb 29 on leap year
        assertEquals(LocalDate.of(2024, 2, 29), ParserUtil.parseSessionDate("2024-02-29"));
    }

    @Test
    public void parseSessionDate_feb30LeapYear_throwsParseException() {
        // BVA: 30 feb on leap year
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionDate("2024-02-30"));
    }

    @Test
    public void parseSessionDate_nonDateString_throwsParseException() {
        // EP: non-date input
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionDate("abc"));
    }

    @Test
    public void parseSessionDate_missingHyphens_throwsParseException() {
        // EP: date with no hyphens
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionDate("20260316"));
    }

    @Test
    public void parseSessionDate_dayFirstWithSlashes_throwsParseException() {
        // EP: date with slashes instead
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionDate("16/03/2026"));
    }

    @Test
    public void parseSessionDate_dayFirstWithHyphens_throwsParseException() {
        // EP: day is formatted as day-month-year
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionDate("16-03-2026"));
    }

    @Test
    public void parseSessionDate_monthZero_throwsParseException() {
        // BVA: month = 0
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionDate("2026-00-01"));
    }

    @Test
    public void parseSessionDate_monthThirteen_throwsParseException() {
        // BVA: month = 13
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionDate("2026-13-01"));
    }

    @Test
    public void parseSessionDate_dayZero_throwsParseException() {
        // BVA: day = 0
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionDate("2026-03-00"));
    }


    @Test
    public void parseSessionDate_dayThirtyTwo_throwsParseException() {
        // BVA: day = 32
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionDate("2026-03-32"));
    }
}
