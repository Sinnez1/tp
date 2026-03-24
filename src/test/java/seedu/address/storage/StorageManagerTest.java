package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;

public class StorageManagerTest {

    @TempDir
    public Path testFolder;

    private StorageManager storageManager;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(getTempFilePath("ab"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getTempFilePath("prefs"));
        storageManager = new StorageManager(addressBookStorage, userPrefsStorage);
    }

    private Path getTempFilePath(String fileName) {
        return testFolder.resolve(fileName);
    }

    @Test
    public void prefsReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonUserPrefsStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonUserPrefsStorageTest} class.
         */
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(new GuiSettings(300, 600, 4, 6));
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void addressBookReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonAddressBookStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonAddressBookStorageTest} class.
         */
        AddressBook original = getTypicalAddressBook();
        storageManager.saveAddressBook(original);
        ReadOnlyAddressBook retrieved = storageManager.readAddressBook().get();
        assertEquals(original, new AddressBook(retrieved));
    }

    @Test
    public void getAddressBookFilePath() {
        assertNotNull(storageManager.getAddressBookFilePath());
    }

    @Test
    public void getLastLoadWarnings_jsonAddressBookStorage_returnsWarnings() {
        // storageManager in setUp() is already initialized with JsonAddressBookStorage
        assertEquals(java.util.List.of(), storageManager.getLastLoadWarnings());
    }

    @Test
    public void getLastLoadWarnings_notJsonAddressBookStorage_returnsEmptyList() {
        AddressBookStorage stubStorage = new AddressBookStorage() {
            @Override
            public Path getAddressBookFilePath() {
                return null;
            }
            @Override
            public java.util.Optional<seedu.address.model.ReadOnlyAddressBook> readAddressBook() {
                return java.util.Optional.empty();
            }
            @Override
            public java.util.Optional<seedu.address.model.ReadOnlyAddressBook> readAddressBook(Path filePath) {
                return java.util.Optional.empty();
            }
            @Override
            public void saveAddressBook(seedu.address.model.ReadOnlyAddressBook addressBook) { }
            @Override
            public void saveAddressBook(seedu.address.model.ReadOnlyAddressBook addressBook,
                                                  Path filePath) { }
        };

        StorageManager manager = new StorageManager(stubStorage,
                new JsonUserPrefsStorage(java.nio.file.Paths.get("dummy")));
        assertEquals(java.util.List.of(), manager.getLastLoadWarnings());
    }

    @Test
    public void getLastLoadWarnings_afterReadingInvalidData_returnsWarnings() throws Exception {
        Path filePath = testFolder.resolve("invalidClassSpaceAddressBook.json");
        String json = """
            {
              "persons": [],
              "groups": [
                {
                  "name": "T01",
                  "assignments": []
                },
                {
                  "name": "T#1",
                  "assignments": []
                }
              ]
            }
            """;
        java.nio.file.Files.writeString(filePath, json);

        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(filePath);
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getTempFilePath("prefs"));
        StorageManager manager = new StorageManager(addressBookStorage, userPrefsStorage);

        manager.readAddressBook();

        assertEquals(1, manager.getLastLoadWarnings().size());
        assertTrue(manager.getLastLoadWarnings().get(0).contains("Skipped invalid class space"));
    }

}
