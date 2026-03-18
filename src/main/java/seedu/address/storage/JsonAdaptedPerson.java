package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Email;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Participation;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Session;
import seedu.address.model.person.SessionList;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String matricNumber;
    private final String attendance;
    private final Integer participation;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final List<String> classSpaces = new ArrayList<>();
    private final Map<String, List<JsonAdaptedSession>> classSpaceSessions = new HashMap<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("matricNumber") String matricNumber,
            @JsonProperty("attendance") String attendance,
            @JsonProperty("participation") Integer participation,
            @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("classSpaces") List<String> classSpaces,
            @JsonProperty("classSpaceSessions") Map<String, List<JsonAdaptedSession>> classSpaceSessions) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.matricNumber = matricNumber;
        this.attendance = attendance;
        this.participation = participation;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        if (classSpaces != null) {
            this.classSpaces.addAll(classSpaces);
        }
        if (classSpaceSessions != null) {
            classSpaceSessions.forEach((classSpaceName, sessions) -> {
                List<JsonAdaptedSession> adaptedSessions = sessions == null
                        ? new ArrayList<>()
                        : new ArrayList<>(sessions);
                this.classSpaceSessions.put(classSpaceName, adaptedSessions);
            });
        }
    }

    public JsonAdaptedPerson(String name, String phone, String email, String matricNumber,
                             List<JsonAdaptedTag> tags) {
        this(name, phone, email, matricNumber, null, null, tags, null, null);
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        matricNumber = source.getMatricNumber().value;
        attendance = source.getAttendance().toString();
        participation = source.getParticipation().value;
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .toList());
        classSpaces.addAll(source.getClassSpaces().stream()
                .map(classSpaceName -> classSpaceName.value)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList());
        source.getClassSpaceSessions().forEach((classSpaceName, sessionList) -> classSpaceSessions.put(
                classSpaceName.value,
                sessionList.getSessions().stream().map(JsonAdaptedSession::new).toList()
        ));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        final Set<ClassSpaceName> modelClassSpaces = new HashSet<>();
        for (String classSpace : classSpaces) {
            if (!ClassSpaceName.isValidClassSpaceName(classSpace)) {
                throw new IllegalValueException(ClassSpaceName.MESSAGE_CONSTRAINTS);
            }
            modelClassSpaces.add(new ClassSpaceName(classSpace));
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (matricNumber == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    MatricNumber.class.getSimpleName()));
        }

        final MatricNumber modelMatricNumber;
        try {
            modelMatricNumber = new MatricNumber(matricNumber);
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException(e.getMessage());
        }

        final Attendance modelAttendance;
        if (attendance == null) {
            modelAttendance = new Attendance(Attendance.Status.UNINITIALISED);
        } else if (!Attendance.isValidAttendance(attendance)) {
            throw new IllegalValueException(Attendance.MESSAGE_CONSTRAINTS);
        } else {
            modelAttendance = new Attendance(attendance);
        }

        final Participation modelParticipation;
        if (participation == null) {
            modelParticipation = new Participation(0);
        } else if (!Participation.isValidParticipation(participation)) {
            throw new IllegalValueException(Participation.MESSAGE_CONSTRAINTS);
        } else {
            modelParticipation = new Participation(participation);
        }

        final Set<Tag> modelTags = new HashSet<>(personTags);
        Person person = new Person(modelName, modelPhone, modelEmail, modelMatricNumber, modelClassSpaces, modelTags);
        person = new Person(person, modelAttendance);
        person = new Person(person, modelParticipation);
        person = new Person(person, parseClassSpaceSessions());
        return person;
    }

    private Map<ClassSpaceName, SessionList> parseClassSpaceSessions() throws IllegalValueException {
        Map<ClassSpaceName, SessionList> modelSessionMap = new HashMap<>();
        for (Map.Entry<String, List<JsonAdaptedSession>> entry : classSpaceSessions.entrySet()) {
            String classSpaceNameString = entry.getKey();
            if (!ClassSpaceName.isValidClassSpaceName(classSpaceNameString)) {
                throw new IllegalValueException(ClassSpaceName.MESSAGE_CONSTRAINTS);
            }
            ClassSpaceName classSpaceName = new ClassSpaceName(classSpaceNameString);
            List<Session> sessions = new ArrayList<>();
            List<JsonAdaptedSession> adaptedSessions = entry.getValue() == null ? List.of() : entry.getValue();
            for (JsonAdaptedSession adaptedSession : adaptedSessions) {
                sessions.add(adaptedSession.toModelType());
            }
            modelSessionMap.put(classSpaceName, new SessionList(sessions));
        }
        return modelSessionMap;
    }

}
