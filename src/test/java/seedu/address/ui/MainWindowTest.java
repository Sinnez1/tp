package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;

public class MainWindowTest {

    private static final double ASSERTION_EPSILON = 1e-6;

    @BeforeAll
    public static void setUpJavaFxToolkit() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            // JavaFX runtime already started by another test.
            latch.countDown();
        }
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void constructor_inBoundsSizeAndCoordinates_appliesRequestedValues() throws Exception {
        runOnFxThreadAndWait(() -> {
            // EP: requested size and coordinates are valid and within visual bounds.
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            double requestedWidth = bounds.getWidth() * 0.8;
            double requestedHeight = bounds.getHeight() * 0.8;
            int x = (int) bounds.getMinX() + 20;
            int y = (int) bounds.getMinY() + 20;

            GuiSettings guiSettings = new GuiSettings(requestedWidth, requestedHeight, x, y);
            Stage stage = new Stage();
            MainWindow mainWindow = new MainWindow(stage, new MainWindowLogicStub(guiSettings));

            assertEquals(requestedWidth, stage.getWidth(), ASSERTION_EPSILON);
            assertEquals(requestedHeight, stage.getHeight(), ASSERTION_EPSILON);
            assertEquals(x, stage.getX(), ASSERTION_EPSILON);
            assertEquals(y, stage.getY(), ASSERTION_EPSILON);
            assertEquals(stage, mainWindow.getPrimaryStage());

            stage.hide();
        });
    }

    @Test
    public void constructor_oversizedWindow_clampsToScreenFitRatio() throws Exception {
        runOnFxThreadAndWait(() -> {
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            // BVA: requested width/height exceed screen bounds.
            double requestedWidth = bounds.getWidth() + 500;
            double requestedHeight = bounds.getHeight() + 500;
            int x = (int) bounds.getMinX() + 20;
            int y = (int) bounds.getMinY() + 20;

            GuiSettings guiSettings = new GuiSettings(requestedWidth, requestedHeight, x, y);
            Stage stage = new Stage();
            MainWindow mainWindow = new MainWindow(stage, new MainWindowLogicStub(guiSettings));

            // EP: oversized windows are clamped to 90% of the target screen.
            assertEquals(bounds.getWidth() * 0.9, stage.getWidth(), ASSERTION_EPSILON);
            assertEquals(bounds.getHeight() * 0.9, stage.getHeight(), ASSERTION_EPSILON);
            assertEquals(stage, mainWindow.getPrimaryStage());

            stage.hide();
        });
    }

    @Test
    public void constructor_outOfBoundsCoordinates_doesNotApplyCoordinates() throws Exception {
        runOnFxThreadAndWait(() -> {
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            double requestedWidth = bounds.getWidth() * 0.8;
            double requestedHeight = bounds.getHeight() * 0.8;
            // BVA: coordinates are far outside all screen visual bounds.
            int invalidX = (int) bounds.getMinX() - 10_000;
            int invalidY = (int) bounds.getMinY() - 10_000;

            GuiSettings guiSettings = new GuiSettings(requestedWidth, requestedHeight, invalidX, invalidY);
            Stage stage = new Stage();
            MainWindow mainWindow = new MainWindow(stage, new MainWindowLogicStub(guiSettings));

            // EP: invalid coordinates are ignored rather than applied.
            assertNotEquals(invalidX, stage.getX(), ASSERTION_EPSILON);
            assertNotEquals(invalidY, stage.getY(), ASSERTION_EPSILON);
            assertEquals(stage, mainWindow.getPrimaryStage());

            stage.hide();
        });
    }

    private static void runOnFxThreadAndWait(ThrowingRunnable action) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> throwableRef = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable t) {
                throwableRef.set(t);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        if (throwableRef.get() != null) {
            throw new RuntimeException(throwableRef.get());
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static class MainWindowLogicStub implements Logic {
        private final GuiSettings guiSettings;

        MainWindowLogicStub(GuiSettings guiSettings) {
            this.guiSettings = guiSettings;
        }

        @Override
        public GuiSettings getGuiSettings() {
            return guiSettings;
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CommandResult execute(String commandText) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReadOnlyStringProperty currentViewProperty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReadOnlyObjectProperty<GroupName> activeGroupNameProperty() {
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
            throw new UnsupportedOperationException();
        }

        @Override
        public ReadOnlyObjectProperty<LocalDate> visibleSessionRangeEndProperty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new UnsupportedOperationException();
        }
    }
}
