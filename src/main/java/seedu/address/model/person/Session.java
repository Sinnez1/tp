package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a specific session (e.g., a tutorial group session) for a student on a specific date.
 * Contains the date, attendance, and participation for that session.
 */
public class Session {
    public static final String MESSAGE_CONSTRAINTS = "Date must be a valid calender date in the format yyy-MM-dd";
    public static final String BLANK_DATE_MESSAGE = "Date must not be blank and should be in the format yyyy-MM-dd.";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT); // ensures days must be valid for each month, not just in general

    private final LocalDate date;
    private final Attendance attendance;
    private final Participation participation;
    private final String note;

    /**
     *  Constructs a {@code Session} using a {@code LocalDate}.
     *
     * @param date Date of session in yyyy-mm-dd.
     * @param attendance Attendance of the student.
     * @param participation Participation score of the student.
     */
    public Session(LocalDate date, Attendance attendance, Participation participation) {
        this(date, attendance, participation, "");
    }

    /**
     *  Constructs a {@code Session} using a {@code LocalDate}.
     *
     * @param date Date of session in yyyy-mm-dd.
     * @param attendance Attendance of the student.
     * @param participation Participation score of the student.
     * @param note Optional note describing the session.
     */
    public Session(LocalDate date, Attendance attendance, Participation participation, String note) {
        requireAllNonNull(date, attendance, participation);
        this.date = date;
        this.attendance = attendance;
        this.participation = participation;
        this.note = normalizeNote(note);
    }

    /**
     * Constructs a {@code Session} using a string representation of the date.
     *
     * @param dateString String representation of the date.
     * @param attendance Attendance of the student.
     * @param participation Participation score of the student.
     */
    public Session(String dateString, Attendance attendance, Participation participation) {
        this(dateString, attendance, participation, "");
    }

    /**
     * Constructs a {@code Session} using a string representation of the date.
     *
     * @param dateString String representation of the date.
     * @param attendance Attendance of the student.
     * @param participation Participation score of the student.
     * @param note Optional note describing the session.
     */
    public Session(String dateString, Attendance attendance, Participation participation, String note) {
        requireAllNonNull(dateString, attendance, participation);
        try {
            this.date = LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        this.attendance = attendance;
        this.participation = participation;
        this.note = normalizeNote(note);
    }

    /**
     * Returns date of session in yyyy-mm-dd format.
     *
     * @return Date in yyyy-mm-dd format.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns attendance of the student on the session.
     *
     * @return Attendance of the student.
     */
    public Attendance getAttendance() {
        return attendance;
    }

    /**
     * Returns participation score of the student on the session.
     *
     * @return Participation score of the student.
     */
    public Participation getParticipation() {
        return participation;
    }

    /**
     * Returns the optional note describing the session.
     */
    public String getNote() {
        return note;
    }

    /**
     * Returns true if the session has a non-blank note.
     */
    public boolean hasNote() {
        return !note.isBlank();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Session)) {
            return false;
        }
        Session otherSession = (Session) other;
        return date.equals(otherSession.date)
                && attendance.equals(otherSession.attendance)
                && participation.equals(otherSession.participation)
                && note.equals(otherSession.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, attendance, participation, note);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("date", date.format(DATE_FORMATTER))
                .add("attendance", attendance)
                .add("participation", participation)
                .add("note", note)
                .toString();
    }

    private static String normalizeNote(String note) {
        requireAllNonNull(note);
        return note.trim();
    }
}
