package seedu.address.ui;

import java.util.List;

/**
 * Pure-Java utility for computing window geometry.
 * Contains no JavaFX dependency so it can be unit-tested in a headless environment.
 */
public class WindowLayoutCalculator {

    /** Fraction of the screen to be used when the saved size is larger than the screen. */
    public static final double SCREEN_FIT_RATIO = 0.9;

    private WindowLayoutCalculator() {} // utility class

    /** Effective window dimensions returned by {@link #calculateEffectiveSize}. */
    public record Size(double width, double height) {}

    /**
     * Screen bounds expressed as plain doubles – no JavaFX dependency.
     */
    public record ScreenBounds(double minX, double minY, double width, double height) {
        /**
         * Returns {@code true} if the point (x, y) lies within this bound (inclusive on all edges).
         */
        public boolean contains(double x, double y) {
            return x >= minX && x <= minX + width && y >= minY && y <= minY + height;
        }
    }

    /**
     * Returns the effective window size.
     * If the requested size exceeds the screen bounds it is clamped to
     * {@code SCREEN_FIT_RATIO} × the relevant screen dimension.
     */
    public static Size calculateEffectiveSize(
            double requestedWidth, double requestedHeight,
            double screenWidth, double screenHeight) {
        if (requestedWidth > screenWidth || requestedHeight > screenHeight) {
            return new Size(screenWidth * SCREEN_FIT_RATIO, screenHeight * SCREEN_FIT_RATIO);
        }
        return new Size(requestedWidth, requestedHeight);
    }

    /**
     * Returns {@code true} if the point (x, y) lies within any of the supplied bounds.
     */
    public static boolean isWithinAnyBounds(int x, int y, List<ScreenBounds> bounds) {
        return bounds.stream().anyMatch(b -> b.contains(x, y));
    }
}
