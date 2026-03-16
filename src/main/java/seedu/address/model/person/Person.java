package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
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
    /**
     * Used for AddCommand. Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, MatricNumber matricNumber, Set<Tag> tags) {
        this(name, phone, email, matricNumber, tags,
                Collections.emptySet(), new Attendance(Attendance.Status.UNINITIALISED), new Participation(0),
                new HashMap<>()
        );
    }

    /**
     * Used for AddCommand. Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, MatricNumber matricNumber, Set<ClassSpaceName> classSpaces,
                  Set<Tag> tags) {
        this(name, phone, email, matricNumber, tags, classSpaces,
                new Attendance(Attendance.Status.UNINITIALISED), new Participation(0), new HashMap<>()
        );
    }

    /**
     * Used for EditCommand. Every field must be present and not null.
     */
    public Person(Person person, Name name, Phone phone, Email email, MatricNumber matricNumber, Set<Tag> tags) {
        this(name, phone, email, matricNumber, tags,
                person.classSpaces, person.attendance, person.participation, person.classSpaceSessions);
    }

    /**
     * Used for Attendance commands. Every field must be present and not null.
     */
    public Person(Person person, Attendance attendance) {
        this(person.name, person.phone, person.email, person.matricNumber, person.tags, person.classSpaces,
                attendance, person.participation, person.classSpaceSessions);
    }

    /**
     * Used for Participation commands. Every field must be present and not null.
     */
    public Person(Person person, Participation participation) {
        this(person.name, person.phone, person.email, person.matricNumber, person.tags, person.classSpaces,
                person.attendance, participation, person.classSpaceSessions
        );
    }

    /**
     * Used for ClassSpace commands. Every field must be present and not null.
     */
    public Person(Person person, Set<ClassSpaceName> classSpaces) {
        this(person.name, person.phone, person.email, person.matricNumber, person.tags,
                classSpaces,
                person.attendance, person.participation, person.classSpaceSessions
        );
    }

    private Person(Name name,
                   Phone phone,
                   Email email,
                   MatricNumber matricNumber,
                   Set<Tag> tags,
                   Set<ClassSpaceName> classSpaces,
                   Attendance attendance,
                   Participation participation,
                   Map<ClassSpaceName, SessionList> classSpaceSessions) {
        requireAllNonNull(name, phone, email, matricNumber, attendance, participation, tags, classSpaces);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.matricNumber = matricNumber;
        this.tags.addAll(tags);
        this.classSpaces.addAll(classSpaces);
        this.attendance = attendance;
        this.participation = participation;
        this.classSpaceSessions.putAll(classSpaceSessions);
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
        // Copy the existing map.
        Map<ClassSpaceName, SessionList> updatedMap = new HashMap<>(this.classSpaceSessions);

        // Get the existing list of sessions for this class space or create a new one.
        SessionList currentSessionList = updatedMap.getOrDefault(classSpaceName, new SessionList());

        // Create a copy of the SessionList and add or overwrite the session.
        SessionList newSessionList = new SessionList(currentSessionList.getSessions());
        newSessionList.addSession(newSession);

        // Update the map.
        updatedMap.put(classSpaceName, newSessionList);

        // Return a new Person with the updated map.
        return new Person(name, phone, email, matricNumber, tags, classSpaces, attendance, participation, updatedMap);
    }
    /**
     * Returns a {@code Map<ClassSpaceName, SessionList>}.
     * Represents the sessions for a class space.
     *
     * @return {@code Map<ClassSpaceName, SessionLsit>}.
     */
    public Map<ClassSpaceName, SessionList> getClassSpaceSessions() {
        return Collections.unmodifiableMap(classSpaceSessions);
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
                        new Attendance(Attendance.Status.UNINITIALISED), new Participation(0)));
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

    public Attendance getAttendance() {
        return attendance;
    }

    public Participation getParticipation() {
        return participation;
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

        // instanceof handles nulls
        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && matricNumber.equals(otherPerson.matricNumber)
                && attendance.equals(otherPerson.attendance)
                && participation.equals(otherPerson.participation)
                && tags.equals(otherPerson.tags)
                && classSpaces.equals(otherPerson.classSpaces)
                && classSpaceSessions.equals(otherPerson.classSpaceSessions);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, matricNumber, attendance, participation, tags, classSpaces,
                classSpaceSessions);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("matricNumber", matricNumber)
                //.add("participation", participation) // TODO: This is causing PersonTest.toStringMethod to fail
                .add("tags", tags)
                .add("classSpaces", classSpaces)
                // .add("classSpaceSessions", classSpaceSessions) // TODO: check if necessary
                .toString();
    }

}
