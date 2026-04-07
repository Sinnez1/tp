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
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Email;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Name;
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
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final List<String> groups = new ArrayList<>();
    private final Map<String, List<JsonAdaptedSession>> groupSessions = new HashMap<>();
    private final Map<String, Map<String, Integer>> assignmentGrades = new HashMap<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(
            @JsonProperty("name") String name,
            @JsonProperty("phone") String phone,
            @JsonProperty("email") String email,
            @JsonProperty("matricNumber") String matricNumber,
            @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("groups") List<String> groups,
            @JsonProperty("groupSessions") Map<String, List<JsonAdaptedSession>> groupSessions,
            @JsonProperty("assignmentGrades") Map<String, Map<String, Integer>> assignmentGrades) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.matricNumber = matricNumber;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        if (groups != null) {
            this.groups.addAll(groups);
        }
        if (groupSessions != null) {
            groupSessions.forEach((groupName, sessions) -> {
                List<JsonAdaptedSession> adaptedSessions = (sessions == null)
                        ? new ArrayList<>()
                        : new ArrayList<>(sessions);
                this.groupSessions.put(groupName, adaptedSessions);
            });
        }
        if (assignmentGrades != null) {
            assignmentGrades.forEach((groupName, grades) -> {
                Map<String, Integer> adaptedGrades = (grades == null)
                        ? new HashMap<>()
                        : new HashMap<>(grades);
                this.assignmentGrades.put(groupName, adaptedGrades);
            });
        }
    }

    public JsonAdaptedPerson(String name,
                             String phone,
                             String email,
                             String matricNumber,
                             List<JsonAdaptedTag> tags,
                             List<String> groups,
                             Map<String, List<JsonAdaptedSession>> groupSessions) {
        this(name, phone, email, matricNumber, tags, groups, groupSessions, null);
    }

    public JsonAdaptedPerson(String name,
                             String phone,
                             String email,
                             String matricNumber,
                             List<JsonAdaptedTag> tags) {
        this(name, phone, email, matricNumber, tags, null, null, null);
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getNameValue();
        phone = source.getPhoneValue();
        email = source.getEmailValue();
        matricNumber = source.getMatricNumberValue();
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .toList());
        groups.addAll(source.getGroups().stream()
                .map(groupName -> groupName.value)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList());
        source.getGroupSessions()
                .forEach((groupName, sessionList) -> groupSessions.put(
                        groupName.value,
                        sessionList.getSessions().stream().map(JsonAdaptedSession::new).toList()
        ));
        source.getAssignmentGrades()
                .forEach((groupName, gradeMap) -> assignmentGrades.put(
                        groupName.value,
                        gradeMap.entrySet().stream()
                                .collect(HashMap::new, (
                                        map, entry) -> map.put(entry.getKey().value, entry.getValue()),
                                        HashMap::putAll)));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<String> validationErrors = new ArrayList<>();

        if (name == null) {
            validationErrors.add(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        } else if (!Name.isValidName(name)) {
            validationErrors.add(Name.MESSAGE_CONSTRAINTS);
        }

        if (phone == null) {
            validationErrors.add(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        } else if (!Phone.isValidPhone(phone)) {
            validationErrors.add(Phone.MESSAGE_CONSTRAINTS);
        }

        if (email == null) {
            validationErrors.add(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        } else if (!Email.isValidEmail(email)) {
            validationErrors.add(Email.getDiagnosticMessage(email));
        }

        if (matricNumber == null) {
            validationErrors.add(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    MatricNumber.class.getSimpleName()));
        } else {
            try {
                new MatricNumber(matricNumber);
            } catch (IllegalArgumentException e) {
                validationErrors.add(e.getMessage());
            }
        }

        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            try {
                personTags.add(tag.toModelType());
            } catch (IllegalValueException e) {
                validationErrors.add(e.getMessage());
            }
        }

        for (String group : groups) {
            if (!GroupName.isValidGroupName(group)) {
                validationErrors.add(GroupName.MESSAGE_CONSTRAINTS);
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new IllegalValueException(String.join("; ", validationErrors));
        }

        final Name modelName = new Name(name);
        final Phone modelPhone = new Phone(phone);
        final Email modelEmail = new Email(email);
        final MatricNumber modelMatricNumber = new MatricNumber(matricNumber);
        final Set<Tag> modelTags = new HashSet<>(personTags);

        final Set<GroupName> modelGroups = new HashSet<>();
        for (String group : groups) {
            modelGroups.add(new GroupName(group));
        }

        Person person = new Person(modelName, modelPhone, modelEmail, modelMatricNumber, modelGroups, modelTags);
        person = new Person(person, parseGroupSessions());
        person = new Person(person, parseAssignmentGrades(), true);
        return person;
    }

    private Map<GroupName, SessionList> parseGroupSessions() throws IllegalValueException {
        Map<GroupName, SessionList> modelSessionMap = new HashMap<>();
        for (Map.Entry<String, List<JsonAdaptedSession>> entry : groupSessions.entrySet()) {
            String groupNameString = entry.getKey();
            if (!GroupName.isValidGroupName(groupNameString)) {
                throw new IllegalValueException(GroupName.MESSAGE_CONSTRAINTS);
            }
            GroupName groupName = new GroupName(groupNameString);
            List<Session> sessions = new ArrayList<>();
            List<JsonAdaptedSession> adaptedSessions = entry.getValue() == null ? List.of() : entry.getValue();
            for (JsonAdaptedSession adaptedSession : adaptedSessions) {
                sessions.add(adaptedSession.toModelType());
            }
            modelSessionMap.put(groupName, new SessionList(sessions));
        }
        return modelSessionMap;
    }

    private Map<GroupName, Map<AssignmentName, Integer>> parseAssignmentGrades() throws IllegalValueException {
        Map<GroupName, Map<AssignmentName, Integer>> modelAssignmentGrades = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : assignmentGrades.entrySet()) {
            String groupNameString = entry.getKey();
            if (!GroupName.isValidGroupName(groupNameString)) {
                throw new IllegalValueException(GroupName.MESSAGE_CONSTRAINTS);
            }
            GroupName groupName = new GroupName(groupNameString);
            Map<AssignmentName, Integer> classGrades = new HashMap<>();
            Map<String, Integer> storedGrades = entry.getValue() == null ? Map.of() : entry.getValue();
            for (Map.Entry<String, Integer> gradeEntry : storedGrades.entrySet()) {
                String assignmentNameString = gradeEntry.getKey();
                Integer gradeValue = gradeEntry.getValue();
                if (!AssignmentName.isValidAssignmentName(assignmentNameString)) {
                    throw new IllegalValueException(AssignmentName.MESSAGE_CONSTRAINTS);
                }
                if (gradeValue == null || gradeValue < 0) {
                    throw new IllegalValueException("Assignment grades should be non-negative integers.");
                }
                classGrades.put(new AssignmentName(assignmentNameString), gradeValue);
            }
            modelAssignmentGrades.put(groupName, classGrades);
        }
        return modelAssignmentGrades;
    }
}
