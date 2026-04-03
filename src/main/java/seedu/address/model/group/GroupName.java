package seedu.address.model.group;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the name of a group.
 * Guarantees: immutable; is valid as declared in {@link #isValidGroupName(String)}.
 */
// @@author ongrussell
public class GroupName {

    public static final String MESSAGE_CONSTRAINTS = "Group names should only contain letters, numbers, "
            + "spaces, hyphens, and underscores, and it should not be blank.";
    private static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} _-]*";

    public final String value;

    /**
     * Constructs a {@code GroupName}.
     */
    public GroupName(String name) {
        requireNonNull(name);
        String trimmedName = name.trim();
        checkArgument(isValidGroupName(trimmedName), MESSAGE_CONSTRAINTS);
        value = trimmedName;
    }

    /**
     * Returns true if a given string is a valid group name.
     */
    public static boolean isValidGroupName(String test) {
        return test != null && test.trim().matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof GroupName)) {
            return false;
        }

        GroupName otherName = (GroupName) other;
        return value.equalsIgnoreCase(otherName.value);
    }

    @Override
    public int hashCode() {
        return value.toLowerCase().hashCode();
    }
}
// @@author
