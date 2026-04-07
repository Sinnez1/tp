package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Email;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Participation;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_BLANK_FIELD = " ";
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_MATRIC_NUMBER = "A1234567A";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";
    private static final List<String> INVALID_GROUPS = List.of(" ");
    private static final Map<String, List<JsonAdaptedSession>> NULL_GROUP_SESSIONS = null;
    private static final String INVALID_ATTENDANCE = "LATE";
    private static final int INVALID_PARTICIPATION = 999;

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_MATRIC_NUMBER = BENSON.getMatricNumber().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags()
            .stream().map(JsonAdaptedTag::new).collect(Collectors.toList());
    private static final List<String> VALID_GROUPS = Arrays.asList("CS2103T-T01", "CS2103T-T02");
    private static final String VALID_SESSION_NOTE = "";
    private static final String VALID_ATTENDANCE = "ABSENT";
    private static final int VALID_PARTICIPATION = 4;

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        var sourcePerson = new PersonBuilder(BENSON)
                .withGroups("CS2103T-T01", "CS2103T-T02")
                .build();

        JsonAdaptedPerson person = new JsonAdaptedPerson(sourcePerson);
        assertEquals(sourcePerson, person.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_MATRIC_NUMBER, VALID_TAGS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                null, VALID_PHONE, VALID_EMAIL, VALID_MATRIC_NUMBER, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_MATRIC_NUMBER, VALID_TAGS);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, null, VALID_EMAIL, VALID_MATRIC_NUMBER, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_MATRIC_NUMBER, VALID_TAGS);

        String expectedMessage = Email.getDiagnosticMessage(INVALID_EMAIL);

        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, null, VALID_MATRIC_NUMBER, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_blankMatricNumber_throwsIllegalValueException() {
        // EP: blank matric number (white space)
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, INVALID_BLANK_FIELD, VALID_TAGS);
        String expectedMessage = MatricNumber.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidMatricNumber_throwsIllegalValueException() {
        // EP: invalid matric number (wrong checksum)
        String expectedMessage = null;
        try {
            new MatricNumber(INVALID_MATRIC_NUMBER);
        } catch (IllegalArgumentException e) {
            expectedMessage = e.getMessage();
        }
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, INVALID_MATRIC_NUMBER, VALID_TAGS);
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullMatricNumber_throwsIllegalValueException() {
        // EP: null matric number
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, null, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, MatricNumber.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_MATRIC_NUMBER, invalidTags);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_invalidAttendanceInSession_throwsIllegalValueException() {
        // EP: attendance now lives inside sessions — invalid session attendance propagates up
        Map<String, List<JsonAdaptedSession>> invalidSessionMap = Map.of(
                "CS2103T-T01",
                List.of(new JsonAdaptedSession(
                        "2026-01-01", INVALID_ATTENDANCE, VALID_PARTICIPATION, VALID_SESSION_NOTE))
        );
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_MATRIC_NUMBER, VALID_TAGS,
                VALID_GROUPS, invalidSessionMap);
        assertThrows(IllegalValueException.class, Attendance.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_invalidParticipationInSession_throwsIllegalValueException() {
        // EP: attendance now lives inside sessions — invalid session attendance propagates up
        Map<String, List<JsonAdaptedSession>> invalidSessionMap = Map.of(
                "CS2103T-T01",
                List.of(new JsonAdaptedSession(
                        "2026-01-01", VALID_ATTENDANCE, INVALID_PARTICIPATION, VALID_SESSION_NOTE))
        );
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_MATRIC_NUMBER, VALID_TAGS,
                VALID_GROUPS, invalidSessionMap);
        assertThrows(IllegalValueException.class, Participation.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_invalidGroup_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_MATRIC_NUMBER, VALID_TAGS,
                INVALID_GROUPS, NULL_GROUP_SESSIONS);
        assertThrows(IllegalValueException.class, GroupName.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_multipleInvalidFields_throwsIllegalValueException() {
        // EP: Person with both an invalid email and an invalid matric number.
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, INVALID_EMAIL, INVALID_MATRIC_NUMBER, VALID_TAGS);

        String expectedMessage = Email.getDiagnosticMessage(INVALID_EMAIL) + "; "
                + String.format(MatricNumber.MESSAGE_INVALID_CHECKSUM, 'X');

        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidGroupNameInGrades_throwsIllegalValueException() {
        // EP: Group name key in assignmentGrades is invalid (empty string fails validation)
        Map<String, Map<String, Integer>> invalidGrades = Map.of(
                " ", Map.of("Assignment 1", 50) // invalid group name
        );
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_MATRIC_NUMBER, VALID_TAGS,
                VALID_GROUPS, NULL_GROUP_SESSIONS, invalidGrades);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_invalidAssignmentNameInGrades_throwsIllegalValueException() {
        // EP: Assignment name key in grades is invalid (empty string fails validation)
        Map<String, Map<String, Integer>> invalidGrades = Map.of(
                "CS2103T-T01", Map.of(" ", 50) // invalid assignment name
        );
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_MATRIC_NUMBER, VALID_TAGS,
                VALID_GROUPS, NULL_GROUP_SESSIONS, invalidGrades);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_negativeGradeValue_throwsIllegalValueException() {
        // EP: assignment with negative max marks
        Map<String, Map<String, Integer>> invalidGrades = Map.of(
                "CS2103T-T01", Map.of("Assignment 1", -1)
        );
        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_MATRIC_NUMBER, VALID_TAGS,
                VALID_GROUPS, NULL_GROUP_SESSIONS, invalidGrades);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

}
