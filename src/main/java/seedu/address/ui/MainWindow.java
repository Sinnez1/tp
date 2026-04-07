package seedu.address.ui;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";
    private static final double COMMANDBOX_STARTUP_HEIGHT_PX = 45.0;
    private static final double RESULTDISPLAY_STARTUP_HEIGHT_PX = 170.0;

    /** Fraction of screen size used when the saved/default size is too large. */
    private static final double SCREEN_FIT_RATIO = WindowLayoutCalculator.SCREEN_FIT_RATIO;

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private PersonListPanel personListPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;

    // resizeable container containing the following 3 Placeholders:
    @FXML
    private SplitPane mainSplitPane;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane personListPanelPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        // UiPart loads MainWindow.fxml during super(), so the Stage already has
        // the minWidth/minHeight declared in FXML when we use its size here:
        setWindowSizeAndPosition(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Displays the provided warning message in the result display.
     *
     * @param message The warning message to display in the result display.
     */
    public void showStartupWarning(String message) {
        resultDisplay.setFeedbackToUser(message); // Set the warning message in the result display.
    }

    /**
     * Returns the {@link ResultDisplay} component.
     * Used by {@link UiManager} to display startup warnings after initialization.
     *
     * @return {@code ResultDisplay}
     */
    public ResultDisplay getResultDisplay() {
        return resultDisplay;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        CommandBox commandBox = new CommandBox(
                this::executeCommand,
                // For command autocompletion feature:
                resultDisplay::getResultText,
                resultDisplay::setFeedbackToUser
        );
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        personListPanel = new PersonListPanel(
                logic.getFilteredPersonList(),
                logic.getAddressBook().getPersonList(),
                logic.getAddressBook().getGroupList(),
                logic.attendanceViewActiveProperty(),
                logic.activeGroupNameProperty(),
                logic.activeSessionDateProperty(),
                logic.visibleSessionRangeStartProperty(),
                logic.visibleSessionRangeEndProperty(),
                this::executeCommand);
        personListPanelPlaceholder.getChildren().add(personListPanel.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(
                logic.getAddressBookFilePath(),
                logic.currentViewProperty());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        // Make commandBox and resultDisplay not resize when resizing app window:
        SplitPane.setResizableWithParent(commandBoxPlaceholder, false);
        SplitPane.setResizableWithParent(resultDisplayPlaceholder, false);
        SplitPane.setResizableWithParent(personListPanelPlaceholder, true); // TRUE to absorb window resizes

        // Calculate startup heights of the 2 resizeable placeholders (commandBox, resultDisplay):
        Platform.runLater(this::calculateSplitPaneStartupHeights);
    }

    private void calculateSplitPaneStartupHeights() {
        // Default to minimum height first
        double commandBoxStartupHeightRatio = 0;
        double resultDisplayStartupHeightRatio = 0;

        final double mainSplitPaneHeight = mainSplitPane.getHeight();
        if (mainSplitPaneHeight > 0) {
            commandBoxStartupHeightRatio = COMMANDBOX_STARTUP_HEIGHT_PX / mainSplitPaneHeight;
            resultDisplayStartupHeightRatio = (COMMANDBOX_STARTUP_HEIGHT_PX + RESULTDISPLAY_STARTUP_HEIGHT_PX)
                    / mainSplitPaneHeight;
        }

        mainSplitPane.setDividerPositions(commandBoxStartupHeightRatio, resultDisplayStartupHeightRatio);
    }

    /**
     * Sets the default size based on {@code guiSettings}, clamping to 90 % of the
     * relevant screen's visual bounds when the requested size would overflow it.
     * If even 90 % of the screen is smaller than the application's minimum window
     * size, an error dialog is shown via {@code Platform.runLater} after startup.
     */
    private void setWindowSizeAndPosition(GuiSettings guiSettings) {
        WindowLayoutCalculator.Size effectiveWindowSize = calculateEffectiveWindowSize(guiSettings);
        setWindowSize(effectiveWindowSize);
        setWindowPosition(guiSettings);
    }

    private WindowLayoutCalculator.Size calculateEffectiveWindowSize(GuiSettings guiSettings) {
        final double requestedWidth = guiSettings.getWindowWidth();
        final double requestedHeight = guiSettings.getWindowHeight();
        Rectangle2D screenBounds = getScreenBoundsForWindow(guiSettings);

        WindowLayoutCalculator.Size effective = WindowLayoutCalculator.calculateEffectiveSize(
                requestedWidth, requestedHeight,
                screenBounds.getWidth(), screenBounds.getHeight());

        if (effective.width() != requestedWidth || effective.height() != requestedHeight) {
            logger.warning(String.format(
                    "Window size (%.0f x %.0f) exceeds screen bounds (%.0f x %.0f). "
                            + "Resizing to %.0f%% of screen: %.0f x %.0f.",
                    requestedWidth, requestedHeight,
                    screenBounds.getWidth(), screenBounds.getHeight(),
                    SCREEN_FIT_RATIO * 100, effective.width(), effective.height())
            );

            final double minimumWidth = getMinimumWindowWidth();
            final double minimumHeight = getMinimumWindowHeight();

            if (effective.width() < minimumWidth || effective.height() < minimumHeight) {
                Platform.runLater(() -> showScreenTooSmallError(screenBounds));

                logger.warning(String.format(
                        "Screen resolution (%.0f x %.0f) is too small for the minimum window size (%.0f x %.0f).",
                        screenBounds.getWidth(), screenBounds.getHeight(), minimumWidth, minimumHeight)
                );
            }
        }

        return effective;
    }

    private void setWindowSize(WindowLayoutCalculator.Size effectiveWindowSize) {
        primaryStage.setWidth(effectiveWindowSize.width());
        primaryStage.setHeight(effectiveWindowSize.height());
    }

    private void setWindowPosition(GuiSettings guiSettings) {
        if (guiSettings.getWindowCoordinates() != null) {
            final int x = (int) guiSettings.getWindowCoordinates().getX();
            final int y = (int) guiSettings.getWindowCoordinates().getY();
            if (isWithinScreenBounds(x, y)) {
                primaryStage.setX(x);
                primaryStage.setY(y);
            }
        }
    }

    /**
     * Returns the minimum window width defined by MainWindow.fxml.
     */
    private double getMinimumWindowWidth() {
        return primaryStage.getMinWidth();
    }

    /**
     * Returns the minimum window height defined by MainWindow.fxml.
     */
    private double getMinimumWindowHeight() {
        return primaryStage.getMinHeight();
    }

    /**
     * Returns the visual bounds of the screen that the window should open on.
     * Uses the screen containing the saved window coordinates when available;
     * falls back to the primary screen otherwise.
     */
    private Rectangle2D getScreenBoundsForWindow(GuiSettings guiSettings) {
        if (guiSettings.getWindowCoordinates() != null) {
            int x = (int) guiSettings.getWindowCoordinates().getX();
            int y = (int) guiSettings.getWindowCoordinates().getY();
            for (Screen screen : Screen.getScreens()) {
                if (screen.getVisualBounds().contains(x, y)) {
                    return screen.getVisualBounds();
                }
            }
        }
        return Screen.getPrimary().getVisualBounds();
    }

    /**
     * Shows an error dialog informing the user that their monitor resolution is
     * too small for the application's minimum window size.
     *
     * @param screenBounds Visual bounds of the screen the window would open on.
     */
    private void showScreenTooSmallError(final Rectangle2D screenBounds) {
        double minimumWidth = getMinimumWindowWidth();
        double minimumHeight = getMinimumWindowHeight();

        final Alert.AlertType alertType = Alert.AlertType.ERROR;
        final Stage owner = getPrimaryStage();
        final String title = "Screen Resolution Too Small";
        final String header = "Monitor resolution is too small";
        final String content = String.format(
                "Your screen resolution (%.0f x %.0f) is too small to display the application"
                        + " within its minimum required size (%.0f x %.0f pixels).%n%n"
                        + "The application will still open, but parts of the window may be off-screen.",
                screenBounds.getWidth(), screenBounds.getHeight(),
                minimumWidth, minimumHeight);

        UiManager.showAlertDialogAndWait(owner, alertType, title, header, content);
    }

    /**
     * Returns true if the given app window position is in the screen's visual bounds.
     */
    private boolean isWithinScreenBounds(int x, int y) {
        // Require that at least the top-left corner is within this screen
        List<WindowLayoutCalculator.ScreenBounds> bounds = Screen.getScreens().stream()
                .map(s -> new WindowLayoutCalculator.ScreenBounds(
                        s.getVisualBounds().getMinX(), s.getVisualBounds().getMinY(),
                        s.getVisualBounds().getWidth(), s.getVisualBounds().getHeight()))
                .collect(Collectors.toList());
        return WindowLayoutCalculator.isWithinAnyBounds(x, y, bounds);
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else if (helpWindow.isMinimized()) {
            helpWindow.restoreAndFocus();
        } else {
            helpWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    public PersonListPanel getPersonListPanel() {
        return personListPanel;
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }
}
