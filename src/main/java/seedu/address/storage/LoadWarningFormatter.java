package seedu.address.storage;

/**
 * Utility class for formatting warning messages produced during address book loading.
 */
public class LoadWarningFormatter {

    private LoadWarningFormatter() {}

    /**
     * Formats a warning message for an invalid entry (person or group) that was skipped during loading.
     *
     * @param entityType  Entity type, e.g. contact or a group
     * @param identifier  Display identifier for the entry, e.g. {@code "'Alice'"} or {@code "entry #3 (missing name)"}.
     * @param errorMessage Validation error message describing why the entry was skipped.
     * @return Formatted warning string.
     */
    static String formatInvalidEntryWarning(String entityType, String identifier, String errorMessage) {
        String[] errors = errorMessage.split(";\\s*");

        StringBuilder sb = new StringBuilder("Skipped invalid ")
                .append(entityType)
                .append(" ")
                .append(identifier)
                .append(":\n");

        for (String error : errors) {
            sb.append("- ").append(error).append("\n");
        }

        return sb.toString().trim();
    }

    /**
     * Formats a warning message for a duplicate entry that was skipped during loading.
     *
     * @param entityType Entity type, e.g. contact or a group.
     * @param identifier Display identifier for the entry.
     * @return Formatted warning string.
     */
    private static String formatDuplicateEntryWarning(String entityType, String identifier) {
        return "Skipped duplicate " + entityType + ": " + identifier;
    }
}
