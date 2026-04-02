package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.FIVE_PARTICIPATION;
import static seedu.address.logic.commands.CommandTestUtil.THREE_PARTICIPATION;
import static seedu.address.logic.commands.CommandTestUtil.ZERO_PARTICIPATION;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class SessionTest {

    private final LocalDate testDate = LocalDate.of(2026, 3, 16);
    private final String testDateString = "2026-03-16";

    private final Attendance presentAttendance = new Attendance(Attendance.Status.PRESENT);
    private final Participation zeroParticipation = new Participation(ZERO_PARTICIPATION);
    private final Participation fullParticipation = new Participation(FIVE_PARTICIPATION);

    @Test
    public void constructor_null_throwsNullPointerException() {
        // EP: null date
        assertThrows(NullPointerException.class, () -> new Session((LocalDate) null,
                presentAttendance, zeroParticipation));
        // EP: null attendance
        assertThrows(NullPointerException.class, () -> new Session(testDate, null, zeroParticipation));

        // EP: null participation
        assertThrows(NullPointerException.class, () -> new Session(testDate, presentAttendance, null));
    }

    @Test
    public void constructor_validDateString_success() {
        // EP: valid date format
        Session session = new Session(testDateString, presentAttendance, zeroParticipation);
        assertEquals(testDate, session.getDate());

        // EP: month is valid from [1..12]
        String validFifthMonth = "2026-05-16";
        Session session1 = new Session(validFifthMonth, presentAttendance, zeroParticipation);
        assertEquals(LocalDate.parse(validFifthMonth), session1.getDate());

        //EP: day is valid from [1..28], 29 (leap year for Feb), 30, 31
        String validLeapYearDate = "2028-02-29";
        Session session2 = new Session(validLeapYearDate, presentAttendance, zeroParticipation);
        assertEquals(LocalDate.parse(validLeapYearDate), session2.getDate());
    }

    @Test
    public void constructor_invalidLeapYear_throwsIllegalArgumentException() {
        //EP: non-leap year with invalid date
        String invalidLeapYearDate = "2027-02-29";
        assertThrows(IllegalArgumentException.class, () ->
                new Session(invalidLeapYearDate, presentAttendance, zeroParticipation));
    }

    @Test
    public void constructor_whiteSpaceDate_throwsIllegalArgumentException() {
        // EP: date with white space
        assertThrows(IllegalArgumentException.class, () -> new Session("  ",
                presentAttendance, zeroParticipation));
    }

    @Test
    public void constructor_emptyDate_throwsIllegalArgumentException() {
        // EP: empty string
        assertThrows(IllegalArgumentException.class, () -> new Session("",
                presentAttendance, zeroParticipation));
    }

    @Test
    public void constructor_invalidDateStringFormat_throwsIllegalArgumentException() {
        // EP: wrong date format
        String invalidDateStringFormat = "16-03-2026";
        assertThrows(IllegalArgumentException.class, () ->
                new Session(invalidDateStringFormat, presentAttendance, zeroParticipation));
    }

    @Test
    public void constructor_invalidMonthRange_throwsIllegalArgumentException() {
        // EP: month is out of valid range of [1..12]

        // BVA: month is 13
        String invalidThirteenMonth = "2026-13-01";
        assertThrows(IllegalArgumentException.class, () ->
                new Session(invalidThirteenMonth, presentAttendance, zeroParticipation));

        //BVA: month is 00
        String invalidZeroMonth = "2026-00-01";
        assertThrows(IllegalArgumentException.class, () ->
                new Session(invalidZeroMonth, presentAttendance, zeroParticipation));
    }

    @Test
    public void constructor_invalidDayRange_throwsIllegalArgumentException() {
        // EP: day is out of valid range of [1..28], 29, 30, 31

        // BVA: day is 00
        String invalidZeroDay = "2026-12-00";
        assertThrows(IllegalArgumentException.class, () ->
                new Session(invalidZeroDay, presentAttendance, zeroParticipation));

        //BVA: day is 32
        String invalidThirtyTwoDay = "2026-12-32";
        assertThrows(IllegalArgumentException.class, () ->
                new Session(invalidThirtyTwoDay, presentAttendance, zeroParticipation));

    }

    @Test
    public void constructor_validAttendance_success() {
        //EP: valid attendance
        Session session = new Session(testDate.toString(),
                new Attendance(Attendance.Status.PRESENT), zeroParticipation);
        assertEquals(Attendance.Status.PRESENT, session.getAttendance().value);
    }

    @Test
    public void constructor_validParticipation_success() {
        // EP: valid participation
        Session session = new Session(testDate.toString(),
                presentAttendance, new Participation(THREE_PARTICIPATION));
        assertEquals(THREE_PARTICIPATION, session.getParticipation().value);
    }

    @Test
    public void constructor_invalidAttendance_throwsIllegalArgumentException() {
        // EP: invalid attendance
        assertThrows(IllegalArgumentException.class, () -> new Session(testDate.toString(), new Attendance("test"),
                zeroParticipation));
    }

    @Test
    public void constructor_tooHighParticipation_throwsIllegalArgumentException() {
        // EP: invalid participation, BVA: 6
        assertThrows(IllegalArgumentException.class, () -> new Session(testDate.toString(), presentAttendance,
                new Participation(6)));
    }

    @Test
    public void constructor_negativeParticipation_throwsIllegalArgumentException() {
        // EP: invalid participation, BVA: -1
        assertThrows(IllegalArgumentException.class, () -> new Session(testDate.toString(), presentAttendance,
                new Participation(-1)));
    }

    @Test
    public void getters_validInputs_success() {
        Session session = new Session(testDate, presentAttendance, fullParticipation);
        assertEquals(testDate, session.getDate());
        assertEquals(presentAttendance, session.getAttendance());
        assertEquals(fullParticipation, session.getParticipation());
    }


    @Test
    public void equals() {
        Session sessionA = new Session(testDate, presentAttendance, zeroParticipation);
        Session sessionACopy = new Session(testDate, presentAttendance, zeroParticipation);
        Session sessionB = new Session(testDate, presentAttendance, fullParticipation); // Different participation.
        Session sessionC = new Session(LocalDate.of(2026, 3, 17),
                presentAttendance, zeroParticipation); // Different date.
        Session sessionD = new Session(testDate,
                new Attendance(Attendance.Status.ABSENT), zeroParticipation); // different attendance.

        // EP: different attendance -> returns false.
        assertFalse(sessionA.equals(sessionD));

        // EP: same object -> returns true.
        assertTrue(sessionA.equals(sessionA));

        // EP: same values -> returns true.
        assertTrue(sessionA.equals(sessionACopy));

        // EP: different types -> returns false.
        assertFalse(sessionA.equals(1));

        // EP: null -> returns false.
        assertFalse(sessionA.equals(null));

        // EP: different participation -> returns false.
        assertFalse(sessionA.equals(sessionB));

        // EP: different date -> returns false.
        assertFalse(sessionA.equals(sessionC));
    }

    @Test
    public void hashCode_sameSession_returnsSameHashCode() {
        // EP: same session -> same hashcode
        Session sessionA = new Session(testDate, presentAttendance, zeroParticipation);
        Session sessionACopy = new Session(testDate, presentAttendance, zeroParticipation);
        assertEquals(sessionA.hashCode(), sessionACopy.hashCode());
    }
    @Test
    public void hashCode_differentSession_returnsDifferentHashCode() {
        // EP: different session -> different hashcode
        Session sessionA = new Session(testDate, presentAttendance, zeroParticipation);
        Session sessionB = new Session(testDate, presentAttendance, fullParticipation);
        assertNotEquals(sessionA.hashCode(), sessionB.hashCode());
    }

    @Test
    public void toString_formatsCorrectly() {
        // EP: valid session
        Session session = new Session(testDate, presentAttendance, new Participation(THREE_PARTICIPATION));
        assertTrue(session.toString().contains("date=2026-03-16"));
        assertTrue(session.toString().contains("attendance=PRESENT"));
        assertTrue(session.toString().contains("participation=3"));
    }

}
