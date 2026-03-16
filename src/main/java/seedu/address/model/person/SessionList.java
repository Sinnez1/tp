package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a list of sessions, belonging to a class space.
 * Allows iterating through sessions to retrieve attendance and participation.
 */
public class SessionList implements Iterable<Session> {
    private final List<Session> sessions;

    /**
     * Initializes a {@code SessionList} as an {@code ArrayList}.
     */
    public SessionList() {
        this.sessions = new ArrayList<>();
    }

    /**
     * Constructs a {@code SessionList} using a {@code List<Session>}.
     *
     * @param sessions A {@code List} of sessions.
     */
    public SessionList(List<Session> sessions) {
        requireNonNull(sessions);
        this.sessions = new ArrayList<>(sessions);
    }

    /**
     * Adds a session to the list.
     * If a session on the same date already exists, it is overwritten.
     *
     * @param session Session to be added to the {@code List<Session>}
     */
    public void addSession(Session session) {
        requireNonNull(session);
        sessions.removeIf(s -> s.getDate().equals(session.getDate()));
        sessions.add(session);
    }

    /**
     * Retrieves the session for a given date, if it exists.
     *
     * @param date Date of the session.
     */
    public Optional<Session> getSession(LocalDate date) {
        requireNonNull(date);
        return sessions.stream()
                .filter(session -> session.getDate().equals(date))
                .findFirst();
    }
    /**
     * Retrieves the attendance for a given date, if the session exists.
     *
     * @param date Date of the session.
     */
    public Optional<Attendance> getAttendance(LocalDate date) {
        return getSession(date).map(Session::getAttendance);
    }

    /**
     * Retrieves the participation for a given date, if the session exists.
     *
     * @param date Date of the session.
     */
    public Optional<Participation> getParticipation(LocalDate date) {
        return getSession(date).map(Session::getParticipation);
    }

    /**
     * Returns an unmodifiable view of the underlying list of sessions.
     *
     * @return Unmodifiable view of sessions available.
     */
    public List<Session> getSessions() {
        return Collections.unmodifiableList(sessions);
    }

    @Override
    public Iterator<Session> iterator() {
        return sessions.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SessionList)) {
            return false;
        }
        SessionList otherList = (SessionList) other;
        return sessions.equals(otherList.sessions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessions);
    }
}
