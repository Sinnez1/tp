package seedu.address.model;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.classspace.ClassSpace;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;
    String ALL_STUDENTS_VIEW_NAME = "All Students";

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Sets the user prefs' address book file path.
     */
    void setAddressBookFilePath(Path addressBookFilePath);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Returns the person with the given matric number if it exists.
     */
    Optional<Person> findPersonByMatricNumber(MatricNumber matricNumber);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /**
     * Returns true if a class space with the same identity as {@code classSpace} exists in the address book.
     */
    boolean hasClassSpace(ClassSpace classSpace);

    /**
     * Returns the class space with the given name if it exists.
     */
    Optional<ClassSpace> findClassSpaceByName(ClassSpaceName classSpaceName);

    /**
     * Adds the given class space.
     * {@code classSpace} must not already exist in the address book.
     */
    void addClassSpace(ClassSpace classSpace);

    /**
     * Deletes the given class space.
     * The class space must exist in the address book.
     */
    void deleteClassSpace(ClassSpace target);

    /**
     * Replaces the given class space {@code target} with {@code editedClassSpace}.
     */
    void setClassSpace(ClassSpace target, ClassSpace editedClassSpace);

    /** Returns an unmodifiable view of the class space list. */
    ObservableList<ClassSpace> getClassSpaceList();

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list within the current view to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /** Switches the current view to all students and shows all students. */
    void switchToAllStudentsView();

    /** Switches the current view to the given class space and shows all students in that class space. */
    void switchToClassSpaceView(ClassSpaceName classSpaceName);

    /**
     * Returns the current active class space, or empty if the current view is all students.
     */
    Optional<ClassSpaceName> getActiveClassSpaceName();

    /**
     * Returns the current active session date, or empty if no session is selected.
     */
    Optional<LocalDate> getActiveSessionDate();

    /**
     * Sets the current active session date.
     */
    void setActiveSessionDate(LocalDate date);

    /**
     * Clears the current active session date.
     */
    void clearActiveSessionDate();

    /** Returns the current view label property. */
    ReadOnlyStringProperty currentViewProperty();

    /** Returns the current active class space property. */
    ReadOnlyObjectProperty<ClassSpaceName> activeClassSpaceNameProperty();

    /** Returns the current active session date property. */
    ReadOnlyObjectProperty<LocalDate> activeSessionDateProperty();

    /** Sets whether attendance view mode is active. */
    void setAttendanceViewActive(boolean isActive);

    /** Returns whether attendance view mode is active. */
    boolean isAttendanceViewActive();

    /** Returns the attendance view mode property. */
    ReadOnlyBooleanProperty attendanceViewActiveProperty();
}
