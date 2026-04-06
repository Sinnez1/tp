package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final int MAX_LENGTH = 300;

    public static final String MESSAGE_CONSTRAINTS =
            "Name cannot be blank, must be at most 300 characters long "
                    + "and characters like semicolons and <> are invalid.";

    /*
     * The first character of the name must start with a Unicode letter and
     * not be a whitespace otherwise " " (a blank string) becomes a valid input.
     * Separators (space, apostrophe, hyphen, slash) must be followed by a letter or combining mark
     * which prevents invalid names ending with separators or having repeated separators only.
     */
    public static final String VALIDATION_REGEX =
            "[\\p{L}\\p{N}](?:[\\p{L}\\p{M}\\p{N}]|[ '/\\-](?=[\\p{L}\\p{M}\\p{N}]))*";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        requireNonNull(test);
        return test.length() <= MAX_LENGTH && test.matches(VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name)) {
            return false;
        }

        Name otherName = (Name) other;
        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
