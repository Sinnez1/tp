package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.FIVE_PARTICIPATION;
import static seedu.address.logic.commands.CommandTestUtil.ONE_PARTICIPATION;
import static seedu.address.logic.commands.CommandTestUtil.TWO_PARTICIPATION;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class SessionListTest {

    private final LocalDate date1 = LocalDate.of(2026, 3, 16);
    private final LocalDate date2 = LocalDate.of(2026, 3, 17);
    private final Session session1 = new Session(date1, new Attendance(Attendance.Status.PRESENT),
            new Participation(ONE_PARTICIPATION));
    private final Session session2 = new Session(date2, new Attendance(Attendance.Status.ABSENT),
            new Participation(TWO_PARTICIPATION));
    private final SessionList singleTestSessionList1 = new SessionList(Collections.singletonList(session1));
    private final SessionList singleTestSessionList2 = new SessionList(Collections.singletonList(session2));
    private final SessionList doubleTestSessionList = new SessionList(Arrays.asList(session1, session2));

    @Test
    public void addSession_newSession_addsSuccessfully() {
        // EP: add session with date not in list
        SessionList sessionList = new SessionList();
        sessionList.addSession(session1);
        assertEquals(1, sessionList.getSessions().size());
        assertEquals(session1, sessionList.getSession(date1).get());
    }

    @Test
    public void addSession_existingDate_overwritesSuccessfully() {
        // EP: add session with existing date; overwrites it
        SessionList sessionList = new SessionList();
        sessionList.addSession(session1);

        // Create new session for the same date.
        Session updatedSession1 = new Session(date1, new Attendance(Attendance.Status.ABSENT),
                new Participation(FIVE_PARTICIPATION));
        sessionList.addSession(updatedSession1);

        // Size should remain 1, and the session should be the updated one.
        assertEquals(1, sessionList.getSessions().size());
        assertEquals(updatedSession1, sessionList.getSession(date1).get());
    }

    @Test
    public void addSession_nullSession_throwsNullPointerException() {
        SessionList sessionList = new SessionList();
        assertThrows(NullPointerException.class, () -> sessionList.addSession(null));
    }

    @Test
    public void removeSession_removeExistingSession_returnsTrueAndRemoves() {
        // EP: date present in list — returns true and session is gone
        SessionList sessionList = new SessionList();
        sessionList.addSession(session1);
        assertTrue(sessionList.removeSession(date1));
        assertFalse(sessionList.getSession(date1).isPresent());
    }

    @Test
    public void removeSession_existingDate_decreasesSize() {
        // EP: removing from a 2-element list — size decreases by 1
        SessionList sessionList = new SessionList();
        sessionList.addSession(session1);
        sessionList.addSession(session2);
        sessionList.removeSession(date1);
        assertEquals(1, sessionList.getSessions().size());
    }

    @Test
    public void removeSession_lastElement_listBecomesEmpty() {
        // BVA: removing the only element — list becomes empty
        SessionList sessionList = new SessionList();
        sessionList.addSession(session1);
        assertTrue(sessionList.removeSession(date1));
        assertEquals(0, sessionList.getSessions().size());
    }

    @Test
    public void removeSession_nonExistentDate_returnsFalseAndLeavesListUnchanged() {
        // EP: date not in list — returns false, list unchanged
        SessionList sessionList = new SessionList();
        sessionList.addSession(session1);
        assertFalse(sessionList.removeSession(date2));
        assertEquals(1, sessionList.getSessions().size());
        assertTrue(sessionList.getSession(date1).isPresent());
    }

    @Test
    public void removeSession_null_throwsNullPointerException() {
        // EP: null date to remove
        SessionList sessionList = new SessionList();
        sessionList.addSession(session1);
        assertThrows(NullPointerException.class, () -> sessionList.removeSession(null));
    }


    @Test
    public void getSession_existingDate_returnsSession() {
        // EP: existing session, returns correct session
        Optional<Session> result = doubleTestSessionList.getSession(date1);
        assertTrue(result.isPresent());
        assertEquals(session1, result.get());
    }

    @Test
    public void getSession_nonExistentDate_returnsEmpty() {
        // EP: no such session in list
        Optional<Session> result = singleTestSessionList1.getSession(date2);
        assertFalse(result.isPresent());
    }

    @Test
    public void getAttendance_existingSession_returnsAttendance() {
        // EP: return correct attendance for existing date
        Optional<Attendance> attendance = singleTestSessionList1.getAttendance(date1);
        assertTrue(attendance.isPresent());
        assertEquals(session1.getAttendance(), attendance.get());
    }

    @Test
    public void getAttendance_nonExistentDate_returnsEmpty() {
        // EP: date absent
        assertFalse(singleTestSessionList1.getAttendance(date2).isPresent());
    }

    @Test
    public void getParticipation_existingSession_returnsParticipation() {
        // EP: return correct participation for existing date
        Optional<Participation> participation = singleTestSessionList1.getParticipation(date1);
        assertTrue(participation.isPresent());
        assertEquals(session1.getParticipation(), participation.get());
    }

    @Test
    public void getParticipation_nonExistentDate_returnsEmpty() {
        // EP: date absent
        assertFalse(singleTestSessionList1.getParticipation(date2).isPresent());
    }

    @Test
    public void getSessions_modifyList_throwsUnsupportedOperationException() {
        // EP: attempt to mutate unmodifiable
        List<Session> unmodifiableList = singleTestSessionList2.getSessions();
        assertThrows(UnsupportedOperationException.class, () -> unmodifiableList.add(session2));
    }

    @Test
    public void getSessions_emptyList_returnsEmptyList() {
        // BVA: minimum list size (0) — returns empty list
        SessionList emptyList = new SessionList();
        assertTrue(emptyList.getSessions().isEmpty());
    }

    @Test
    public void sessionList_iterator_iteratesSucessfully() {
        // EP: has elements to iterate
        int count = 0;
        for (Session s : doubleTestSessionList) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void iterator_emptyList_iteratesZeroTimes() {
        // EP: no elements to iterate
        SessionList emptyList = new SessionList();
        int count = 0;
        for (Session s : emptyList) {
            count++;
        }
        assertEquals(0, count);
    }

    @Test
    public void equals() {
        SessionList list1Copy = new SessionList(Collections.singletonList(session1));

        // EP: null -> returns false.
        assertFalse(singleTestSessionList1.equals(null));

        // EP: same session (copy of session) -> returns true.
        assertTrue(singleTestSessionList1.equals(list1Copy));

        // EP: same object -> returns true.
        assertTrue(singleTestSessionList1.equals(singleTestSessionList1));

        // EP: different sessions -> returns false.
        assertFalse(singleTestSessionList1.equals(singleTestSessionList2));

        // EP: different types.
        assertFalse(singleTestSessionList1.equals(1));

    }

    @Test
    public void equals_twoEmptyLists_returnsTrue() {
        // EP: empty lists are equal
        SessionList empty1 = new SessionList();
        SessionList empty2 = new SessionList();
        assertTrue(empty1.equals(empty2));
    }

    @Test
    public void equals_sameSessionsDifferentOrder_returnsFalse() {
        // EP: SessionList uses ArrayList so order matters
        // Same sessions in different insertion order should be unequal
        SessionList list1 = new SessionList(Arrays.asList(session1, session2));
        SessionList list2 = new SessionList(Arrays.asList(session2, session1));
        assertFalse(list1.equals(list2));
    }

    @Test
    public void hashCode_test() {
        // EP: same object -> same hashcode
        assertEquals(singleTestSessionList1.hashCode(), singleTestSessionList1.hashCode());
        assertEquals(singleTestSessionList1, singleTestSessionList1);

        // EP: different lists
        assertNotEquals(singleTestSessionList1, singleTestSessionList2);
    }

    @Test
    public void hashCode_emptyLists_returnsSameHashCode() {
        // EP: empty lists have same hashcode
        SessionList empty1 = new SessionList();
        SessionList empty2 = new SessionList();
        assertEquals(empty1.hashCode(), empty2.hashCode());

        // EP: structurally equal lists — hash codes must match
        SessionList list1Copy = new SessionList(Collections.singletonList(session1));
        assertEquals(singleTestSessionList1.hashCode(), list1Copy.hashCode());
    }
}
