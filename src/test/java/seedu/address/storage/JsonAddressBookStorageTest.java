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
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
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
        // EP: file has 1 invalid person, so it skips them and loads an empty address book.
        AddressBook addressBook = new AddressBook(readAddressBook("invalidPersonAddressBook.json").get());
        assertEquals(0, addressBook.getPersonList().size());
    }

    @Test
    public void readAddressBook_invalidAndValidPersonAddressBook_returnsOnlyValidPerson() throws Exception {
        // EP: The file has 1 valid person and 1 invalid person.
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
        original.addGroup(new Group(new GroupName("CS2103T-T01")));
        original.addPerson(new PersonBuilder()
                .withName("Session Student")
                .withMatricNumber("A1234567X")
                .withPhone("91234567")
                .withEmail("session@example.com")
                .withGroups("CS2103T-T01")
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
                .getAttendance(new GroupName("CS2103T-T01"), java.time.LocalDate.of(2026, 3, 16))
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
        // EP: invalid person has warnings generated in loadWarnings
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
        // EP: invalid person is saved into preservedSkippedPersons
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
    public void readAddressBook_subsequentReads_resetsWarnings() throws Exception {
        // EP: warnings reset between subsequent reads
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
    public void readAddressBook_invalidGroupAddressBook_populatesLoadWarnings() throws Exception {
        // EP: file has 1 invalid and 1 valid group, valid group is loaded and invalid group generates warning
        Path filePath = addToTestDataPathIfNotNull("invalidGroupAddressBook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        ReadOnlyAddressBook addressBook = storage.readAddressBook(filePath).orElseThrow();

        assertEquals(1, addressBook.getGroupList().size());
        assertEquals(1, storage.getLastLoadWarnings().size());
        assertTrue(storage.getLastLoadWarnings().get(0).contains("Skipped invalid group"));
    }

    @Test
    public void saveAddressBook_afterFailedLoad_doesNotOverwriteOriginalFile() throws Exception {
        // EP: fatal load does not overwrite save file
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
        // EP : fatal load through malformed JSON

        // brokenAddressBook.json is malformed JSON — triggers DataLoadingException not IllegalValueException.
        // After a fatal load failure, a subsequent save is skipped.
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
    public void readAddressBook_brokenFile_lastLoadWarningsIsEmpty() {
        // EP: loadWarnings is empty if fatal load
        Path filePath = addToTestDataPathIfNotNull("notJsonFormatAddressBook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        assertThrows(DataLoadingException.class, () -> storage.readAddressBook(filePath));

        // Fatal error message is assembled in MainApp, not storage
        // so storage warnings should be empty after a fatal load failure
        assertEquals(0, storage.getLastLoadWarnings().size());
    }

    @Test
    public void readAddressBook_emptyFile_returnsEmptyAddressBook() throws Exception {
        // BVA: zero-byte file → Optional.empty() (triggers sample data)
        Path filePath = testFolder.resolve("emptyAddressBook.json");
        java.nio.file.Files.createFile(filePath);

        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
        java.util.Optional<ReadOnlyAddressBook> result = storage.readAddressBook(filePath);

        assertFalse(result.isPresent(), "Empty file should return Optional.empty() to trigger sample data");
    }

    @Test
    public void readAddressBook_blankFile_returnsEmptyAddressBook() throws Exception {
        // EP: whitespace-only content → Optional.empty() (triggers sample data)
        Path filePath = testFolder.resolve("blankAddressBook.json");
        java.nio.file.Files.writeString(filePath, "   \n  \t  ");

        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
        java.util.Optional<ReadOnlyAddressBook> result = storage.readAddressBook(filePath);

        assertFalse(result.isPresent(), "Blank file should return Optional.empty() to trigger sample data");
    }

    @Test
    public void readAddressBook_clearedAddressBook_returnsEmptyNotSampleData() throws Exception {
        // After clear, the saved JSON has empty arrays — must load as empty, not sample data.
        Path filePath = testFolder.resolve("clearedAddressBook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
        storage.saveAddressBook(new AddressBook(), filePath);

        java.util.Optional<ReadOnlyAddressBook> result = storage.readAddressBook(filePath);

        assertTrue(result.isPresent(), "Cleared address book should return empty book, not Optional.empty()");
        assertEquals(0, new AddressBook(result.get()).getPersonList().size());
        assertEquals(0, new AddressBook(result.get()).getGroupList().size());
    }
}
