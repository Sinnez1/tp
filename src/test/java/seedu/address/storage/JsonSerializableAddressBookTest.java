package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

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
    private static final Path GROUP_ONLY_HAS_NAME_STRING =
            TEST_DATA_FOLDER.resolve("groupOnlyHasStringAddressBook.json");
    private static final Path PRESERVED_GROUP_ONLY_HAS_NAME_STRING =
            TEST_DATA_FOLDER.resolve("preservedSkippedGroupsOnlyHasStringAddressBook.json");
    private static final Path PERSON_ONLY_HAS_NAME_STRING =
            TEST_DATA_FOLDER.resolve("personOnlyHasNameAddressBook.json");
    private static final Path PRESERVED_PERSON_ONLY_HAS_NAME_STRING =
            TEST_DATA_FOLDER.resolve("preservedSkippedPersonsOnlyHasStringNameAddressBook.json");
    private static final Path VALID_PERSON_WITH_PRESERVED_PERSON =
            TEST_DATA_FOLDER.resolve("validPersonWithPreservedSkippedPersonsAddressBook.json");
    private static final Path TWO_INVALID_GROUPS_WITH_NO_NAME =
            TEST_DATA_FOLDER.resolve("twoInvalidGroupsWithNoNameAddressBook.json");

    private static final String CLEAN_ERROR_MESSAGE =
            "Entry has invalid structure or an unrecognised field type. "
                    + "Ensure it is a valid JSON object with the correct fields.";

    @Test
    public void toModelType_invalidPersonWithMultipleInvalidFields_formatsWarningAsBulletList() throws Exception {
        // EP: invalid person with multiple bad fields, formats as bullet list

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
        // EP: valid person, loads correctly
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_skipsInvalidPerson() throws Exception {
        // EP: invalid person not loaded
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();

        // Since the file only contains 1 invalid person, it should skip it and return 0 persons.
        assertEquals(0, addressBookFromFile.getPersonList().size());
    }

    @Test
    public void toModelType_invalidPersonFile_preservesSkippedPersonsAndLoadsWarning() throws Exception {
        // EP: invalid person, moved to preservedSkippedPersons and warning is generated
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
        // EP: duplicate entry skipped and moved to preservedSkippedPersons with a warning generated
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
        // EP: null preservedSkippedPersons passed to constructor, treated as empty.
        AddressBook addressBook = TypicalPersons.getTypicalAddressBook();

        // Create the serializable book with a null list for skipped persons
        JsonSerializableAddressBook serializable = new JsonSerializableAddressBook(addressBook, null);

        // Does not crash and still correctly models the valid persons
        assertEquals(addressBook.getPersonList().size(), serializable.toModelType().getPersonList().size());
        assertEquals(0, serializable.getPreservedSkippedPersons().size());
    }

    @Test
    public void toModelType_personWithImplicitGroup_skipsPersonAndAddsWarning() throws Exception {
        // EP: person references a group that does not exist yet
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
        // EP: can serialize properly without loss
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
        // EP: missing field "name":
        assertMissingNameWarningIsGenerated(MISSING_NAME_PERSON_FILE);
    }

    @Test
    public void toModelType_jsonNullName_generatesCorrectWarning() throws Exception {
        // EP: person has "name": null,
        assertMissingNameWarningIsGenerated(JSON_NULL_NAME_PERSON_FILE);
    }

    @Test
    public void toModelType_duplicateGroups_skipsDuplicateGroupAndAddsWarning() throws Exception {
        // EP: duplicate groups, only 1 is loaded and the other goes to preservedSkippedGroups
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_GROUP_FILE,
                JsonSerializableAddressBook.class).get();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(1, addressBookFromFile.getGroupList().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped duplicate group"));
        assertEquals(1, dataFromFile.getPreservedSkippedGroups().size());
    }

    @Test
    public void constructor_nullLists_doesNotThrow() throws Exception {
        // EP: all lists are null, empty address book produced
        JsonSerializableAddressBook serializable = new JsonSerializableAddressBook(
                null, null, null, null, null);

        AddressBook addressBook = serializable.toModelType();
        assertEquals(0, addressBook.getPersonList().size());
        assertEquals(0, addressBook.getGroupList().size());
    }

    @Test
    public void toModelType_invalidGroupAddressBook_skipsInvalidGroupAndAddsWarning() throws Exception {
        // EP: invalid group name is skipped
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_GROUP_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(1, addressBookFromFile.getGroupList().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid group"));
    }

    @Test
    public void toModelType_gradeExceedsMaxMarks_skipsPersonAndAddsWarning() throws Exception {
        // EP: person has grades for an assignment that exceeds max marks for the assignment
        // BVA: exceeds max marks by 1
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(GRADE_EXCEEDS_MAX_MARKS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        String warning = dataFromFile.getLoadWarnings().get(0);

        // Person with invalid grade should be skipped
        assertEquals(0, addressBookFromFile.getPersonList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(warning.contains("Skipped invalid contact"));
        assertTrue(warning.contains("exceeds max marks"));

        // warning for person marks exceeding max marks should detail assignment name, group name, and max marks
        assertTrue(warning.contains("Quiz 1"), "Warning should mention the assignment name");
        assertTrue(warning.contains("T01"), "Warning should mention the group name");
        assertTrue(warning.contains("101"), "Warning should mention the offending grade");
        assertTrue(warning.contains("100"), "Warning should mention the max marks");
    }

    @Test
    public void toModelType_gradeAtMaxMarks_loadsPersonSuccessfully() throws Exception {
        // EP: marks below max marks for assignments
        // BVA: marks equals max marks
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(GRADE_AT_MAX_MARKS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        // Grade equal to maxMarks is valid — person should be loaded
        assertEquals(1, addressBookFromFile.getPersonList().size());
        assertEquals(0, dataFromFile.getLoadWarnings().size());
    }

    @Test
    public void toModelType_negativeMaxMarksAssignment_skipsGroupAndAddsWarning() throws Exception {
        // EP: group has assignment with negative max marks -> group is skipped
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
        // EP: person has grade for a group they are not part of
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(GRADE_FOR_NON_MEMBER_GROUP_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        String warning = dataFromFile.getLoadWarnings().get(0);

        assertEquals(0, addressBookFromFile.getPersonList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(warning.contains("Skipped invalid contact"));
        assertTrue(warning.contains("not a member of it"));

        // warning message should also name the group
        assertTrue(warning.contains("T01"));
    }

    @Test
    public void toModelType_gradeForNonExistentAssignment_skipsPersonAndAddsWarning() throws Exception {
        // EP: person has grades for an assignment that does not exist in the group
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(GRADE_FOR_NON_EXISTENT_ASSIGNMENT_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        String warning = dataFromFile.getLoadWarnings().get(0);

        assertEquals(0, addressBookFromFile.getPersonList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(warning.contains("Skipped invalid contact"));
        assertTrue(warning.contains("does not exist"));

        // warning should also generate warning message that names assignment name and group
        assertTrue(warning.contains("NonExistentAssignment"));
        assertTrue(warning.contains("T01"));
    }


    @Test
    public void toModelType_sessionForNonMemberGroup_skipsPersonAndAddsWarning() throws Exception {
        // EP: person has session for a group they are not part of
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(SESSION_FOR_NON_MEMBER_GROUP_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        String warning = dataFromFile.getLoadWarnings().get(0);

        assertEquals(0, addressBookFromFile.getPersonList().size());
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(warning.contains("Skipped invalid contact"));
        assertTrue(warning.contains("not a member of it"));

        // warning should name the group
        assertTrue(warning.contains("T01"));
    }

    @Test
    public void toModelType_validGradesAndSessions_loadsPersonSuccessfully() throws Exception {
        // EP: person has valid grades and session and will load
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(VALID_GRADES_AND_SESSIONS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBookFromFile = dataFromFile.toModelType();

        assertEquals(1, addressBookFromFile.getPersonList().size());
        assertEquals(0, dataFromFile.getLoadWarnings().size());
    }

    @Test
    public void toModelType_preservedSkippedPersonsInFile_survivesReload() throws Exception {
        // EP: preserved skipped person is still invalid, and is still there after reload of app

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
        // EP: invalid person is not loaded into valid contact list
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(PRESERVED_SKIPPED_PERSONS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        AddressBook addressBook = dataFromFile.toModelType();

        // The preserved skipped person should NOT be loaded as a valid contact.
        assertEquals(1, addressBook.getPersonList().size()); // only David Li since he is valid
        assertEquals("David Li", addressBook.getPersonList().get(0).getName().fullName);
    }

    @Test
    public void toModelType_preservedPersonStillInvalid_warningIsRegeneratedNotRestored() throws Exception {
        // EP: warning exists in JSON file -> must be regenerated from current state, not read from file
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(PRESERVED_SKIPPED_PERSONS_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        // Before toModelType(), warnings must be empty even though the JSON file has them.
        assertEquals(0, dataFromFile.getLoadWarnings().size());

        dataFromFile.toModelType();

        // Warning still appears because Alex still fails - but it was regenerated, not restored.
        assertTrue(dataFromFile.getLoadWarnings().stream().anyMatch(w -> w.contains("FakeAssignment")));
    }

    @Test
    public void toModelType_preservedSkippedGroupsInFile_survivesReload() throws Exception {
        //EP: preserved group is still invalid and remains there after reload of app
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
    public void toModelType_assignmentWithSpecialCharacterName_skipsGroupAndAddsWarning() throws Exception {
        // EP: group has an assignment with an invalid name -> whole group is skipped
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
        // EP: group has no "name:" field
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(MISSING_NAME_GROUP_FILE,
                JsonSerializableAddressBook.class).orElseThrow();

        dataFromFile.toModelType();

        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("entry #1 (missing name)"));
    }

    @Test
    public void toModelType_preservedPersonNowValid_isMovedToValidPersonAndNoWarning() throws Exception {
        //EP: preserved person is now valid, moved to valid list and no warning is generated
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
        //EP: preserved group is now valid, and moved to valid list
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
        // EP: preserved group and person is now valid, both are loaded into app.

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

    @Test
    public void toModelType_onlyStringGroupEntry_skipsWithCleanMessage() throws Exception {
        // JSON: "groups": [ ] field is wrong
        // JSON: "groups": ["T02"]
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(
                GROUP_ONLY_HAS_NAME_STRING,
                JsonSerializableAddressBook.class).get();

        dataFromFile.toModelType(); // needs toModelType() to trigger loading

        List<String> warnings = dataFromFile.getLoadWarnings();
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains(CLEAN_ERROR_MESSAGE));
        assertFalse(warnings.get(0).contains("JsonAdaptedGroup"));
        assertFalse(warnings.get(0).contains("instantiate value of type"));
    }

    @Test
    public void toModelType_onlyStringSkippedGroupEntry_skipsWithCleanMessage() throws Exception {
        // EP: "preservedSkippedGroups": [ ] field is wrong
        // JSON: "preservedSkippedGroups": ["T02"]
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(
                PRESERVED_GROUP_ONLY_HAS_NAME_STRING,
                JsonSerializableAddressBook.class).get();

        dataFromFile.toModelType(); // needs toModelType() to trigger loading

        List<String> warnings = dataFromFile.getLoadWarnings();
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains(CLEAN_ERROR_MESSAGE));
        assertFalse(warnings.get(0).contains("JsonAdaptedGroup"));
        assertFalse(warnings.get(0).contains("instantiate value of type"));
    }

    @Test
    public void toModelType_onlyStringInNamePersonEntry_skipsWithCleanMessage() throws Exception {
        // EP: "persons": [ ] field is wrong
        // JSON: "persons" : [ "Alice" ]

        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(
                PERSON_ONLY_HAS_NAME_STRING,
                JsonSerializableAddressBook.class).get();

        dataFromFile.toModelType(); // needs toModelType() to trigger loading

        List<String> warnings = dataFromFile.getLoadWarnings();
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains(CLEAN_ERROR_MESSAGE));
        assertFalse(warnings.get(0).contains("JsonAdaptedPerson"));
        assertFalse(warnings.get(0).contains("instantiate value of type"));
    }

    @Test
    public void toModelType_onlyStringInNameSkippedPersonEntry_skipsWithCleanMessage() throws Exception {
        // EP: "preservedSkippedPersons": [ ] field is wrong
        // JSON: "preservedSkippedPersons" : [ "Alice" ]

        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(
                PRESERVED_PERSON_ONLY_HAS_NAME_STRING,
                JsonSerializableAddressBook.class).get();

        dataFromFile.toModelType(); // needs toModelType() to trigger loading

        List<String> warnings = dataFromFile.getLoadWarnings();
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains(CLEAN_ERROR_MESSAGE));
        assertFalse(warnings.get(0).contains("JsonAdaptedPerson"));
        assertFalse(warnings.get(0).contains("instantiate value of type"));
        assertTrue(warnings.get(0).contains("entry #1 (missing name)"));
    }

    @Test
    public void toModelType_validPersonWithPreservedSkipped_bothOutcomesIndependent() throws Exception {
        // EP: JSON has valid person and an invalid person in preservedSkippedPersons

        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(VALID_PERSON_WITH_PRESERVED_PERSON,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBook = dataFromFile.toModelType();

        // Valid person loads successfully
        assertEquals(1, addressBook.getPersonList().size());
        assertEquals("Hans Muster", addressBook.getPersonList().get(0).getNameValue());

        // Invalid preserved person stays preserved with a fresh warning
        assertEquals(1, dataFromFile.getPreservedSkippedPersons().size());
        assertEquals(1, dataFromFile.getLoadWarnings().size());
        assertTrue(dataFromFile.getLoadWarnings().get(0).contains("Skipped invalid contact"));
    }

    @Test
    public void toModelType_twoInvalidGroupsWithNoName_eachGetsCorrectEntryNumber() throws Exception {
        // EP: 2 invalid group entries with no name, each will get an entry # that is incremented correctly.
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TWO_INVALID_GROUPS_WITH_NO_NAME,
                JsonSerializableAddressBook.class).get();
        dataFromFile.toModelType();

        List<String> warnings = dataFromFile.getLoadWarnings();
        assertEquals(2, warnings.size());
        assertTrue(warnings.get(0).contains("entry #1 (missing name)"));
        assertTrue(warnings.get(1).contains("entry #2 (missing name)"));
    }
}
