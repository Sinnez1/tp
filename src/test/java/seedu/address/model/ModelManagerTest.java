package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesFieldsPredicate;
import seedu.address.testutil.AddressBookBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void constructor_withSavedViewContext_startsAtHomeView() {
        AddressBook addressBook = new AddressBookBuilder().build();
        addressBook.addGroup(new Group(new GroupName("T01")));
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setLastActiveGroupName("T01");
        userPrefs.setLastActiveSessionDate("2026-03-16");
        userPrefs.setAttendanceViewActive(true);

        ModelManager modelManager = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.getActiveGroupName().isEmpty());
        assertTrue(modelManager.getActiveSessionDate().isEmpty());
        assertFalse(modelManager.isAttendanceViewActive());
        assertEquals(Model.ALL_STUDENTS_VIEW_NAME, modelManager.currentViewProperty().get());
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setAddressBookFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setAddressBookFilePath(Paths.get("new/address/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setAddressBookFilePath(null));
    }

    @Test
    public void setAddressBookFilePath_validPath_setsAddressBookFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setAddressBookFilePath(path);
        assertEquals(path, modelManager.getAddressBookFilePath());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void switchToAllStudentsView_afterSortedFind_restoresOriginalOrder() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        modelManager = new ModelManager(addressBook, new UserPrefs());

        PersonMatchesFieldsPredicate predicate = new PersonMatchesFieldsPredicate(
                Collections.singletonList("e"),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());

        Comparator<Person> comparator = Comparator
                .comparingInt((Person person) -> predicate.getMatchedCriteriaCount(person))
                .thenComparingInt(person -> predicate.getExactMatchCount(person))
                .reversed()
                .thenComparing(person -> person.getName().toString(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(person -> person.getMatricNumber().toString(), String.CASE_INSENSITIVE_ORDER);

        List<Person> originalOrder = Arrays.asList(ALICE, BENSON);

        modelManager.updateFilteredPersonList(predicate, comparator);
        modelManager.switchToAllStudentsView();

        assertEquals(originalOrder, modelManager.getFilteredPersonList());
    }

    @Test
    public void switchToGroupView_clearsSessionSpecificContext() {
        AddressBook addressBook = new AddressBookBuilder().build();
        GroupName currentGroup = new GroupName("T01");
        GroupName targetGroup = new GroupName("T02");
        addressBook.addGroup(new Group(currentGroup));
        addressBook.addGroup(new Group(targetGroup));
        modelManager = new ModelManager(addressBook, new UserPrefs());

        modelManager.switchToGroupView(currentGroup);
        modelManager.setActiveSessionDate(LocalDate.of(2026, 3, 16));
        modelManager.setVisibleSessionRange(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31));

        modelManager.switchToGroupView(targetGroup);

        assertEquals(targetGroup, modelManager.getActiveGroupName().orElseThrow());
        assertTrue(modelManager.getActiveSessionDate().isEmpty());
        assertTrue(modelManager.getVisibleSessionRangeStart().isEmpty());
        assertTrue(modelManager.getVisibleSessionRangeEnd().isEmpty());
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        modelManager.updateFilteredPersonList(new PersonMatchesFieldsPredicate(
                Arrays.asList(ALICE.getName().fullName.split("\\s+")),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));

        // different attendance view mode -> returns false
        ModelManager attendanceViewModel = new ModelManager(addressBook, userPrefs);
        attendanceViewModel.setAttendanceViewActive(true);
        assertFalse(modelManager.equals(attendanceViewModel));

        // different active session date -> returns false
        ModelManager activeSessionModel = new ModelManager(addressBook, userPrefs);
        activeSessionModel.setActiveSessionDate(LocalDate.of(2026, 3, 16));
        assertFalse(modelManager.equals(activeSessionModel));
    }
}
