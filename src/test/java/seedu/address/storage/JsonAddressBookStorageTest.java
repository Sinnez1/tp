package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.IDA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.FileUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.classspace.Group;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.MatricNumber;
import seedu.address.testutil.PersonBuilder;

public class JsonAddressBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAddressBookStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readAddressBook(null));
    }

    private java.util.Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws Exception {
        return new JsonAddressBookStorage(Paths.get(filePath)).readAddressBook(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("notJsonFormatAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidPersonAddressBook_returnsEmptyAddressBook() throws Exception {
        // The file has 1 invalid person, so it skips them and loads an empty address book.
        AddressBook addressBook = new AddressBook(readAddressBook("invalidPersonAddressBook.json").get());
        assertEquals(0, addressBook.getPersonList().size());
    }

    @Test
    public void readAddressBook_invalidAndValidPersonAddressBook_returnsOnlyValidPerson() throws Exception {
        // The file has 1 valid person and 1 invalid person.
        // It skips the invalid one and successfully loads the 1 valid person.
        AddressBook addressBook = new AddressBook(readAddressBook("invalidAndValidPersonAddressBook.json").get());
        assertEquals(1, addressBook.getPersonList().size());
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBook.json");
        AddressBook original = getTypicalAddressBook();
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        // Save in new file and read back
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Modify data, overwrite exiting file, and read back
        original.addPerson(HOON);
        original.removePerson(ALICE);
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Save and read without specifying file path
        original.addPerson(IDA);
        jsonAddressBookStorage.saveAddressBook(original); // file path not specified
        readBack = jsonAddressBookStorage.readAddressBook().get(); // file path not specified
        assertEquals(original, new AddressBook(readBack));
    }

    @Test
    public void readAndSaveAddressBook_personSessionFieldsPreserved_success() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBookWithSessionFields.json");
        AddressBook original = new AddressBook();
        original.addClassSpace(new Group(new ClassSpaceName("CS2103T-T01")));
        original.addPerson(new PersonBuilder()
                .withName("Session Student")
                .withMatricNumber("A1234567X")
                .withPhone("91234567")
                .withEmail("session@example.com")
                .withClassSpaces("CS2103T-T01")
                .withSession("CS2103T-T01", "2026-03-16", "PRESENT", 5)
                .build());

        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);
        jsonAddressBookStorage.saveAddressBook(original, filePath);

        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));
        assertEquals("PRESENT", readBack.getPersonList().stream()
                .filter(person -> person.getMatricNumber().equals(new MatricNumber("A1234567X")))
                .findFirst()
                .orElseThrow()
                .getAttendance(new ClassSpaceName("CS2103T-T01"), java.time.LocalDate.of(2026, 3, 16))
                .toString());
    }

    @Test
    public void saveAddressBook_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(null, "SomeFile.json"));
    }

    /**
     * Saves {@code addressBook} at the specified {@code filePath}.
     */
    private void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath) {
        try {
            new JsonAddressBookStorage(Paths.get(filePath))
                    .saveAddressBook(addressBook, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(new AddressBook(), null));
    }

    @Test
    public void readAddressBook_invalidAndValidPersonAddressBook_populatesLoadWarnings() throws Exception {
        Path filePath = addToTestDataPathIfNotNull("invalidAndValidPersonAddressBook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        // Read the file which contains 1 valid and 1 invalid person.
        storage.readAddressBook(filePath);

        // Verify that the storage object successfully captured the warning.
        List<String> warnings = storage.getLastLoadWarnings();
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains("Skipped invalid contact"));
    }

    @Test
    public void readAndSaveAddressBook_invalidPersonPreserved_success() throws Exception {
        Path originalFilePath = addToTestDataPathIfNotNull("invalidAndValidPersonAddressBook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(originalFilePath);

        // Read the address book (1 valid is loaded, 1 invalid is skipped and preserved).
        ReadOnlyAddressBook addressBook = storage.readAddressBook(originalFilePath).get();

        // Save it back to a new temporary file.
        Path saveFilePath = testFolder.resolve("TempAddressBookWithInvalid.json");
        storage.saveAddressBook(addressBook, saveFilePath);

        // Read the saved file natively as a String to verify the invalid person was written back.
        String savedJson = FileUtil.readFromFile(saveFilePath);

        // Ensure the valid person is there.
        assertTrue(savedJson.contains("Valid Person"));
        // Ensure the invalid person was preserved.
        assertTrue(savedJson.contains("Person With Invalid Phone Field"));
    }

    @Test
    public void readAddressBook_sequentialReads_resetsWarnings() throws Exception {
        Path invalidFilePath = addToTestDataPathIfNotNull("invalidPersonAddressBook.json");
        Path validFilePath = testFolder.resolve("TempAddressBook.json");

        // Create a valid file to read from.
        JsonAddressBookStorage storage = new JsonAddressBookStorage(validFilePath);
        storage.saveAddressBook(getTypicalAddressBook(), validFilePath);

        // Read the invalid file -> Should have warnings.
        storage.readAddressBook(invalidFilePath);
        assertEquals(1, storage.getLastLoadWarnings().size());

        // Read the valid file -> Warnings should be cleared/reset.
        storage.readAddressBook(validFilePath);
        assertEquals(0, storage.getLastLoadWarnings().size());
    }

    @Test
    public void readAddressBook_invalidClassSpaceAddressBook_populatesLoadWarnings() throws Exception {
        Path filePath = addToTestDataPathIfNotNull("invalidClassSpaceAddressBook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        ReadOnlyAddressBook addressBook = storage.readAddressBook(filePath).orElseThrow();

        assertEquals(1, addressBook.getClassSpaceList().size());
        assertEquals(1, storage.getLastLoadWarnings().size());
        assertTrue(storage.getLastLoadWarnings().get(0).contains("Skipped invalid class space"));
    }

    @Test
    public void saveAddressBook_afterFailedLoad_doesNotOverwriteOriginalFile() throws Exception {
        Path sourceFilePath = addToTestDataPathIfNotNull("brokenAddressBook.json");
        Path tempFilePath = testFolder.resolve("brokenAddressBook.json");

        String originalContents = FileUtil.readFromFile(sourceFilePath);
        FileUtil.writeToFile(tempFilePath, originalContents);

        JsonAddressBookStorage storage = new JsonAddressBookStorage(tempFilePath);

        assertThrows(DataLoadingException.class, () -> storage.readAddressBook(tempFilePath));

        storage.saveAddressBook(getTypicalAddressBook(), tempFilePath);

        String afterSaveContents = FileUtil.readFromFile(tempFilePath);
        assertEquals(originalContents, afterSaveContents);
    }

    @Test
    public void readAddressBook_illegalValueExceptionFile_setsSkipSaveFlag() throws Exception {
        // brokenAddressBook.json is malformed JSON — triggers DataLoadingException not IllegalValueException.
        // This test documents that after a fatal load failure, a subsequent save is skipped.
        Path filePath = testFolder.resolve("TempAddressBook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        // Write a valid file first so there is something to overwrite.
        storage.saveAddressBook(getTypicalAddressBook());

        // Overwrite with a broken file.
        FileUtil.writeToFile(filePath, "not valid json");

        // Load should fail.
        assertThrows(DataLoadingException.class, () -> storage.readAddressBook(filePath));

        // Save should be skipped --> the file should still contain the broken content.
        storage.saveAddressBook(getTypicalAddressBook(), filePath);
        String fileContent = java.nio.file.Files.readString(filePath);
        assertEquals("not valid json", fileContent);
    }

    @Test
    public void readAddressBook_brokenFile_lastLoadWarningsIsEmpty() throws Exception {
        Path filePath = addToTestDataPathIfNotNull("notJsonFormatAddressBook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        assertThrows(DataLoadingException.class, () -> storage.readAddressBook(filePath));

        // Fatal error message is assembled in MainApp, not storage
        // so storage warnings should be empty after a fatal load failure
        assertEquals(0, storage.getLastLoadWarnings().size());
    }
}
