package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Participation;
import seedu.address.model.person.Session;

/**
 * Jackson-friendly version of {@link Session}.
 */
class JsonAdaptedSession {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Session's %s field is missing!";

    private final String date;
    private final String attendance;
    private final Integer participation;

    @JsonCreator
    public JsonAdaptedSession(@JsonProperty("date") String date,
                              @JsonProperty("attendance") String attendance,
                              @JsonProperty("participation") Integer participation) {
        this.date = date;
        this.attendance = attendance;
        this.participation = participation;
    }

    public JsonAdaptedSession(Session source) {
        date = source.getDate().toString();
        attendance = source.getAttendance().toString();
        participation = source.getParticipation().value;
    }

    public Session toModelType() throws IllegalValueException {
        if (date == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "date"));
        }
        if (attendance == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "attendance"));
        }
        if (participation == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "participation"));
        }

        if (!Attendance.isValidAttendance(attendance)) {
            throw new IllegalValueException(Attendance.MESSAGE_CONSTRAINTS);
        }
        if (!Participation.isValidParticipation(participation)) {
            throw new IllegalValueException(Participation.MESSAGE_CONSTRAINTS);
        }

        try {
            return new Session(date, new Attendance(attendance), new Participation(participation));
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException(e.getMessage());
        }
    }
}
