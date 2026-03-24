package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.classspace.Group;
import seedu.address.model.classspace.UniqueClassSpaceList;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed.
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons;
    private final UniqueClassSpaceList classSpaces;

    {
        persons = new UniquePersonList();
        classSpaces = new UniqueClassSpaceList();
    }

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Replaces the contents of the class space list with {@code groups}.
     * {@code groups} must not contain duplicate class spaces.
     */
    public void setClassSpaces(List<Group> groups) {
        this.classSpaces.setClassSpaces(groups);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
        setClassSpaces(newData.getClassSpaceList());
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
    }

    //// class-space-level operations

    /**
     * Returns true if a class space with the same identity as {@code group} exists in the address book.
     */
    public boolean hasClassSpace(Group group) {
        requireNonNull(group);
        return classSpaces.contains(group);
    }

    /**
     * Adds a class space to the address book.
     * The class space must not already exist in the address book.
     */
    public void addClassSpace(Group group) {
        classSpaces.add(group);
    }

    /**
     * Replaces the given class space {@code target} in the list with {@code editedGroup}.
     */
    public void setClassSpace(Group target, Group editedGroup) {
        requireNonNull(editedGroup);
        classSpaces.setClassSpace(target, editedGroup);
    }

    /**
     * Removes the given class space from the address book.
     */
    public void removeClassSpace(Group target) {
        classSpaces.remove(target);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("groups", classSpaces)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Group> getClassSpaceList() {
        return classSpaces.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddressBook)) {
            return false;
        }

        AddressBook otherAddressBook = (AddressBook) other;
        return persons.equals(otherAddressBook.persons)
                && classSpaces.equals(otherAddressBook.classSpaces);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(persons, classSpaces);
    }
}
