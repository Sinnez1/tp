package seedu.address.ui;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.CommandRegistry;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";
    private static final List<String> COMMAND_SUGGESTIONS = CommandRegistry.COMMAND_ATTRIBUTES.keySet()
            .stream()
            .sorted()
            .toList();

    private final CommandExecutor commandExecutor;
    private final Supplier<String> resultDisplayGetter;
    private final Consumer<String> resultDisplaySetter;

    private String savedResultText = null; // text that was in display before help was shown
    private String activeCommandWord = null; // which word triggered the save


    @FXML
    private TextArea commandTextArea;

    @FXML
    private Label ghostTextLabel;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor} and result display callbacks.
     *
     * @param commandExecutor     the executor used to run commands entered by the user
     * @param resultDisplayGetter a supplier that retrieves the current text shown in the result display,
     *                            used to save and restore it when contextual help is shown or dismissed
     * @param resultDisplaySetter a consumer that updates the result display text,
     *                            used to show contextual parameter help when a recognized command word is typed
     */
    public CommandBox(CommandExecutor commandExecutor,
                      Supplier<String> resultDisplayGetter,
                      Consumer<String> resultDisplaySetter) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.resultDisplayGetter = resultDisplayGetter;
        this.resultDisplaySetter = resultDisplaySetter;
        commandTextArea.textProperty().addListener((unused1, unused2, newText) -> {
            setStyleToDefault();
            updateGhostText(newText);
            updateContextualHelp(newText); // new
        });
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextArea.getText();
        if (commandText.isBlank()) {
            return;
        }

        // Reset before setText("") triggers the listener, otherwise the listener
        // would try to restore savedResultText over the command's own result message
        savedResultText = null;
        activeCommandWord = null;

        try {
            commandExecutor.execute(commandText);
            commandTextArea.setText("");
            clearGhostText();
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    private void updateContextualHelp(String input) {
        if (input == null || input.isBlank()) {
            restoreResultDisplay();
            return;
        }

        String commandWord = extractCommandWord(input);
        String parameters = CommandRegistry.COMMAND_ATTRIBUTES.get(commandWord);
        if (parameters == null) {
            parameters = CommandRegistry.COMMAND_ALIASES.get(commandWord);
        }

        if (parameters != null && !parameters.isBlank()) {
            if (!commandWord.equals(activeCommandWord)) {
                if (activeCommandWord == null) {
                    savedResultText = resultDisplayGetter.get();
                    // We only save once and don't overwrite with a half-saved state
                }
                activeCommandWord = commandWord;
                resultDisplaySetter.accept(commandWord + " " + parameters);
            }
            // if same command word as before, do nothing to avoid flickering
        } else {
            restoreResultDisplay();
        }
    }

    static String extractCommandWord(String input) {
        return input.stripLeading().split("\\s+")[0].toLowerCase();
    }

    private void restoreResultDisplay() {
        if (activeCommandWord != null) {
            resultDisplaySetter.accept(savedResultText != null
                            ? savedResultText
                            : "");

            savedResultText = null;
            activeCommandWord = null;
        }
    }

    /**
     * Handles the Enter button pressed event for TextArea,
     * to handle migration from TextField to TextArea,
     * to support resizeable panels.
     */
    @FXML
    private void initialize() {
        commandTextArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            // handle ENTER key
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                event.consume();
                handleCommandEntered();
            }

            // handle TAB key
            if (event.getCode() == KeyCode.TAB) {
                event.consume(); // Prevent focus from shifting to another element
                String suggestion = ghostTextLabel.getText();
                if (!suggestion.isBlank()) {
                    commandTextArea.setText(suggestion + " ");
                    commandTextArea.positionCaret(commandTextArea.getText().length());
                    clearGhostText();
                }
            }
        });
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextArea.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextArea.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Updates the faded ghost text suggestion based on the current input.
     */
    private void updateGhostText(String input) {
        if (input == null || input.isBlank()) {
            clearGhostText();
            return;
        }

        if (input.contains("\n")) {
            clearGhostText();
            return;
        }

        String trimmedInput = input.stripLeading().toLowerCase();
        if (trimmedInput.isEmpty() || trimmedInput.contains(" ")) {
            clearGhostText();
            return;
        }

        String suggestion = findSuggestion(trimmedInput);
        if (suggestion == null || suggestion.equals(trimmedInput)) {
            clearGhostText();
            return;
        }

        ghostTextLabel.setText(suggestion);
        ghostTextLabel.setVisible(true);
    }

    /**
     * Returns the first matching command suggestion for the input.
     */
    private String findSuggestion(String input) {
        return findSuggestion(input, COMMAND_SUGGESTIONS);
    }

    /**
     * Returns the first matching command suggestion for the given input.
     * Static method for easy unit testing without JavaFX dependencies.
     *
     * @param input the user input (should be lowercase)
     * @param suggestions list of available command suggestions
     * @return the first matching suggestion, or null if no match found
     */
    static String findSuggestion(String input, List<String> suggestions) {
        for (String command : suggestions) {
            if (command.startsWith(input)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Clears the ghost text suggestion.
     */
    private void clearGhostText() {
        ghostTextLabel.setText("");
        ghostTextLabel.setVisible(false);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

}
