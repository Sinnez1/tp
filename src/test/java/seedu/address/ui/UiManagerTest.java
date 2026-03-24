package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class UiManagerTest {

    @Test
    public void buildStartUpMessage_fatalLoadError_displaysErrorMessage() {
        Logic logicStub = new LogicStub(0);
        UiManager uiManager = new UiManager(logicStub, List.of());

        List<String> warnings = List.of(
                "FATAL: The save file could not be read — it may contain invalid JSON.\n"
                        + "Your data has NOT been changed and the file will NOT be overwritten.\n"
                        + "Any changes you make in the app during this session will NOT be saved.\n"
                        + "Please fix the file manually and restart the app."
        );

        String result = uiManager.buildStartUpMessage(warnings);

        assertTrue(result.contains("The save file could not be read"));
        assertTrue(result.contains("will NOT be saved"));
        assertTrue(result.contains("Please fix the file manually"));
    }

    @Test
    public void buildStartUpMessage_fatalLoadError_appearsBeforeContactWarnings() {
        Logic logicStub = new LogicStub(0);
        UiManager uiManager = new UiManager(logicStub, List.of());

        List<String> warnings = List.of(
                "FATAL: The save file could not be read.",
                "Skipped invalid contact 'Alice':\n- invalid email"
        );

        String result = uiManager.buildStartUpMessage(warnings);

        // Fatal error should appear before contact warnings
        assertTrue(result.indexOf("save file could not be read")
                < result.indexOf("Skipped invalid contact"));
    }


    @Test
    public void buildStartUpMessage_noWarnings_returnsSuccessMessage() {
        // Create a stub that has exactly 5 contacts loaded.
        Logic logicStub = new LogicStub(5);
        UiManager uiManager = new UiManager(logicStub, List.of());

        String result = uiManager.buildStartUpMessage(List.of());

        assertEquals("5 contacts loaded successfully.", result);
    }

    @Test
    public void buildStartUpMessage_contactAndClassSpaceWarnings_formatsSeparateSections() {
        Logic logicStub = new LogicStub(10);
        UiManager uiManager = new UiManager(logicStub, List.of());

        List<String> warnings = List.of(
                "Skipped invalid contact 'Bob':\n- invalid email",
                "Skipped invalid class space 'T#1':\n- invalid class space name"
        );

        String result = uiManager.buildStartUpMessage(warnings);

        assertTrue(result.contains("10 contacts loaded successfully."));
        assertTrue(result.contains("1 contact could not be loaded and was skipped:"));
        assertTrue(result.contains("1 class space could not be loaded and was skipped:"));
        assertTrue(result.contains("1. Skipped invalid contact 'Bob':\n- invalid email"));
        assertTrue(result.contains("1. Skipped invalid class space 'T#1':\n- invalid class space name"));
    }

    @Test
    public void buildStartUpMessage_multipleWarnings_formatsPluralCorrectly() {
        Logic logicStub = new LogicStub(5);
        UiManager uiManager = new UiManager(logicStub, List.of());

        List<String> warnings = List.of(
                "Skipped invalid contact 'Alice':\n- invalid email",
                "Skipped invalid contact 'Bob':\n- invalid phone"
        );

        String result = uiManager.buildStartUpMessage(warnings);

        assertTrue(result.contains("5 contacts loaded successfully."));
        assertTrue(result.contains("2 contacts could not be loaded and were skipped:"));
        assertTrue(result.contains("1. Skipped invalid contact 'Alice':\n- invalid email"));
        assertTrue(result.contains("2. Skipped invalid contact 'Bob':\n- invalid phone"));
    }

    @Test
    public void buildWarningList_multilineWarnings_separatesContactsWithBlankLine() {
        Logic logicStub = new LogicStub(2);
        UiManager uiManager = new UiManager(logicStub, List.of());

        List<String> warnings = List.of(
                "Skipped invalid contact 'Alice':\n- invalid email\n- invalid matric number",
                "Skipped invalid contact 'Bob':\n- invalid phone"
        );

        String result = uiManager.buildWarningList(warnings);

        String expected = "1. Skipped invalid contact 'Alice':\n"
                + "- invalid email\n"
                + "- invalid matric number\n\n"
                + "2. Skipped invalid contact 'Bob':\n"
                + "- invalid phone\n";

        assertEquals(expected, result);
    }

    @Test
    public void buildStartUpMessage_duplicateContactWarning_classifiedAsContactWarning() {
        Logic logicStub = new LogicStub(1);
        UiManager uiManager = new UiManager(logicStub, List.of());

        List<String> warnings = List.of("Skipped duplicate contact 'Alice' (Matric: A1234567X)");
        String result = uiManager.buildStartUpMessage(warnings);

        assertTrue(result.contains("1 contact could not be loaded and was skipped:"));
    }

    @Test
    public void buildStartUpMessage_duplicateClassSpaceWarning_classifiedAsClassSpaceWarning() {
        Logic logicStub = new LogicStub(1);
        UiManager uiManager = new UiManager(logicStub, List.of());

        List<String> warnings = List.of("Skipped duplicate class space 'T01'");
        String result = uiManager.buildStartUpMessage(warnings);

        assertTrue(result.contains("1 class space could not be loaded and was skipped:"));
    }

    @Test
    public void buildStartUpMessage_unrecognisedWarning_notIncludedInEitherSection() {
        Logic logicStub = new LogicStub(1);
        UiManager uiManager = new UiManager(logicStub, List.of());

        // A warning that doesn't start with any known prefix should be silently ignored
        List<String> warnings = List.of("Some unknown warning type");
        String result = uiManager.buildStartUpMessage(warnings);

        assertFalse(result.contains("could not be loaded"));
    }

    @Test
    public void constructor_noWarnings_defaultsToEmptyStartupWarnings() {
        // Tests the single-arg constructor: new UiManager(logic)
        // which delegates to this(logic, List.of())
        Logic logicStub = new LogicStub(3);
        UiManager uiManager = new UiManager(logicStub);

        // buildStartUpMessage with empty list should still produce the loaded count message
        String result = uiManager.buildStartUpMessage(List.of());
        assertEquals("3 contacts loaded successfully.", result);
    }
    /**
     * A stub class to isolate UiManager string testing from the rest of the application.
     * It provides hardcoded, predictable data for testing.
     */
    private static class LogicStub implements Logic {

        private static final String[] VALID_MATRIC_NUMBERS = { "A0123456J", "A1234567X", "A7654321J", "A0000067Y",
                                                               "A4567891E", "A0000002W", "A1111111M", "A0408987E",
                                                               "A1002345X", "A0304556E"
        };

        private final ReadOnlyAddressBook addressBook;

        LogicStub(int personCount) {
            seedu.address.model.AddressBook temp = new seedu.address.model.AddressBook();

            for (int i = 0; i < personCount; i++) {
                temp.addPerson(new PersonBuilder()
                        .withName("Person " + i)
                        .withPhone(String.format("91234%03d", i))
                        .withEmail("person" + i + "@example.com")
                        .withMatricNumber(VALID_MATRIC_NUMBERS[i])
                        .build());
            }

            this.addressBook = temp;
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return addressBook;
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            return FXCollections.observableArrayList(addressBook.getPersonList());
        }

        @Override
        public Path getAddressBookFilePath() {
            return Paths.get("dummy.json");
        }

        @Override
        public CommandResult execute(String commandText) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReadOnlyStringProperty currentViewProperty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReadOnlyObjectProperty<ClassSpaceName> activeClassSpaceNameProperty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReadOnlyObjectProperty<LocalDate> activeSessionDateProperty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReadOnlyBooleanProperty attendanceViewActiveProperty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReadOnlyObjectProperty<LocalDate> visibleSessionRangeStartProperty() {
            return null;
        }

        @Override
        public ReadOnlyObjectProperty<LocalDate> visibleSessionRangeEndProperty() {
            return null;
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new UnsupportedOperationException();
        }
    }
}
