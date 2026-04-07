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
import seedu.address.model.group.GroupName;
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
    private final Set<GroupName> groups = new HashSet<>();

    // Session fields (stored per group)
    private final Map<GroupName, SessionList> groupSessions = new HashMap<>();
    private final Map<GroupName, Map<AssignmentName, Integer>> assignmentGrades = new HashMap<>();

    /**
     * Used for AddCommand. Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, MatricNumber matricNumber, Set<Tag> tags) {
        this(name, phone, email, matricNumber, tags,
                Collections.emptySet(), new HashMap<>(), new HashMap<>()
        );
    }

    /**
     * Used for AddCommand. Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, MatricNumber matricNumber, Set<GroupName> groups,
                  Set<Tag> tags) {
        this(name, phone, email, matricNumber, tags, groups, new HashMap<>(), new HashMap<>());
    }

    /**
     * Used for EditCommand. Every field must be present and not null.
     */
    public Person(Person person, Name name, Phone phone, Email email, MatricNumber matricNumber, Set<Tag> tags) {
        this(name, phone, email, matricNumber, tags,
                person.groups, person.groupSessions, person.assignmentGrades);
    }

    /**
     * Used for Group commands. Every field must be present and not null.
     */
    public Person(Person person, Set<GroupName> groups) {
        this(person.name, person.phone, person.email, person.matricNumber, person.tags,
                groups,
                person.groupSessions, person.assignmentGrades
        );
    }

    /**
     * Used for Session commands. Every field must be present and not null.
     */
    public Person(Person person, Map<GroupName, SessionList> updatedSessionMap) {
        this(person.name, person.phone, person.email, person.matricNumber, person.tags, person.groups,
                updatedSessionMap,
                person.assignmentGrades);
    }

    /**
     * Used for assignment-grade updates. Every field must be present and not null.
     */
    public Person(Person person, Map<GroupName, Map<AssignmentName, Integer>> updatedAssignmentGrades,
                  boolean ignored) {
        this(person.name, person.phone, person.email, person.matricNumber, person.tags, person.groups,
                person.groupSessions,
                updatedAssignmentGrades);
    }

    private Person(Name name,
                   Phone phone,
                   Email email,
                   MatricNumber matricNumber,
                   Set<Tag> tags,
                   Set<GroupName> groups,
                   Map<GroupName, SessionList> groupSessions,
                   Map<GroupName, Map<AssignmentName, Integer>> assignmentGrades) {
        requireAllNonNull(name, phone, email, matricNumber, tags, groups, groupSessions, assignmentGrades);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.matricNumber = matricNumber;
        this.tags.addAll(tags);
        this.groups.addAll(groups);
        this.groupSessions.putAll(copySessionMap(groupSessions));
        this.assignmentGrades.putAll(copyAssignmentGradeMap(assignmentGrades));
    }

    /**
     * Returns a copy of the {@code Person} with the specified {@code Session} added or overwritten.
     * Preserves immutability of {@code Person}.
     *
     * @param groupName Group of the person.
     * @param newSession Session to be updated or added.
     * @return {@code Person} object with updated {@code Session} information.
     */
    public Person withUpdatedSession(GroupName groupName, Session newSession) {
        Map<GroupName, SessionList> updatedSessionMap = copySessionMap(this.groupSessions);
        SessionList currentSessionList = updatedSessionMap.getOrDefault(groupName, new SessionList());

        // Create a copy of the SessionList and add or overwrite the session.
        SessionList newSessionList = new SessionList(currentSessionList.getSessions());
        newSessionList.addSession(newSession);

        // Update the map.
        updatedSessionMap.put(groupName, newSessionList);
        return new Person(this.name, this.phone, this.email, this.matricNumber, this.tags, this.groups,
                updatedSessionMap,
                this.assignmentGrades);
    }

    /**
     * Returns a copy of the {@code Person} with the specified session removed from the group.
     */
    public Person withoutSession(GroupName groupName, LocalDate date) {
        requireAllNonNull(groupName, date);
        Map<GroupName, SessionList> updatedSessionMap = copySessionMap(this.groupSessions);
        SessionList currentSessionList = updatedSessionMap.get(groupName);
        if (currentSessionList == null) {
            return this;
        }

        SessionList newSessionList = new SessionList(currentSessionList.getSessions());
        boolean removed = newSessionList.removeSession(date);
        if (!removed) {
            return this;
        }

        if (newSessionList.getSessions().isEmpty()) {
            updatedSessionMap.remove(groupName);
        } else {
            updatedSessionMap.put(groupName, newSessionList);
        }

        return new Person(this.name, this.phone, this.email, this.matricNumber, this.tags, this.groups,
                updatedSessionMap,
                this.assignmentGrades);
    }

    /**
     * Returns a copy of the {@code Person} with the given assignment grade added or overwritten.
     */
    public Person withUpdatedAssignmentGrade(GroupName groupName, AssignmentName assignmentName, int grade) {
        requireAllNonNull(groupName, assignmentName);
        Map<GroupName, Map<AssignmentName, Integer>> updatedAssignmentGrades =
                copyAssignmentGradeMap(this.assignmentGrades);
        Map<AssignmentName, Integer> classAssignmentGrades =
                updatedAssignmentGrades.getOrDefault(groupName, new HashMap<>());
        classAssignmentGrades.put(assignmentName, grade);
        updatedAssignmentGrades.put(groupName, classAssignmentGrades);
        return new Person(this.name, this.phone, this.email, this.matricNumber, this.tags, this.groups,
                this.groupSessions,
                updatedAssignmentGrades);
    }

    /**
     * Returns a copy of the {@code Person} with all data for the specified group removed.
     */
    public Person withoutGroupData(GroupName groupName) {
        requireAllNonNull(groupName);
        Set<GroupName> updatedGroups = new HashSet<>(this.groups);
        updatedGroups.remove(groupName);

        Map<GroupName, SessionList> updatedSessionMap = copySessionMap(this.groupSessions);
        updatedSessionMap.remove(groupName);

        Map<GroupName, Map<AssignmentName, Integer>> updatedAssignmentGrades =
                copyAssignmentGradeMap(this.assignmentGrades);
        updatedAssignmentGrades.remove(groupName);

        // consider converting this to Wither Pattern
        return new Person(this.name, this.phone, this.email, this.matricNumber, this.tags,
                updatedGroups, updatedSessionMap, updatedAssignmentGrades);

    }

    /**
     * Returns a copy of the {@code Person} with the group renamed across all relevant data.
     */
    public Person withRenamedGroup(GroupName oldGroupName, GroupName newGroupName) {
        requireAllNonNull(oldGroupName, newGroupName);

        Set<GroupName> updatedGroups = new HashSet<>(this.groups);
        if (updatedGroups.remove(oldGroupName)) {
            updatedGroups.add(newGroupName);
        }

        Map<GroupName, SessionList> updatedSessionMap = copySessionMap(this.groupSessions);
        SessionList existingSessions = updatedSessionMap.remove(oldGroupName);
        if (existingSessions != null) {
            updatedSessionMap.put(newGroupName, existingSessions);
        }

        Map<GroupName, Map<AssignmentName, Integer>> updatedAssignmentGrades =
                copyAssignmentGradeMap(this.assignmentGrades);
        Map<AssignmentName, Integer> existingGrades = updatedAssignmentGrades.remove(oldGroupName);
        if (existingGrades != null) {
            updatedAssignmentGrades.put(newGroupName, existingGrades);
        }

        return new Person(this.name, this.phone, this.email, this.matricNumber, this.tags,
                updatedGroups, updatedSessionMap, updatedAssignmentGrades);
    }

    /**
     * Returns a copy of the {@code Person} with the specified assignment grade removed.
     */
    public Person withoutAssignmentGrade(GroupName groupName, AssignmentName assignmentName) {
        requireAllNonNull(groupName, assignmentName);
        Map<GroupName, Map<AssignmentName, Integer>> updatedAssignmentGrades =
                copyAssignmentGradeMap(this.assignmentGrades);
        Map<AssignmentName, Integer> classAssignmentGrades = updatedAssignmentGrades.get(groupName);
        if (classAssignmentGrades == null) {
            return this;
        }
        classAssignmentGrades.remove(assignmentName);
        if (classAssignmentGrades.isEmpty()) {
            updatedAssignmentGrades.remove(groupName);
        } else {
            updatedAssignmentGrades.put(groupName, classAssignmentGrades);
        }
        return new Person(this.name, this.phone, this.email, this.matricNumber, this.tags, this.groups,
                this.groupSessions,
                updatedAssignmentGrades);
    }

    /**
     * Returns a copy of the {@code Person} with the specified assignment grade key renamed.
     */
    public Person withRenamedAssignmentGrade(GroupName groupName, AssignmentName oldAssignmentName,
                                             AssignmentName newAssignmentName) {
        requireAllNonNull(groupName, oldAssignmentName, newAssignmentName);
        Map<GroupName, Map<AssignmentName, Integer>> updatedAssignmentGrades =
                copyAssignmentGradeMap(this.assignmentGrades);
        Map<AssignmentName, Integer> classAssignmentGrades = updatedAssignmentGrades.get(groupName);
        if (classAssignmentGrades == null || !classAssignmentGrades.containsKey(oldAssignmentName)) {
            return this;
        }
        Integer existingGrade = classAssignmentGrades.remove(oldAssignmentName);
        classAssignmentGrades.put(newAssignmentName, existingGrade);
        updatedAssignmentGrades.put(groupName, classAssignmentGrades);
        return new Person(this.name, this.phone, this.email, this.matricNumber, this.tags, this.groups,
                this.groupSessions,
                updatedAssignmentGrades);
    }

    /**
     * Returns a {@code Map<GroupName, SessionList>}.
     * Represents the sessions for a group.
     *
     * @return {@code Map<GroupName, SessionList>}.
     */
    public Map<GroupName, SessionList> getGroupSessions() {
        return Collections.unmodifiableMap(groupSessions);
    }

    /**
     * Returns the assignment grade map.
     */
    public Map<GroupName, Map<AssignmentName, Integer>> getAssignmentGrades() {
        return assignmentGrades.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey,
                        entry -> Collections.unmodifiableMap(entry.getValue())));
    }

    /**
     * Returns the grade for the specified assignment in the given group, if it exists.
     */
    public Optional<Integer> getAssignmentGrade(GroupName groupName, AssignmentName assignmentName) {
        requireAllNonNull(groupName, assignmentName);
        return Optional.ofNullable(assignmentGrades.getOrDefault(groupName, Collections.emptyMap())
                .get(assignmentName));
    }

    /**
     * Returns the session that belongs to a group for a given date.
     * Creates the session if it does not exist yet.
     *
     * @param groupName Group name.
     * @param date Date of session.
     * @return Session belonging to the group for a given date.
     */
    public Session getOrCreateSession(GroupName groupName, LocalDate date) {
        return groupSessions.getOrDefault(groupName, new SessionList())
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
     * Returns the attendance for the specified group and session date.
     * If the session does not exist, returns UNINITIALISED attendance.
     *
     * @param groupName Group name.
     * @param date Date of session.
     * @return Attendance for the specified session.
     */
    public Attendance getAttendance(GroupName groupName, LocalDate date) {
        requireAllNonNull(groupName, date);
        return getOrCreateSession(groupName, date).getAttendance();
    }

    /**
     * Returns the participation for the specified group and session date.
     * If the session does not exist, returns 0 participation.
     *
     * @param groupName Group name.
     * @param date Date of session.
     * @return Participation for the specified session.
     */
    public Participation getParticipation(GroupName groupName, LocalDate date) {
        requireAllNonNull(groupName, date);
        return getOrCreateSession(groupName, date).getParticipation();
    }

    /**
     * Returns the note for the specified group and session date.
     * If the session does not exist, returns an empty note.
     */
    public String getSessionNote(GroupName groupName, LocalDate date) {
        requireAllNonNull(groupName, date);
        return getOrCreateSession(groupName, date).getNote();
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns an immutable group set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<GroupName> getGroups() {
        return Collections.unmodifiableSet(groups);
    }

    /**
     * Returns true if the person belongs to the specified group.
     */
    public boolean hasGroup(GroupName groupName) {
        return groups.contains(groupName);
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

        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && matricNumber.equals(otherPerson.matricNumber)
                && tags.equals(otherPerson.tags)
                && groups.equals(otherPerson.groups)
                && groupSessions.equals(otherPerson.groupSessions)
                && assignmentGrades.equals(otherPerson.assignmentGrades);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, matricNumber, tags, groups, groupSessions, assignmentGrades);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("matricNumber", matricNumber)
                .add("tags", tags)
                .add("groups", groups)
                .toString();
    }

    private static Map<GroupName, SessionList> copySessionMap(Map<GroupName, SessionList> source) {
        Map<GroupName, SessionList> copiedMap = new HashMap<>();
        source.forEach((groupName, sessionList) ->
                copiedMap.put(groupName, new SessionList(sessionList.getSessions())));
        return copiedMap;
    }

    private static Map<GroupName, Map<AssignmentName, Integer>> copyAssignmentGradeMap(
            Map<GroupName, Map<AssignmentName, Integer>> source) {
        Map<GroupName, Map<AssignmentName, Integer>> copiedMap = new HashMap<>();
        source.forEach((groupName, assignmentGradeMap) ->
                copiedMap.put(groupName, new HashMap<>(assignmentGradeMap)));
        return copiedMap;
    }

    /**
     * Returns the Name of Person as a String.
     *
     * @return Name of Person as a String.
     */
    public String getNameValue() {
        return name.fullName;
    }

    /**
     * Returns Phone of Person as a String.
     *
     * @return  Phone of Person as a String.
     */
    public String getPhoneValue() {
        return phone.value;
    }

    /**
     * Returns Email of Person as a String.
     *
     * @return Email of Person as a String.
     */
    public String getEmailValue() {
        return email.value;
    }

    /**
     * Returns matriculation number of Person.
     *
     * @return Matriculation number of Person as a String.
     */
    public String getMatricNumberValue() {
        return matricNumber.value;
    }
}
