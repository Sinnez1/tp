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
    private static final Path MISSING_GROUP_FILE =
            TEST_DATA_FOLDER.resolve("personReferencingMissingGroupAddressBook.json");
    private static final Path MISSING_NAME_PERSON_FILE =
            TEST_DATA_FOLDER.resolve("missingNamePersonAddressBook.json");
    private static final Path JSON_NULL_NAME_PERSON_FILE =
            TEST_DATA_FOLDER.resolve("jsonNullNamePersonAddressBook.json");
    private static final Path DUPLICATE_GROUP_FILE =
            TEST_DATA_FOLDER.resolve("duplicateGroupAddressBook.json");
    private static final Path INVALID_GROUP_FILE =
            TEST_DATA_FOLDER.resolve("invalidGroupAddressBook.json");
    private static final Path GRADE_EXCEEDS_MAX_MARKS_FILE =
            TEST_DATA_FOLDER.resolve("gradeExceedsMaxMarksAddressBook.json");
    private static final Path GRADE_AT_MAX_MARKS_FILE =
            TEST_DATA_FOLDER.resolve("gradeAtMaxMarksAddressBook.json");
    private static final Path NEGATIVE_MAX_MARKS_FILE =
            TEST_DATA_FOLDER.resolve("negativeMaxMarksAddressBook.json");
    private static final Path GRADE_FOR_NON_MEMBER_GROUP_FILE =
            TEST_DATA_FOLDER.resolve("gradeForNonMemberGroupAddressBook.json");
    private static final Path GRADE_FOR_NON_EXISTENT_ASSIGNMENT_FILE =
            TEST_DATA_FOLDER.resolve("gradeForNonExistentAssignmentAddressBook.json");
    private static final Path SESSION_FOR_NON_MEMBER_GROUP_FILE =
            TEST_DATA_FOLDER.resolve("sessionForNonMemberGroupAddressBook.json");
    private static final Path VALID_GRADES_AND_SESSIONS_FILE =
            TEST_DATA_FOLDER.resolve("validGradesAndSessionsAddressBook.json");
    private static final Path PRESERVED_SKIPPED_PERSONS_FILE =
            TEST_DATA_FOLDER.resolve("preservedSkippedPersonsAddressBook.json");
    private static final Path PRESERVED_SKIPPED_GROUPS_FILE =
            TEST_DATA_FOLDER.resolve("preservedSkippedGroupsAddressBook.json");
    private static final Path INVALID_ASSIGNMENT_NAME_FILE =
            TEST_DATA_FOLDER.resolve("invalidAssignmentNameAddressBook.json");
    private static final Path MISSING_NAME_GROUP_FILE =
            TEST_DATA_FOLDER.resolve("missingNameGroupAddressBook.json");
    private static final Path PRESERVED_GROUP_IS_VALID =
            TEST_DATA_FOLDER.resolve("preservedGroupIsValidAddressBook.json");
    private static final Path PRESERVED_PERSON_IS_VALID =
            TEST_DATA_FOLDER.resolve("preservedPersonIsValidAddressBook.json");
    private static final Path PRESERVED_GROUP_AND_PERSON_IS_VALID =
            TEST_DATA_FOLDER.resolve("preservedGroupAndPersonIsValidAddressBook.json");

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
    public void toModelType_personWithImplicitGroup_skipsPersonAndAddsWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(MISSING_GROUP_FILE,
                JsonSerializableAddressBook.class).get();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        // Person references a group not in the groups list — should be skipped, not loaded.
        assertEquals(0, addressBookFromFile.getPersonList().size());

        // The missing group should not be auto-created.
        assertEquals(0, addressBookFromFile.getGroupList().size());

        // The skipped person should be preserved on reload.
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());

        // A warning should be generated describing the missing group reference.
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid contact"));
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Implicit-Group"));
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
    public void toModelType_duplicateGroups_skipsDuplicateGroupAndAddsWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_GROUP_FILE,
                JsonSerializableAddressBook.class).get();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(1, addressBookFromFile.getGroupList().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped duplicate group"));
    }

    @Test
    public void constructor_nullLists_doesNotThrow() throws Exception {
        JsonSerializableAddressBook serializable = new JsonSerializableAddressBook(
                (List<JsonNode>) null, (List<JsonNode>) null, null, null, null);

        AddressBook addressBook = serializable.toModelType();
        assertEquals(0, addressBook.getPersonList().size());
        assertEquals(0, addressBook.getGroupList().size());
    }

    @Test
    public void toModelType_invalidGroupAddressBook_skipsInvalidGroupAndAddsWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_GROUP_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(1, addressBookFromFile.getGroupList().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid group"));
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
    public void toModelType_gradeExceedsMaxMarks_warningIncludesAssignmentAndGroupDetails() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(GRADE_EXCEEDS_MAX_MARKS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        dataFromFile.toModelType();

        String warning = dataFromFile.getLoadWarnings().get(0);
        assertTrue(warning.contains("Quiz 1"), "Warning should mention the assignment name");
        assertTrue(warning.contains("T01"), "Warning should mention the group name");
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
    public void toModelType_negativeMaxMarksAssignment_skipsGroupAndAddsWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(NEGATIVE_MAX_MARKS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(0, addressBookFromFile.getGroupList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedGroups().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid group"));
    }

    @Test
    public void toModelType_gradeForNonMemberGroup_skipsPersonAndAddsWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(GRADE_FOR_NON_MEMBER_GROUP_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(0, addressBookFromFile.getPersonList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid contact"));
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("not a member of it"));
    }

    @Test
    public void toModelType_gradeForNonMemberGroup_warningMentionsGroup() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(GRADE_FOR_NON_MEMBER_GROUP_FILE,
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
    public void toModelType_gradeForNonExistentAssignment_warningMentionsAssignmentAndGroup() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(GRADE_FOR_NON_EXISTENT_ASSIGNMENT_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        dataFromFile.toModelType();

        String warning = dataFromFile.getLoadWarnings().get(0);
        assertTrue(warning.contains("NonExistentAssignment"));
        assertTrue(warning.contains("T01"));
    }

    @Test
    public void toModelType_sessionForNonMemberGroup_skipsPersonAndAddsWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(SESSION_FOR_NON_MEMBER_GROUP_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(0, addressBookFromFile.getPersonList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid contact"));
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("not a member of it"));
    }

    @Test
    public void toModelType_sessionForNonMemberGroup_warningMentionsGroup() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(SESSION_FOR_NON_MEMBER_GROUP_FILE,
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
    public void toModelType_preservedPersonStillInvalid_warningIsRegeneratedNotRestored() throws Exception {
        // EP: warning exists in JSON file → must be regenerated from current state, not read from file
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(PRESERVED_SKIPPED_PERSONS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        // Before toModelType(), warnings must be empty even though the JSON file has them.
        assertEquals(0, dataFromFile.getLoadWarnings().size());

        dataFromFile.toModelType();

        // Warning still appears because Alex still fails — but it was regenerated, not restored.
        assertTrue(dataFromFile.getLoadWarnings().stream().anyMatch(w -> w.contains("FakeAssignment")));
    }

    @Test
    public void toModelType_preservedSkippedGroupsInFile_survivesReload() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(PRESERVED_SKIPPED_GROUPS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        dataFromFile.toModelType();

        // The skipped group from the file should still be preserved after toModelType().
        assertEquals(1, dataFromFile.getPreservedSkippedGroups().size());
        assertEquals("T01!!!",
                dataFromFile.getPreservedSkippedGroups().get(0).get("name").asText());
    }

    @Test
    public void toModelType_preservedSkippedGroupsInFile_doesNotLoadSkippedGroupIntoAddressBook()
            throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(PRESERVED_SKIPPED_GROUPS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBook = dataFromFile.toModelType();

        // Only T01 (the valid one) should be loaded, not T01!!!
        assertEquals(1, addressBook.getGroupList().size());
        assertEquals("T01", addressBook.getGroupList().get(0).getGroupName().value);
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
    public void toModelType_assignmentWithSpecialCharacterName_skipsGroupAndAddsWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_ASSIGNMENT_NAME_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(0, addressBookFromFile.getGroupList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedGroups().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid group"));
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
    public void toModelType_groupWithMissingName_generatesEntryNumberWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(MISSING_NAME_GROUP_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        dataFromFile.toModelType();

        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("entry #1 (missing name)"));
    }

    @Test
    public void toModelType_preservedPersonNowValid_isMovedToValidPersonAndNoWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(PRESERVED_PERSON_IS_VALID,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBook = dataFromFile.toModelType();

        assertEquals(1, addressBook.getPersonList().size(), "Alice should be moved to valid person");
        assertEquals("Alice Pauline", addressBook.getPersonList().get(0).getName().fullName);
        assertEquals(0, dataFromFile.getPreservedSkippedPersons().size(), "Preserved list should be empty");
        assertEquals(0, dataFromFile.getLoadWarnings().size(), "No warning for a valid entry");
    }

    @Test
    public void toModelType_preservedGroupNowValid_isMovedToValidGroupAndNoWarning() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(PRESERVED_GROUP_IS_VALID,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBook = dataFromFile.toModelType();

        assertEquals(1, addressBook.getGroupList().size(), "T01-Fixed should be promoted");
        assertEquals("T01-Fixed", addressBook.getGroupList().get(0).getGroupName().value);
        assertEquals(0, dataFromFile.getPreservedSkippedGroups().size());
        assertEquals(0, dataFromFile.getLoadWarnings().size());
    }

    @Test
    public void toModelType_preservedGroupAndPersonBothNowValid_bothMovedToValidSuccessfully() throws Exception {
        // T01 was previously an invalid group — now fixed and in preservedSkippedGroups.
        // Alice was previously skipped because T01 didn't exist — now in preservedSkippedPersons.
        // On this load: T01 is revalidated and moved to valid group first, then Alice passes because T01 now exists.
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(
                PRESERVED_GROUP_AND_PERSON_IS_VALID,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBook = dataFromFile.toModelType();

        // Both should be moved to valid group.
        assertEquals(1, addressBook.getGroupList().size(), "T01 should be moved from preserved groups");
        assertEquals("T01", addressBook.getGroupList().get(0).getGroupName().value);
        assertEquals(1, addressBook.getPersonList().size(), "Alice should be moved from preserved persons");
        assertEquals("Alice Pauline", addressBook.getPersonList().get(0).getName().fullName);

        // Nothing left in preserved lists and no warnings.
        assertEquals(0, dataFromFile.getPreservedSkippedGroups().size());
        assertEquals(0, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(0, dataFromFile.getLoadWarnings().size());
    }
}
