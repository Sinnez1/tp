package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Email;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Name;
import seedu.address.testutil.TypicalPersons;


public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");
    private static final Path PERSON_WITH_MULTIPLE_INVALID_FIELDS =
            TEST_DATA_FOLDER.resolve("invalidPersonAddressBookWithMultipleInvalidFields.json");
    private static final Path IMPLICIT_CLASS_SPACE_FILE =
            TEST_DATA_FOLDER.resolve("personWithImplicitClassSpaceAddressBook.json");
    private static final Path MISSING_NAME_PERSON_FILE =
            TEST_DATA_FOLDER.resolve("missingNamePersonAddressBook.json");
    private static final Path JSON_NULL_NAME_PERSON_FILE =
            TEST_DATA_FOLDER.resolve("jsonNullNamePersonAddressBook.json");
    private static final Path DUPLICATE_CLASS_SPACE_FILE =
            TEST_DATA_FOLDER.resolve("duplicateClassSpaceAddressBook.json");
    private static final Path INVALID_CLASS_SPACE_FILE =
            TEST_DATA_FOLDER.resolve("invalidClassSpaceAddressBook.json");
    private static final Path GRADE_EXCEEDS_MAX_MARKS_FILE =
            TEST_DATA_FOLDER.resolve("gradeExceedsMaxMarksAddressBook.json");
    private static final Path GRADE_AT_MAX_MARKS_FILE =
            TEST_DATA_FOLDER.resolve("gradeAtMaxMarksAddressBook.json");
    private static final Path NEGATIVE_MAX_MARKS_FILE =
            TEST_DATA_FOLDER.resolve("negativeMaxMarksAddressBook.json");
    private static final Path GRADE_FOR_NON_MEMBER_CLASS_SPACE_FILE =
            TEST_DATA_FOLDER.resolve("gradeForNonMemberClassSpaceAddressBook.json");
    private static final Path GRADE_FOR_NON_EXISTENT_ASSIGNMENT_FILE =
            TEST_DATA_FOLDER.resolve("gradeForNonExistentAssignmentAddressBook.json");
    private static final Path SESSION_FOR_NON_MEMBER_CLASS_SPACE_FILE =
            TEST_DATA_FOLDER.resolve("sessionForNonMemberClassSpaceAddressBook.json");
    private static final Path VALID_GRADES_AND_SESSIONS_FILE =
            TEST_DATA_FOLDER.resolve("validGradesAndSessionsAddressBook.json");
    private static final Path PRESERVED_SKIPPED_PERSONS_FILE =
            TEST_DATA_FOLDER.resolve("preservedSkippedPersonsAddressBook.json");
    private static final Path PRESERVED_SKIPPED_CLASS_SPACES_FILE =
            TEST_DATA_FOLDER.resolve("preservedSkippedClassSpacesAddressBook.json");
    private static final Path INVALID_ASSIGNMENT_NAME_FILE =
            TEST_DATA_FOLDER.resolve("invalidAssignmentNameAddressBook.json");
    private static final Path MISSING_NAME_CLASS_SPACE_FILE =
            TEST_DATA_FOLDER.resolve("missingNameClassSpaceAddressBook.json");

    @Test
    public void toModelType_invalidPersonWithMultipleInvalidFields_formatsWarningAsBulletList() throws Exception {

        JsonSerializableAddressBook dataFromFile =
                JsonUtil.readJsonFile(PERSON_WITH_MULTIPLE_INVALID_FIELDS, JsonSerializableAddressBook.class).get();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        String expectedWarning = "Skipped invalid contact 'Hans Must!er':\n"
                + "- " + Name.MESSAGE_CONSTRAINTS + "\n"
                + "- " + Email.getDiagnosticMessage("hans@example.com.d") + "\n"
                + "- " + String.format(MatricNumber.MESSAGE_INVALID_CHECKSUM, 'X');

        assertEquals(0, addressBookFromFile.getPersonList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertEquals(expectedWarning, dataFromFile.getLoadWarnings().get(0));
    }

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

    @Test
    public void constructor_nullPreservedSkippedPersons_success() throws Exception {
        AddressBook addressBook = TypicalPersons.getTypicalAddressBook();

        // Create the serializable book with a null list for skipped persons
        JsonSerializableAddressBook serializable = new JsonSerializableAddressBook(addressBook, null);

        // Verify it doesn't crash and still correctly models the valid persons
        assertEquals(addressBook.getPersonList().size(), serializable.toModelType().getPersonList().size());
        assertEquals(0, serializable.getPreservedSkippedPersons().size());
    }

    @Test
    public void toModelType_personWithImplicitClassSpace_createsClassSpaceAutomatically() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(IMPLICIT_CLASS_SPACE_FILE,
                JsonSerializableAddressBook.class).get();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        // Verify the person was loaded successfully.
        assertEquals(1, addressBookFromFile.getPersonList().size());

        // Verify that the app automatically created the missing class space.
        assertEquals(1, addressBookFromFile.getClassSpaceList().size());

        // Verify that it is the expected class space.
        seedu.address.model.classspace.ClassSpace expectedClassSpace =
                new seedu.address.model.classspace.ClassSpace(
                        new seedu.address.model.classspace.ClassSpaceName("Implicit-Class-Space"));

        assertTrue(addressBookFromFile.hasClassSpace(expectedClassSpace));
    }

    @Test
    public void constructor_readOnlyAddressBook_convertsCorrectly() {
        AddressBook typicalAddressBook = TypicalPersons.getTypicalAddressBook();
        JsonSerializableAddressBook serializable = new JsonSerializableAddressBook(typicalAddressBook);

        // Should not throw exceptions, just to check equality.
        try {
            assertEquals(typicalAddressBook, serializable.toModelType());
        } catch (Exception e) {
            throw new AssertionError("Conversion should not fail.", e);
        }
    }

    @Test
    public void toModelType_missingName_generatesCorrectWarning() throws Exception {
        assertMissingNameWarningIsGenerated(MISSING_NAME_PERSON_FILE);
    }

    @Test
    public void toModelType_jsonNullName_generatesCorrectWarning() throws Exception {
        assertMissingNameWarningIsGenerated(JSON_NULL_NAME_PERSON_FILE);
    }

    @Test
    public void toModelType_duplicateClassSpaces_skipsDuplicateClassSpaceAndAddsWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_CLASS_SPACE_FILE,
                JsonSerializableAddressBook.class).get();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(1, addressBookFromFile.getClassSpaceList().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped duplicate class space"));
    }

    @Test
    public void constructor_nullLists_doesNotThrow() throws Exception {
        JsonSerializableAddressBook serializable = new JsonSerializableAddressBook(
                (List<JsonNode>) null, (List<JsonNode>) null, null, null, null);

        AddressBook addressBook = serializable.toModelType();
        assertEquals(0, addressBook.getPersonList().size());
        assertEquals(0, addressBook.getClassSpaceList().size());
    }

    @Test
    public void toModelType_invalidClassSpaceAddressBook_skipsInvalidClassSpaceAndAddsWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_CLASS_SPACE_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(1, addressBookFromFile.getClassSpaceList().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid class space"));
    }

    @Test
    public void toModelType_gradeExceedsMaxMarks_skipsPersonAndAddsWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(GRADE_EXCEEDS_MAX_MARKS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        // Person with invalid grade should be skipped
        assertEquals(0, addressBookFromFile.getPersonList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid contact"));
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("exceeds max marks"));
    }

    @Test
    public void toModelType_gradeExceedsMaxMarks_warningIncludesAssignmentAndClassSpaceDetails() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(GRADE_EXCEEDS_MAX_MARKS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        dataFromFile.toModelType();

        String warning = dataFromFile.getLoadWarnings().get(0);
        assertTrue(warning.contains("Quiz 1"), "Warning should mention the assignment name");
        assertTrue(warning.contains("T01"), "Warning should mention the class space name");
        assertTrue(warning.contains("105"), "Warning should mention the offending grade");
        assertTrue(warning.contains("100"), "Warning should mention the max marks");
    }

    @Test
    public void toModelType_gradeAtMaxMarks_loadsPersonSuccessfully() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(GRADE_AT_MAX_MARKS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        // Grade equal to maxMarks is valid — person should be loaded
        assertEquals(1, addressBookFromFile.getPersonList().size());
        assertEquals(0, dataFromFile.getLoadWarnings().size());
    }

    @Test
    public void toModelType_negativeMaxMarksAssignment_skipsClassSpaceAndAddsWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(NEGATIVE_MAX_MARKS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(0, addressBookFromFile.getClassSpaceList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedClassSpaces().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid class space"));
    }

    @Test
    public void toModelType_gradeForNonMemberClassSpace_skipsPersonAndAddsWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(GRADE_FOR_NON_MEMBER_CLASS_SPACE_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(0, addressBookFromFile.getPersonList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid contact"));
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("not a member of it"));
    }

    @Test
    public void toModelType_gradeForNonMemberClassSpace_warningMentionsClassSpace() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(GRADE_FOR_NON_MEMBER_CLASS_SPACE_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        dataFromFile.toModelType();

        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("T01"));
    }

    @Test
    public void toModelType_gradeForNonExistentAssignment_skipsPersonAndAddsWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(GRADE_FOR_NON_EXISTENT_ASSIGNMENT_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(0, addressBookFromFile.getPersonList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid contact"));
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("does not exist"));
    }

    @Test
    public void toModelType_gradeForNonExistentAssignment_warningMentionsAssignmentAndClassSpace() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(GRADE_FOR_NON_EXISTENT_ASSIGNMENT_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        dataFromFile.toModelType();

        String warning = dataFromFile.getLoadWarnings().get(0);
        assertTrue(warning.contains("NonExistentAssignment"));
        assertTrue(warning.contains("T01"));
    }

    @Test
    public void toModelType_sessionForNonMemberClassSpace_skipsPersonAndAddsWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(SESSION_FOR_NON_MEMBER_CLASS_SPACE_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(0, addressBookFromFile.getPersonList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid contact"));
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("not a member of it"));
    }

    @Test
    public void toModelType_sessionForNonMemberClassSpace_warningMentionsClassSpace() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(SESSION_FOR_NON_MEMBER_CLASS_SPACE_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        dataFromFile.toModelType();

        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("T01"));
    }

    @Test
    public void toModelType_validGradesAndSessions_loadsPersonSuccessfully() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(VALID_GRADES_AND_SESSIONS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(1, addressBookFromFile.getPersonList().size());
        assertEquals(0, dataFromFile.getLoadWarnings().size());
    }

    @Test
    public void toModelType_preservedSkippedPersonsInFile_survivesReload() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(PRESERVED_SKIPPED_PERSONS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        dataFromFile.toModelType();

        // The skipped person from the file should still be preserved after toModelType()
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals("Alex Yeoh",
                dataFromFile.getPreservedSkippedPersons().get(0).get("name").asText());
    }

    @Test
    public void toModelType_preservedSkippedPersonsInFile_doesNotLoadSkippedPersonIntoAddressBook()
            throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(PRESERVED_SKIPPED_PERSONS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBook = dataFromFile.toModelType();

        // The preserved skipped person should NOT be loaded as a valid contact.
        assertEquals(1, addressBook.getPersonList().size()); // only David Li.
        assertEquals("David Li", addressBook.getPersonList().get(0).getName().fullName);
    }

    @Test
    public void toModelType_loadWarningsInFile_survivesReload() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(PRESERVED_SKIPPED_PERSONS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        dataFromFile.toModelType();

        // The load warning from the file should still be present after toModelType().
        assertTrue(dataFromFile.getLoadWarnings().stream()
                .anyMatch(w -> w.contains("FakeAssignment")));
    }

    @Test
    public void toModelType_preservedSkippedClassSpacesInFile_survivesReload() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(PRESERVED_SKIPPED_CLASS_SPACES_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        dataFromFile.toModelType();

        // The skipped class space from the file should still be preserved after toModelType().
        assertEquals(1, dataFromFile.getPreservedSkippedClassSpaces().size());
        assertEquals("T01!!!",
                dataFromFile.getPreservedSkippedClassSpaces().get(0).get("name").asText());
    }

    @Test
    public void toModelType_preservedSkippedClassSpacesInFile_doesNotLoadSkippedClassSpaceIntoAddressBook()
            throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(PRESERVED_SKIPPED_CLASS_SPACES_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBook = dataFromFile.toModelType();

        // Only T01 (the valid one) should be loaded, not T01!!!
        assertEquals(1, addressBook.getClassSpaceList().size());
        assertEquals("T01", addressBook.getClassSpaceList().get(0).getClassSpaceName().value);
    }

    @Test
    public void toModelType_newSkipsAndPreservedSkips_bothPresentInResult() throws Exception {
        // File has 1 pre-existing preserved skip (Alex) + 1 valid person (David).
        // toModelType() should keep Alex in preservedSkippedPersons without re-processing him.
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(PRESERVED_SKIPPED_PERSONS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBook = dataFromFile.toModelType();

        assertEquals(1, addressBook.getPersonList().size()); // David loaded
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size()); // Alex preserved
        assertTrue(dataFromFile.getLoadWarnings().stream()
                .anyMatch(w -> w.contains("FakeAssignment"))); // warning preserved
    }

    @Test
    public void toModelType_assignmentWithSpecialCharacterName_skipsClassSpaceAndAddsWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_ASSIGNMENT_NAME_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(0, addressBookFromFile.getClassSpaceList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedClassSpaces().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid class space"));
    }

    /**
     * Helper method to read JSON file and assert that it produces "missing name" warning.
     *
     * @param filePath Path of JSON file.
     * @throws Exception
     */
    private void assertMissingNameWarningIsGenerated(Path filePath) throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(filePath,
                JsonSerializableAddressBook.class).get();
        dataFromFile.toModelType(); // This populates the warnings

        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("entry #1 (missing name)"));
        assertEquals(0, dataFromFile.toModelType().getPersonList().size());
    }

    @Test
    public void toModelType_classSpaceWithMissingName_generatesEntryNumberWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(MISSING_NAME_CLASS_SPACE_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        dataFromFile.toModelType();

        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("entry #1 (missing name)"));
    }
}

