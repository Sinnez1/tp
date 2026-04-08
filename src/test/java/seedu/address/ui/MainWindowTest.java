package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Contains no JavaFX dependency so it can be unit-tested in a headless environment (GitHub Actions),
 * at the cost of not actually testing JXF GUI behavior, and only the calculation logic.
 */
public class MainWindowTest {

    private static final double ASSERTION_EPSILON = 1e-6;
    private static final double SCREEN_W = 1920;
    private static final double SCREEN_H = 1080;

    // ── calculateEffectiveSize ──────────────────────────────────────────────

    @Test
    public void calculateEffectiveSize_inBoundsSizeAndCoordinates_appliesRequestedValues() {
        // EP: requested size fits within the screen → returned unchanged.
        double requestedWidth = SCREEN_W * 0.8;
        double requestedHeight = SCREEN_H * 0.8;

        WindowLayoutCalculator.Size result = WindowLayoutCalculator.calculateEffectiveSize(
                requestedWidth, requestedHeight, SCREEN_W, SCREEN_H);

        assertEquals(requestedWidth, result.width(), ASSERTION_EPSILON);
        assertEquals(requestedHeight, result.height(), ASSERTION_EPSILON);
    }

    @Test
    public void calculateEffectiveSize_oversizedWindow_clampsToScreenFitRatio() {
        // BVA: requested width/height exceed screen bounds → clamped to 90%.
        double requestedWidth = SCREEN_W + 500;
        double requestedHeight = SCREEN_H + 500;

        WindowLayoutCalculator.Size result = WindowLayoutCalculator.calculateEffectiveSize(
                requestedWidth, requestedHeight, SCREEN_W, SCREEN_H);

        assertEquals(SCREEN_W * 0.9, result.width(), ASSERTION_EPSILON);
        assertEquals(SCREEN_H * 0.9, result.height(), ASSERTION_EPSILON);
    }

    @Test
    public void calculateEffectiveSize_exactlyScreenSize_appliesRequestedValues() {
        // BVA: requested size exactly equals the screen → not clamped (boundary).
        WindowLayoutCalculator.Size result = WindowLayoutCalculator.calculateEffectiveSize(
                SCREEN_W, SCREEN_H, SCREEN_W, SCREEN_H);

        assertEquals(SCREEN_W, result.width(), ASSERTION_EPSILON);
        assertEquals(SCREEN_H, result.height(), ASSERTION_EPSILON);
    }

    // ── isWithinAnyBounds ──────────────────────────────────────────────────

    @Test
    public void isWithinAnyBounds_coordsInsideSingleScreen_returnsTrue() {
        // EP: coordinates lie within the only screen -> returns true
        List<WindowLayoutCalculator.ScreenBounds> bounds = List.of(
                new WindowLayoutCalculator.ScreenBounds(0, 0, SCREEN_W, SCREEN_H));

        assertTrue(WindowLayoutCalculator.isWithinAnyBounds(100, 100, bounds));
    }

    @Test
    public void isWithinAnyBounds_outOfBoundsCoordinates_returnsFalse() {
        // BVA: coordinates are far outside all screen bounds -> returns false
        List<WindowLayoutCalculator.ScreenBounds> bounds = List.of(
                new WindowLayoutCalculator.ScreenBounds(0, 0, SCREEN_W, SCREEN_H));

        assertFalse(WindowLayoutCalculator.isWithinAnyBounds(-10_000, -10_000, bounds));
    }

    @Test
    public void isWithinAnyBounds_coordsOnSecondScreen_returnsTrue() {
        // EP: coordinates miss the first screen but land on the second (multi-monitor) -> returns true
        List<WindowLayoutCalculator.ScreenBounds> bounds = List.of(
                new WindowLayoutCalculator.ScreenBounds(0, 0, SCREEN_W, SCREEN_H),
                new WindowLayoutCalculator.ScreenBounds(SCREEN_W, 0, SCREEN_W, SCREEN_H));

        assertTrue(WindowLayoutCalculator.isWithinAnyBounds((int) SCREEN_W + 50, 50, bounds));
    }
}
