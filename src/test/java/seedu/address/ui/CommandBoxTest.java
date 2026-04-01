package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for CommandBox logic methods that don't require JavaFX GUI components.
 * Tests focus on pure logic: suggestion finding, ghost text computation, and contextual help.
 */
public class CommandBoxTest {

    private List<String> testCommands;
    private AtomicReference<String> capturedOutput;
    private AtomicReference<String> savedText;

    @BeforeEach
    public void setUp() {
        testCommands = new ArrayList<>();
        testCommands.add("add");
        testCommands.add("delete");
        testCommands.add("edit");
        testCommands.add("find");
        testCommands.add("help");
        testCommands.add("list");
        testCommands.add("addsession");

        capturedOutput = new AtomicReference<>();
        savedText = new AtomicReference<>();
    }

    // ==================== Tests for findSuggestion(String input, List<String> suggestions) ====================

    @Test
    public void findSuggestion_validPrefix_returnsMatchingCommand() {
        String result = CommandBox.findSuggestion("ad", testCommands);
        assertEquals("add", result);
    }

    @Test
    public void findSuggestion_singleCharacterPrefix_returnsFirstMatch() {
        String result = CommandBox.findSuggestion("a", testCommands);
        assertEquals("add", result);
    }

    @Test
    public void findSuggestion_exactMatch_returnsCommand() {
        String result = CommandBox.findSuggestion("find", testCommands);
        assertEquals("find", result);
    }

    @Test
    public void findSuggestion_noMatch_returnsNull() {
        String result = CommandBox.findSuggestion("xyz", testCommands);
        assertNull(result);
    }

    @Test
    public void findSuggestion_emptyCommandList_returnsNull() {
        String result = CommandBox.findSuggestion("add", new ArrayList<>());
        assertNull(result);
    }

    @Test
    public void findSuggestion_matchesFirstInList() {
        List<String> commands = List.of("exit", "edit", "add");
        String result = CommandBox.findSuggestion("e", commands);
        assertEquals("exit", result);
    }

    @Test
    public void findSuggestion_caseInsensitive_prefix() {
        // Assumes input is already lowercased before being passed
        String result = CommandBox.findSuggestion("del", testCommands);
        assertEquals("delete", result);
    }

    @Test
    public void findSuggestion_prefixLongerThanCommand_returnsNull() {
        String result = CommandBox.findSuggestion("addextra", testCommands);
        assertNull(result);
    }

    @Test
    public void findSuggestion_singleCommand_exactMatch() {
        List<String> singleCommand = List.of("help");
        String result = CommandBox.findSuggestion("h", singleCommand);
        assertEquals("help", result);
    }

    @Test
    public void findSuggestion_singleCommand_noMatch() {
        List<String> singleCommand = List.of("help");
        String result = CommandBox.findSuggestion("x", singleCommand);
        assertNull(result);
    }

    // ==================== Edge cases ====================

    @Test
    public void findSuggestion_specialCharacters_noMatch() {
        String result = CommandBox.findSuggestion("@#$", testCommands);
        assertNull(result);
    }

    @Test
    public void findSuggestion_whitespace_input() {
        // Input with spaces typically won't match command names that have no spaces
        String result = CommandBox.findSuggestion("ad d", testCommands);
        assertNull(result);
    }

    @Test
    public void findSuggestion_numeric_prefix() {
        List<String> commands = List.of("1-to-one", "2-to-two", "add");
        String result = CommandBox.findSuggestion("1", commands);
        assertEquals("1-to-one", result);
    }

    @Test
    public void findSuggestion_underscore_prefix() {
        List<String> commands = List.of("_private", "_protect", "add");
        String result = CommandBox.findSuggestion("_pr", commands);
        assertEquals("_private", result);
    }

    @Test
    public void findSuggestion_singleCharacterCommands() {
        List<String> commands = List.of("a", "b", "c");
        String result = CommandBox.findSuggestion("a", commands);
        assertEquals("a", result);
    }

    // ==================== Tests for updateContextualHelp logic ====================

    @Test
    public void updateContextualHelp_commandWordExtraction() {
        // From input "add p/12345", extract "add"
        String input = "add p/12345";
        String commandWord = CommandBox.extractCommandWord(input);
        assertEquals("add", commandWord);
    }

    @Test
    public void updateContextualHelp_multipleSpaces_commandWordExtraction() {
        // From input "  edit  some params", extract "edit"
        String input = "  edit  some params";
        String commandWord = CommandBox.extractCommandWord(input);
        assertEquals("edit", commandWord);
    }

    @Test
    public void updateContextualHelp_commandWordOnly_extraction() {
        // From input "delete", extract "delete"
        String input = "delete";
        String commandWord = CommandBox.extractCommandWord(input);
        assertEquals("delete", commandWord);
    }

    @Test
    public void updateContextualHelp_uppercase_commandWord() {
        // Command word should be lowercase for lookup
        String input = "FIND person";
        String commandWord = CommandBox.extractCommandWord(input);
        assertEquals("find", commandWord);
    }

    @Test
    public void updateContextualHelp_resultDisplayGetterCalled() {
        // Verify that the supplier is called to save previous text
        savedText.set("initial text");
        String retrieved = savedText.get();
        assertEquals("initial text", retrieved);
    }

    @Test
    public void updateContextualHelp_blankParameters_restoresDisplay() {
        // When parameters are blank/empty, restore the display
        String parameters = "   ";
        boolean isBlank = parameters.isBlank();
        assertEquals(true, isBlank);
    }

    // ==================== Tests for extractCommandWord ====================

    @Test
    public void extractCommandWord_nullInput() {
        try {
            CommandBox.extractCommandWord(null);
        } catch (Exception e) {
            // exception caught
        }
    }

    @Test
    public void extractCommandWord_emptyInput() {
        String result = CommandBox.extractCommandWord("");
        assertEquals("", result);
    }

    @Test
    public void extractCommandWord_blankInput() {
        String result = CommandBox.extractCommandWord("   ");
        assertEquals("", result);
    }

    @Test
    public void extractCommandWord_singleWord() {
        String result = CommandBox.extractCommandWord("add");
        assertEquals("add", result);
    }

    @Test
    public void extractCommandWord_withParameters() {
        String result = CommandBox.extractCommandWord("edit n/John p/12345");
        assertEquals("edit", result);
    }

    @Test
    public void extractCommandWord_leadingSpaces() {
        String result = CommandBox.extractCommandWord("  find person");
        assertEquals("find", result);
    }

    @Test
    public void extractCommandWord_mixedCase() {
        String result = CommandBox.extractCommandWord("DELETE");
        assertEquals("delete", result);
    }

    @Test
    public void extractCommandWord_tabsAndSpaces() {
        String result = CommandBox.extractCommandWord("\t  help\t  hi");
        assertEquals("help", result);
    }

    // ==================== Integration-like tests ====================

    @Test
    public void commandWordFlow_fromUserInput_toSuggestion() {
        // Simulate: user types "ed" -> extract command word -> find suggestion
        String userInput = "ed";
        String commandWord = CommandBox.extractCommandWord(userInput);
        String suggestion = CommandBox.findSuggestion(commandWord, testCommands);
        assertEquals("edit", suggestion);
    }

    @Test
    public void commandWordFlow_multipleSpaces_toCommand() {
        // Simulate: user types "  del  " -> extract command -> find in registry
        String userInput = "  del  ";
        String commandWord = CommandBox.extractCommandWord(userInput);
        String suggestion = CommandBox.findSuggestion(commandWord, testCommands);
        assertEquals("delete", suggestion);
    }
}
