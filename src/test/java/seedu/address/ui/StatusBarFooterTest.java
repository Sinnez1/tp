package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;

public class StatusBarFooterTest {

    @BeforeAll
    public static void initFxRuntime() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // JavaFX runtime already started.
        }
    }

    @Test
    public void constructor_bindsCurrentGroupLabel() throws Exception {
        StatusBarFooter footer = runOnFxThread(() ->
                new StatusBarFooter(Path.of("data", "TAA_savefile.json"),
                        new SimpleStringProperty("No Group Selected")));

        Label currentViewStatus = getPrivateField(footer, "currentViewStatus", Label.class);

        assertEquals("Current Group: No Group Selected", currentViewStatus.getText());
    }

    private static <T> T runOnFxThread(FxSupplier<T> supplier) throws Exception {
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                result.set(supplier.get());
            } catch (Throwable t) {
                error.set(t);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        if (error.get() != null) {
            throw new RuntimeException(error.get());
        }
        return result.get();
    }

    private static <T> T getPrivateField(Object target, String fieldName, Class<T> type) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return type.cast(field.get(target));
    }

    @FunctionalInterface
    private interface FxSupplier<T> {
        T get() throws Exception;
    }
}
