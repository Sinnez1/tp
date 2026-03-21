package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_skipsInvalidPerson() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();

        // Since the file only contains 1 invalid person, it should skip it and return 0 persons.
        assertEquals(0, addressBookFromFile.getPersonList().size());
    }

    @Test
    public void toModelType_duplicatePersons_skipsDuplicatePerson() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();

        // Since the file contains 2 duplicates, it skips the 2nd one and loads exactly 1 person.
        assertEquals(1, addressBookFromFile.getPersonList().size());
    }

    @Test
    public void toModelType_invalidPersonFile_preservesSkippedRawPersonAndWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(0, addressBookFromFile.getPersonList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid contact"));
    }

    @Test
    public void toModelType_duplicatePersons_preservesSkippedDuplicateAndWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(1, addressBookFromFile.getPersonList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped duplicate contact"));
    }

}
