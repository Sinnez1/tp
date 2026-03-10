package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's participation in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidParticipation(int)}
 */
public class Participation {

    public static final String MESSAGE_CONSTRAINTS = "Participation must be an integer from 0 to 5.";

    public final int value;

    /**
     * Constructs a {@code Participation}.
     *
     * @param participation A valid participation value.
     */
    public Participation(int participation) {
        checkArgument(isValidParticipation(participation), MESSAGE_CONSTRAINTS);
        value = participation;
    }

    /**
     * Parses a string into a valid participation value.
     */
    public Participation(String participation) {
        requireNonNull(participation);
        try {
            int parsedValue = Integer.parseInt(participation.trim());
            checkArgument(isValidParticipation(parsedValue), MESSAGE_CONSTRAINTS);
            value = parsedValue;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
    }

    /**
     * Returns true if a given integer is a valid participation value.
     */
    public static boolean isValidParticipation(int test) {
        return test >= 0 && test <= 5;
    }

    /**
     * Returns true if a given string is a valid participation value.
     */
    public static boolean isValidParticipation(String test) {
        requireNonNull(test);
        try {
            int parsedValue = Integer.parseInt(test.trim());
            return isValidParticipation(parsedValue);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Participation)) {
            return false;
        }

        Participation otherParticipation = (Participation) other;
        return value == otherParticipation.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}