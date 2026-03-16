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
        assertThrows(NullPointerException.class, () -> new Session((LocalDate) null,
                presentAttendance, zeroParticipation));
        assertThrows(NullPointerException.class, () -> new Session(testDate, null, zeroParticipation));
        assertThrows(NullPointerException.class, () -> new Session(testDate, presentAttendance, null));
    }

    @Test
    public void constructor_validDateString_success() {
        Session session = new Session(testDateString, presentAttendance, zeroParticipation);
        assertEquals(testDate, session.getDate());
    }

    @Test
    public void constructor_nullDate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Session((LocalDate) null,
                presentAttendance, zeroParticipation));
    }

    @Test
    public void constructor_emptyDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Session("",
                presentAttendance, zeroParticipation));
    }
    @Test
    public void constructor_invalidDateString_throwsIllegalArgumentException() {
        String invalidDateString = "16-03-2026";
        assertThrows(IllegalArgumentException.class, () ->
                new Session(invalidDateString, presentAttendance, zeroParticipation));
    }
    @Test
    public void constructor_validAttendance_success() {
        Session session = new Session(testDate.toString(),
                new Attendance(Attendance.Status.PRESENT), zeroParticipation);
        assertEquals(Attendance.Status.PRESENT, session.getAttendance().value);
    }

    @Test
    public void constructor_validParticipation_success() {
        Session session = new Session(testDate.toString(),
                presentAttendance, new Participation(THREE_PARTICIPATION));
        assertEquals(THREE_PARTICIPATION, session.getParticipation().value);
    }

    @Test
    public void constructor_nullAttendance_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Session(testDate.toString(), null, zeroParticipation));
    }

    @Test
    public void constructor_nullParticipation_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Session(testDate.toString(), presentAttendance, null));
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

        // same object -> returns true.
        assertTrue(sessionA.equals(sessionA));

        // same values -> returns true.
        assertTrue(sessionA.equals(sessionACopy));

        // different types -> returns false.
        assertFalse(sessionA.equals(1));

        // null -> returns false.
        assertFalse(sessionA.equals(null));

        // different participation -> returns false.
        assertFalse(sessionA.equals(sessionB));

        // different date -> returns false.
        assertFalse(sessionA.equals(sessionC));
    }

    @Test
    public void hashCode_sameSession_returnsSameHashCode() {
        Session sessionA = new Session(testDate, presentAttendance, zeroParticipation);
        Session sessionACopy = new Session(testDate, presentAttendance, zeroParticipation);
        assertEquals(sessionA.hashCode(), sessionACopy.hashCode());
    }
    @Test
    public void hashCode_differentSession_returnsDifferentHashCode() {
        Session sessionA = new Session(testDate, presentAttendance, zeroParticipation);
        Session sessionB = new Session(testDate, presentAttendance, fullParticipation);
        assertNotEquals(sessionA.hashCode(), sessionB.hashCode());
    }

    @Test
    public void toString_formatsCorrectly() {
        Session session = new Session(testDate, presentAttendance, new Participation(THREE_PARTICIPATION));
        String expectedString = "Session{date=2026-03-16, attendance=PRESENT, participation=3}";
        // Note: The ToStringBuilder uses the class name, so the expected output includes "Session{...}"
        // Adjust if your ToStringBuilder implementation differs.
        assertTrue(session.toString().contains("date=2026-03-16"));
        assertTrue(session.toString().contains("attendance=PRESENT"));
        assertTrue(session.toString().contains("participation=3"));
    }

}
