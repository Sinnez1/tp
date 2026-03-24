package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;
    private final MatricNumber matricNumber;

    // Data fields
    private final Set<Tag> tags = new HashSet<>();
    private final Set<ClassSpaceName> classSpaces = new HashSet<>();

    // Session fields (to be refactored into Session class)
    private final Attendance attendance;
    private final Participation participation;

    private final Map<ClassSpaceName, SessionList> classSpaceSessions = new HashMap<>();
    private final Map<ClassSpaceName, Map<AssignmentName, Integer>> assignmentGrades = new HashMap<>();

    /**
     * Used for AddCommand. Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, MatricNumber matricNumber, Set<Tag> tags) {
        this(name, phone, email, matricNumber, tags,
                Collections.emptySet(), new Attendance(Attendance.Status.UNINITIALISED), new Participation(0),
                new HashMap<>(), new HashMap<>()
        );
    }

    /**
     * Used for AddCommand. Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, MatricNumber matricNumber, Set<ClassSpaceName> classSpaces,
                  Set<Tag> tags) {
        this(name, phone, email, matricNumber, tags, classSpaces,
                new Attendance(Attendance.Status.UNINITIALISED), new Participation(0), new HashMap<>(),
                new HashMap<>()
        );
    }

    /**
     * Used for EditCommand. Every field must be present and not null.
     */
    public Person(Person person, Name name, Phone phone, Email email, MatricNumber matricNumber, Set<Tag> tags) {
        this(name, phone, email, matricNumber, tags,
                person.classSpaces, person.attendance, person.participation, person.classSpaceSessions,
                person.assignmentGrades);
    }

    /**
     * Used for Attendance commands. Every field must be present and not null.
     */
    public Person(Person person, Attendance attendance) {
        this(person.name, person.phone, person.email, person.matricNumber, person.tags, person.classSpaces,
                attendance, person.participation, person.classSpaceSessions, person.assignmentGrades);
    }

    /**
     * Used for Participation commands. Every field must be present and not null.
     */
    public Person(Person person, Participation participation) {
        this(person.name, person.phone, person.email, person.matricNumber, person.tags, person.classSpaces,
                person.attendance, participation, person.classSpaceSessions, person.assignmentGrades
        );
    }

    /**
     * Used for Group commands. Every field must be present and not null.
     */
    public Person(Person person, Set<ClassSpaceName> classSpaces) {
        this(person.name, person.phone, person.email, person.matricNumber, person.tags, classSpaces,
                person.attendance, person.participation, person.classSpaceSessions, person.assignmentGrades
        );
    }

    /**
     * Used for Session commands. Every field must be present and not null.
     */
    public Person(Person person, Map<ClassSpaceName, SessionList> updatedSessionMap) {
        this(person.name, person.phone, person.email, person.matricNumber, person.tags, person.classSpaces,
                person.attendance, person.participation, updatedSessionMap, person.assignmentGrades);
    }

    /**
     * Used for assignment-grade updates. Every field must be present and not null.
     */
    public Person(Person person, Map<ClassSpaceName, Map<AssignmentName, Integer>> updatedAssignmentGrades,
                  boolean ignored) {
        this(person.name, person.phone, person.email, person.matricNumber, person.tags, person.classSpaces,
                person.attendance, person.participation, person.classSpaceSessions, updatedAssignmentGrades);
    }

    private Person(Name name,
                   Phone phone,
                   Email email,
                   MatricNumber matricNumber,
                   Set<Tag> tags,
                   Set<ClassSpaceName> classSpaces,
                   Attendance attendance,
                   Participation participation,
                   Map<ClassSpaceName, SessionList> classSpaceSessions,
                   Map<ClassSpaceName, Map<AssignmentName, Integer>> assignmentGrades) {
        requireAllNonNull(name, phone, email, matricNumber, attendance, participation, tags, classSpaces,
                classSpaceSessions, assignmentGrades);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.matricNumber = matricNumber;
        this.tags.addAll(tags);
        this.classSpaces.addAll(classSpaces);
        this.attendance = attendance;
        this.participation = participation;
        this.classSpaceSessions.putAll(copySessionMap(classSpaceSessions));
        this.assignmentGrades.putAll(copyAssignmentGradeMap(assignmentGrades));
    }

    /**
     * Returns a copy of the {@code Person} with the specified {@code Session} added or overwritten.
     * Preserves immutability of {@code Person}.
     *
     * @param classSpaceName Class Space of the person.
     * @param newSession Session to be updated or added.
     * @return {@code Person} object with updated {@code Session} information.
     */
    public Person withUpdatedSession(ClassSpaceName classSpaceName, Session newSession) {
        Map<ClassSpaceName, SessionList> updatedSessionMap = copySessionMap(this.classSpaceSessions);
        SessionList currentSessionList = updatedSessionMap.getOrDefault(classSpaceName, new SessionList());

        // Create a copy of the SessionList and add or overwrite the session.
        SessionList newSessionList = new SessionList(currentSessionList.getSessions());
        newSessionList.addSession(newSession);

        // Update the map.
        updatedSessionMap.put(classSpaceName, newSessionList);
        return new Person(this.name, this.phone, this.email, this.matricNumber, this.tags, this.classSpaces,
                this.attendance, this.participation, updatedSessionMap, this.assignmentGrades);
    }

    /**
     * Returns a copy of the {@code Person} with the specified session removed from the class space.
     */
    public Person withoutSession(ClassSpaceName classSpaceName, LocalDate date) {
        requireAllNonNull(classSpaceName, date);
        Map<ClassSpaceName, SessionList> updatedSessionMap = copySessionMap(this.classSpaceSessions);
        SessionList currentSessionList = updatedSessionMap.get(classSpaceName);
        if (currentSessionList == null) {
            return this;
        }

        SessionList newSessionList = new SessionList(currentSessionList.getSessions());
        boolean removed = newSessionList.removeSession(date);
        if (!removed) {
            return this;
        }

        if (newSessionList.getSessions().isEmpty()) {
            updatedSessionMap.remove(classSpaceName);
        } else {
            updatedSessionMap.put(classSpaceName, newSessionList);
        }

        return new Person(this.name, this.phone, this.email, this.matricNumber, this.tags, this.classSpaces,
                this.attendance, this.participation, updatedSessionMap, this.assignmentGrades);
    }

    /**
     * Returns a copy of the {@code Person} with the given assignment grade added or overwritten.
     */
    public Person withUpdatedAssignmentGrade(ClassSpaceName classSpaceName, AssignmentName assignmentName, int grade) {
        requireAllNonNull(classSpaceName, assignmentName);
        Map<ClassSpaceName, Map<AssignmentName, Integer>> updatedAssignmentGrades =
                copyAssignmentGradeMap(this.assignmentGrades);
        Map<AssignmentName, Integer> classAssignmentGrades =
                updatedAssignmentGrades.getOrDefault(classSpaceName, new HashMap<>());
        classAssignmentGrades.put(assignmentName, grade);
        updatedAssignmentGrades.put(classSpaceName, classAssignmentGrades);
        return new Person(this.name, this.phone, this.email, this.matricNumber, this.tags, this.classSpaces,
                this.attendance, this.participation, this.classSpaceSessions, updatedAssignmentGrades);
    }

    /**
     * Returns a copy of the {@code Person} with all data for the specified class space removed.
     */
    public Person withoutClassSpaceData(ClassSpaceName classSpaceName) {
        requireAllNonNull(classSpaceName);
        Set<ClassSpaceName> updatedClassSpaces = new HashSet<>(this.classSpaces);
        updatedClassSpaces.remove(classSpaceName);

        Map<ClassSpaceName, SessionList> updatedSessionMap = copySessionMap(this.classSpaceSessions);
        updatedSessionMap.remove(classSpaceName);

        Map<ClassSpaceName, Map<AssignmentName, Integer>> updatedAssignmentGrades =
                copyAssignmentGradeMap(this.assignmentGrades);
        updatedAssignmentGrades.remove(classSpaceName);

        // consider converting this to Wither Pattern
        return new Person(this.name, this.phone, this.email, this.matricNumber, this.tags, updatedClassSpaces,
                this.attendance, this.participation, updatedSessionMap, updatedAssignmentGrades);

    }

    /**
     * Returns a copy of the {@code Person} with the class space renamed across all relevant data.
     */
    public Person withRenamedClassSpace(ClassSpaceName oldClassSpaceName, ClassSpaceName newClassSpaceName) {
        requireAllNonNull(oldClassSpaceName, newClassSpaceName);

        Set<ClassSpaceName> updatedClassSpaces = new HashSet<>(this.classSpaces);
        if (updatedClassSpaces.remove(oldClassSpaceName)) {
            updatedClassSpaces.add(newClassSpaceName);
        }

        Map<ClassSpaceName, SessionList> updatedSessionMap = copySessionMap(this.classSpaceSessions);
        SessionList existingSessions = updatedSessionMap.remove(oldClassSpaceName);
        if (existingSessions != null) {
            updatedSessionMap.put(newClassSpaceName, existingSessions);
        }

        Map<ClassSpaceName, Map<AssignmentName, Integer>> updatedAssignmentGrades =
                copyAssignmentGradeMap(this.assignmentGrades);
        Map<AssignmentName, Integer> existingGrades = updatedAssignmentGrades.remove(oldClassSpaceName);
        if (existingGrades != null) {
            updatedAssignmentGrades.put(newClassSpaceName, existingGrades);
        }

        return new Person(this.name, this.phone, this.email, this.matricNumber, this.tags, updatedClassSpaces,
                this.attendance, this.participation, updatedSessionMap, updatedAssignmentGrades);
    }

    /**
     * Returns a copy of the {@code Person} with the specified assignment grade removed.
     */
    public Person withoutAssignmentGrade(ClassSpaceName classSpaceName, AssignmentName assignmentName) {
        requireAllNonNull(classSpaceName, assignmentName);
        Map<ClassSpaceName, Map<AssignmentName, Integer>> updatedAssignmentGrades =
                copyAssignmentGradeMap(this.assignmentGrades);
        Map<AssignmentName, Integer> classAssignmentGrades = updatedAssignmentGrades.get(classSpaceName);
        if (classAssignmentGrades == null) {
            return this;
        }
        classAssignmentGrades.remove(assignmentName);
        if (classAssignmentGrades.isEmpty()) {
            updatedAssignmentGrades.remove(classSpaceName);
        } else {
            updatedAssignmentGrades.put(classSpaceName, classAssignmentGrades);
        }
        return new Person(this.name, this.phone, this.email, this.matricNumber, this.tags, this.classSpaces,
                this.attendance, this.participation, this.classSpaceSessions, updatedAssignmentGrades);
    }

    /**
     * Returns a copy of the {@code Person} with the specified assignment grade key renamed.
     */
    public Person withRenamedAssignmentGrade(ClassSpaceName classSpaceName, AssignmentName oldAssignmentName,
                                             AssignmentName newAssignmentName) {
        requireAllNonNull(classSpaceName, oldAssignmentName, newAssignmentName);
        Map<ClassSpaceName, Map<AssignmentName, Integer>> updatedAssignmentGrades =
                copyAssignmentGradeMap(this.assignmentGrades);
        Map<AssignmentName, Integer> classAssignmentGrades = updatedAssignmentGrades.get(classSpaceName);
        if (classAssignmentGrades == null || !classAssignmentGrades.containsKey(oldAssignmentName)) {
            return this;
        }
        Integer existingGrade = classAssignmentGrades.remove(oldAssignmentName);
        classAssignmentGrades.put(newAssignmentName, existingGrade);
        updatedAssignmentGrades.put(classSpaceName, classAssignmentGrades);
        return new Person(this.name, this.phone, this.email, this.matricNumber, this.tags, this.classSpaces,
                this.attendance, this.participation, this.classSpaceSessions, updatedAssignmentGrades);
    }

    /**
     * Returns a {@code Map<ClassSpaceName, SessionList>}.
     * Represents the sessions for a class space.
     *
     * @return {@code Map<ClassSpaceName, SessionList>}.
     */
    public Map<ClassSpaceName, SessionList> getClassSpaceSessions() {
        return Collections.unmodifiableMap(classSpaceSessions);
    }

    /**
     * Returns the assignment grade map.
     */
    public Map<ClassSpaceName, Map<AssignmentName, Integer>> getAssignmentGrades() {
        return assignmentGrades.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey,
                        entry -> Collections.unmodifiableMap(entry.getValue())));
    }

    /**
     * Returns the grade for the specified assignment in the given class space, if it exists.
     */
    public Optional<Integer> getAssignmentGrade(ClassSpaceName classSpaceName, AssignmentName assignmentName) {
        requireAllNonNull(classSpaceName, assignmentName);
        return Optional.ofNullable(assignmentGrades.getOrDefault(classSpaceName, Collections.emptyMap())
                .get(assignmentName));
    }

    /**
     * Returns the session that belongs to a class space for a given date.
     * Creates the session if it does not exist yet.
     *
     * @param classSpaceName Class space name.
     * @param date Date of session.
     * @return Session belonging to the class space for a given date.
     */
    public Session getOrCreateSession(ClassSpaceName classSpaceName, LocalDate date) {
        return classSpaceSessions.getOrDefault(classSpaceName, new SessionList())
                .getSession(date)
                .orElseGet(() -> new Session(date,
                        new Attendance(Attendance.Status.UNINITIALISED), new Participation(0), ""));
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public MatricNumber getMatricNumber() {
        return matricNumber;
    }

    /**
     * Returns the attendance for the specified class space and session date.
     * If the session does not exist, returns UNINITIALISED attendance.
     *
     * @param classSpaceName Class space name.
     * @param date Date of session.
     * @return Attendance for the specified session.
     */
    public Attendance getAttendance(ClassSpaceName classSpaceName, LocalDate date) {
        requireAllNonNull(classSpaceName, date);
        return getOrCreateSession(classSpaceName, date).getAttendance();
    }

    // TODO: Remove. This is legacy from pre-Session class.
    public Attendance getAttendance() {
        return attendance;
    }

    /**
     * Returns the participation for the specified class space and session date.
     * If the session does not exist, returns 0 participation.
     *
     * @param classSpaceName Class space name.
     * @param date Date of session.
     * @return Participation for the specified session.
     */
    public Participation getParticipation(ClassSpaceName classSpaceName, LocalDate date) {
        requireAllNonNull(classSpaceName, date);
        return getOrCreateSession(classSpaceName, date).getParticipation();
    }

    // TODO: Remove. This is legacy from pre-Session class.
    public Participation getParticipation() {
        return participation;
    }

    /**
     * Returns the note for the specified class space and session date.
     * If the session does not exist, returns an empty note.
     */
    public String getSessionNote(ClassSpaceName classSpaceName, LocalDate date) {
        requireAllNonNull(classSpaceName, date);
        return getOrCreateSession(classSpaceName, date).getNote();
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns an immutable class space set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<ClassSpaceName> getClassSpaces() {
        return Collections.unmodifiableSet(classSpaces);
    }

    /**
     * Returns true if the person belongs to the specified class space.
     */
    public boolean hasClassSpace(ClassSpaceName classSpaceName) {
        return classSpaces.contains(classSpaceName);
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getMatricNumber().value.equalsIgnoreCase(getMatricNumber().value);
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && matricNumber.equals(otherPerson.matricNumber)
                && attendance.equals(otherPerson.attendance) // TODO: Remove. This is legacy from pre-Session class.
                && participation.equals(otherPerson.participation) // TODO: Remove. This is legacy pre-Session class.
                && tags.equals(otherPerson.tags)
                && classSpaces.equals(otherPerson.classSpaces)
                && classSpaceSessions.equals(otherPerson.classSpaceSessions)
                && assignmentGrades.equals(otherPerson.assignmentGrades);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, matricNumber,
                attendance, participation, // TODO: Remove. This is legacy from pre-Session class.
                tags, classSpaces, classSpaceSessions, assignmentGrades);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("matricNumber", matricNumber)
                //.add("participation", participation) // TODO: Remove after Participation is removed from Person
                .add("tags", tags)
                .add("groups", classSpaces)
                .toString();
    }

    private static Map<ClassSpaceName, SessionList> copySessionMap(Map<ClassSpaceName, SessionList> source) {
        Map<ClassSpaceName, SessionList> copiedMap = new HashMap<>();
        source.forEach((classSpaceName, sessionList) ->
                copiedMap.put(classSpaceName, new SessionList(sessionList.getSessions())));
        return copiedMap;
    }

    private static Map<ClassSpaceName, Map<AssignmentName, Integer>> copyAssignmentGradeMap(
            Map<ClassSpaceName, Map<AssignmentName, Integer>> source) {
        Map<ClassSpaceName, Map<AssignmentName, Integer>> copiedMap = new HashMap<>();
        source.forEach((classSpaceName, assignmentGradeMap) ->
                copiedMap.put(classSpaceName, new HashMap<>(assignmentGradeMap)));
        return copiedMap;
    }
}
