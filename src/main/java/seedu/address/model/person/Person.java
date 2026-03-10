package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
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

    // Data fields
    private final MatricNumber matricNumber;
    private final Participation participation;
    private final Set<Tag> tags = new HashSet<>();
    private final Set<ClassSpaceName> classSpaces = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, MatricNumber matricNumber,
                  Participation participation, Set<Tag> tags) {
        this(name, phone, email, matricNumber, participation, tags, Collections.emptySet());
    }

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, MatricNumber matricNumber,
                  Participation participation, Set<Tag> tags,
                  Set<ClassSpaceName> classSpaces) {
        requireAllNonNull(name, phone, email, matricNumber, participation, tags, classSpaces);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.matricNumber = matricNumber;
        this.participation = participation;
        this.tags.addAll(tags);
        this.classSpaces.addAll(classSpaces);
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
                && participation.equals(otherPerson.participation)
                && tags.equals(otherPerson.tags)
                && classSpaces.equals(otherPerson.classSpaces);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, matricNumber, participation, tags, classSpaces);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("matricNumber", matricNumber)
                .add("participation", participation)
                .add("tags", tags)
                .add("classSpaces", classSpaces)
                .toString();
    }

}
