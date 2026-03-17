package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.GuiSettings;

/**
 * Represents User's preferences.
 */
public class UserPrefs implements ReadOnlyUserPrefs {

    private GuiSettings guiSettings = new GuiSettings();
    private Path addressBookFilePath = Paths.get("data" , "addressbook.json");
    private String lastActiveClassSpaceName;
    private String lastActiveSessionDate;
    private boolean attendanceViewActive;

    /**
     * Creates a {@code UserPrefs} with default values.
     */
    public UserPrefs() {}

    /**
     * Creates a {@code UserPrefs} with the prefs in {@code userPrefs}.
     */
    public UserPrefs(ReadOnlyUserPrefs userPrefs) {
        this();
        resetData(userPrefs);
    }

    /**
     * Resets the existing data of this {@code UserPrefs} with {@code newUserPrefs}.
     */
    public void resetData(ReadOnlyUserPrefs newUserPrefs) {
        requireNonNull(newUserPrefs);
        setGuiSettings(newUserPrefs.getGuiSettings());
        setAddressBookFilePath(newUserPrefs.getAddressBookFilePath());
        setLastActiveClassSpaceName(newUserPrefs.getLastActiveClassSpaceName().orElse(null));
        setLastActiveSessionDate(newUserPrefs.getLastActiveSessionDate().orElse(null));
        setAttendanceViewActive(newUserPrefs.isAttendanceViewActive());
    }

    public GuiSettings getGuiSettings() {
        return guiSettings;
    }

    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        this.guiSettings = guiSettings;
    }

    public Path getAddressBookFilePath() {
        return addressBookFilePath;
    }

    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        this.addressBookFilePath = addressBookFilePath;
    }

    public Optional<String> getLastActiveClassSpaceName() {
        return Optional.ofNullable(lastActiveClassSpaceName);
    }

    public void setLastActiveClassSpaceName(String lastActiveClassSpaceName) {
        this.lastActiveClassSpaceName = lastActiveClassSpaceName;
    }

    public Optional<String> getLastActiveSessionDate() {
        return Optional.ofNullable(lastActiveSessionDate);
    }

    public void setLastActiveSessionDate(String lastActiveSessionDate) {
        this.lastActiveSessionDate = lastActiveSessionDate;
    }

    public boolean isAttendanceViewActive() {
        return attendanceViewActive;
    }

    public void setAttendanceViewActive(boolean attendanceViewActive) {
        this.attendanceViewActive = attendanceViewActive;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UserPrefs)) {
            return false;
        }

        UserPrefs otherUserPrefs = (UserPrefs) other;
        return guiSettings.equals(otherUserPrefs.guiSettings)
                && addressBookFilePath.equals(otherUserPrefs.addressBookFilePath)
                && Objects.equals(lastActiveClassSpaceName, otherUserPrefs.lastActiveClassSpaceName)
                && Objects.equals(lastActiveSessionDate, otherUserPrefs.lastActiveSessionDate)
                && attendanceViewActive == otherUserPrefs.attendanceViewActive;
    }

    @Override
    public int hashCode() {
        return Objects.hash(guiSettings, addressBookFilePath,
                lastActiveClassSpaceName, lastActiveSessionDate, attendanceViewActive);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Gui Settings : " + guiSettings);
        sb.append("\nLocal data file location : " + addressBookFilePath);
        sb.append("\nLast active class space : " + lastActiveClassSpaceName);
        sb.append("\nLast active session date : " + lastActiveSessionDate);
        sb.append("\nAttendance view active : " + attendanceViewActive);
        return sb.toString();
    }

}
