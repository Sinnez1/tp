package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Person;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final SortedList<Person> sortedFilteredPersons;
    private final SimpleStringProperty currentView;
    private final SimpleBooleanProperty attendanceViewActive;
    private final SimpleObjectProperty<GroupName> activeGroupName;
    private final SimpleObjectProperty<LocalDate> activeSessionDate;
    private final SimpleObjectProperty<LocalDate> visibleSessionRangeStart;
    private final SimpleObjectProperty<LocalDate> visibleSessionRangeEnd;

    private Predicate<Person> currentAdditionalPredicate;
    private Comparator<Person> currentComparator;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        sortedFilteredPersons = new SortedList<>(filteredPersons);
        currentView = new SimpleStringProperty(ALL_STUDENTS_VIEW_NAME);
        attendanceViewActive = new SimpleBooleanProperty(false);
        activeGroupName = new SimpleObjectProperty<>();
        activeSessionDate = new SimpleObjectProperty<>();
        visibleSessionRangeStart = new SimpleObjectProperty<>();
        visibleSessionRangeEnd = new SimpleObjectProperty<>();
        currentAdditionalPredicate = PREDICATE_SHOW_ALL_PERSONS;
        currentComparator = null;
        refreshFilteredPersonList();
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
        refreshFilteredPersonList();
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public Optional<Person> findPersonByMatricNumber(MatricNumber matricNumber) {
        requireNonNull(matricNumber);
        return addressBook.getPersonList().stream()
                .filter(person -> person.getMatricNumber().equals(matricNumber))
                .findFirst();
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
        refreshFilteredPersonList();
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        refreshFilteredPersonList();
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
        refreshFilteredPersonList();
    }

    @Override
    public boolean hasGroup(Group group) {
        requireNonNull(group);
        return addressBook.hasGroup(group);
    }

    @Override
    public Optional<Group> findGroupByName(GroupName groupName) {
        requireNonNull(groupName);
        return addressBook.getGroupList().stream()
                .filter(group -> group.getGroupName().equals(groupName))
                .findFirst();
    }

    @Override
    public void addGroup(Group group) {
        requireNonNull(group);
        addressBook.addGroup(group);
    }

    @Override
    public void deleteGroup(Group target) {
        requireNonNull(target);
        addressBook.removeGroup(target);
        if (target.getGroupName().equals(activeGroupName.get())) {
            switchToAllStudentsView();
            return;
        }
        refreshFilteredPersonList();
    }

    @Override
    public void setGroup(Group target, Group editedGroup) {
        requireAllNonNull(target, editedGroup);
        addressBook.setGroup(target, editedGroup);
        if (target.getGroupName().equals(activeGroupName.get())) {
            activeGroupName.set(editedGroup.getGroupName());
            updateCurrentViewLabel();
        }
        refreshFilteredPersonList();
    }

    @Override
    public ObservableList<Group> getGroupList() {
        return addressBook.getGroupList();
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list.
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return sortedFilteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        currentAdditionalPredicate = predicate;
        currentComparator = null;
        refreshFilteredPersonList();
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate, Comparator<Person> comparator) {
        requireAllNonNull(predicate, comparator);
        currentAdditionalPredicate = predicate;
        currentComparator = comparator;
        refreshFilteredPersonList();
    }

    @Override
    public void switchToAllStudentsView() {
        activeGroupName.set(null);
        clearActiveSessionDate();
        clearVisibleSessionRange();
        currentAdditionalPredicate = PREDICATE_SHOW_ALL_PERSONS;
        currentComparator = null;
        updateCurrentViewLabel();
        refreshFilteredPersonList();
    }

    @Override
    public void switchToGroupView(GroupName groupName) {
        requireNonNull(groupName);
        activeGroupName.set(groupName);
        clearActiveSessionDate();
        clearVisibleSessionRange();
        currentAdditionalPredicate = PREDICATE_SHOW_ALL_PERSONS;
        currentComparator = null;
        updateCurrentViewLabel();
        refreshFilteredPersonList();
    }

    @Override
    public Optional<GroupName> getActiveGroupName() {
        return Optional.ofNullable(activeGroupName.get());
    }

    @Override
    public Optional<LocalDate> getActiveSessionDate() {
        return Optional.ofNullable(activeSessionDate.get());
    }

    @Override
    public void setActiveSessionDate(LocalDate date) {
        requireNonNull(date);
        activeSessionDate.set(date);
    }

    @Override
    public void clearActiveSessionDate() {
        activeSessionDate.set(null);
    }

    @Override
    public ReadOnlyStringProperty currentViewProperty() {
        return currentView;
    }

    @Override
    public ReadOnlyObjectProperty<GroupName> activeGroupNameProperty() {
        return activeGroupName;
    }

    @Override
    public ReadOnlyObjectProperty<LocalDate> activeSessionDateProperty() {
        return activeSessionDate;
    }

    @Override
    public void setAttendanceViewActive(boolean isActive) {
        attendanceViewActive.set(isActive);
    }

    @Override
    public boolean isAttendanceViewActive() {
        return attendanceViewActive.get();
    }

    @Override
    public ReadOnlyBooleanProperty attendanceViewActiveProperty() {
        return attendanceViewActive;
    }

    @Override
    public Optional<LocalDate> getVisibleSessionRangeStart() {
        return Optional.ofNullable(visibleSessionRangeStart.get());
    }

    @Override
    public Optional<LocalDate> getVisibleSessionRangeEnd() {
        return Optional.ofNullable(visibleSessionRangeEnd.get());
    }

    @Override
    public void setVisibleSessionRange(LocalDate startDate, LocalDate endDate) {
        visibleSessionRangeStart.set(startDate);
        visibleSessionRangeEnd.set(endDate);
    }

    @Override
    public void clearVisibleSessionRange() {
        visibleSessionRangeStart.set(null);
        visibleSessionRangeEnd.set(null);
    }

    @Override
    public ReadOnlyObjectProperty<LocalDate> visibleSessionRangeStartProperty() {
        return visibleSessionRangeStart;
    }

    @Override
    public ReadOnlyObjectProperty<LocalDate> visibleSessionRangeEndProperty() {
        return visibleSessionRangeEnd;
    }

    private void updateCurrentViewLabel() {
        currentView.set(activeGroupName.get() == null
                ? ALL_STUDENTS_VIEW_NAME
                : activeGroupName.get().value);
    }

    private void refreshFilteredPersonList() {
        Predicate<Person> basePredicate = activeGroupName.get() == null
                ? PREDICATE_SHOW_ALL_PERSONS
                : person -> person.hasGroup(activeGroupName.get());
        filteredPersons.setPredicate(basePredicate.and(currentAdditionalPredicate));
        sortedFilteredPersons.setComparator(currentComparator);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons)
                && sortedFilteredPersons.equals(otherModelManager.sortedFilteredPersons)
                && currentView.get().equals(otherModelManager.currentView.get())
                && attendanceViewActive.get() == otherModelManager.attendanceViewActive.get()
                && Optional.ofNullable(activeGroupName.get()).equals(
                Optional.ofNullable(otherModelManager.activeGroupName.get()))
                && Optional.ofNullable(activeSessionDate.get()).equals(
                Optional.ofNullable(otherModelManager.activeSessionDate.get()));
    }
}
