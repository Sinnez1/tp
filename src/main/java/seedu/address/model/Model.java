package seedu.address.model;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;
    String ALL_STUDENTS_VIEW_NAME = "No Group Selected";

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
     * Returns true if a group with the same identity as {@code group} exists in the address book.
     */
    boolean hasGroup(Group group);

    /**
     * Returns the group with the given name if it exists.
     */
    Optional<Group> findGroupByName(GroupName groupName);

    /**
     * Adds the given group.
     * {@code group} must not already exist in the address book.
     */
    void addGroup(Group group);

    /**
     * Deletes the given group.
     * The group must exist in the address book.
     */
    void deleteGroup(Group target);

    /**
     * Replaces the given group {@code target} with {@code editedGroup}.
     */
    void setGroup(Group target, Group editedGroup);

    /** Returns an unmodifiable view of the group list. */
    ObservableList<Group> getGroupList();

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list within the current view to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Updates the filter of the filtered person list within the current view to filter by the given {@code predicate}
     * and sorts it using the given {@code comparator}.
     * @throws NullPointerException if {@code predicate} or {@code comparator} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate, Comparator<Person> comparator);

    /** Switches the current view to all students and shows all students. */
    void switchToAllStudentsView();

    /** Switches the current view to the given group and shows all students in that group. */
    void switchToGroupView(GroupName groupName);

    /**
     * Returns the current active group, or empty if the current view is all students.
     */
    Optional<GroupName> getActiveGroupName();

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

    /** Returns the current active group property. */
    ReadOnlyObjectProperty<GroupName> activeGroupNameProperty();

    /** Returns the current active session date property. */
    ReadOnlyObjectProperty<LocalDate> activeSessionDateProperty();

    /** Sets whether attendance view mode is active. */
    void setAttendanceViewActive(boolean isActive);

    /** Returns whether attendance view mode is active. */
    boolean isAttendanceViewActive();

    /** Returns the attendance view mode property. */
    ReadOnlyBooleanProperty attendanceViewActiveProperty();

    /** Returns the visible session range start, if any. */
    Optional<LocalDate> getVisibleSessionRangeStart();

    /** Returns the visible session range end, if any. */
    Optional<LocalDate> getVisibleSessionRangeEnd();

    /** Sets the visible session range. Null values clear the respective bound. */
    void setVisibleSessionRange(LocalDate startDate, LocalDate endDate);

    /** Clears the visible session range. */
    void clearVisibleSessionRange();

    /** Returns the visible session range start property. */
    ReadOnlyObjectProperty<LocalDate> visibleSessionRangeStartProperty();

    /** Returns the visible session range end property. */
    ReadOnlyObjectProperty<LocalDate> visibleSessionRangeEndProperty();
}
