package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;
import seedu.address.model.person.SessionList;

/**
 * Exports the current view matrix to a CSV file.
 */
public class ExportViewCommand extends Command {

    public static final String COMMAND_WORD = "exportview";
    public static final String COMMAND_PARAMETERS = "[f/FILE_PATH]";

    public static final String DEFAULT_FILE_NAME = "view-export.csv";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Exports the current view to a CSV file.\n"
            + "Parameters: " + COMMAND_PARAMETERS + "\n"
            + "Examples:\n"
            + COMMAND_WORD + " f/exports/t01-view.csv (will overwrite existing file with same name)\n"
            + COMMAND_WORD + " (default filename: " + DEFAULT_FILE_NAME + ")";
    public static final String MESSAGE_SUCCESS = "Exported view to %1$s";
    public static final String MESSAGE_NO_ACTIVE_GROUP =
            "No group selected. Switch to a group before exporting the view.";
    public static final String MESSAGE_EXPORT_FAILED = "Could not export view: %1$s";
    public static final String MESSAGE_INVALID_FILE_NAME =
            "The file name '%1$s' is invalid because it contains illegal character(s): '%2$s'. "
                    + "Please choose a different file name.";

    private static final String INVALID_FILE_NAME_CHARACTERS = "<>:\"|?*";

    private final String filePath;

    public ExportViewCommand() {
        this(DEFAULT_FILE_NAME);
    }

    /**
     * Creates an export command targeting the given file path.
     */
    public ExportViewCommand(String filePath) {
        requireNonNull(filePath);
        this.filePath = filePath.trim().isEmpty() ? DEFAULT_FILE_NAME : filePath.trim();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        GroupName activeGroup = model.getActiveGroupName()
                .orElseThrow(() -> new CommandException(MESSAGE_NO_ACTIVE_GROUP));
        List<Person> persons = List.copyOf(model.getFilteredPersonList());
        List<LocalDate> sessionDates = getSessionDates(persons, activeGroup, model);

        StringBuilder csv = new StringBuilder("Student");
        for (LocalDate sessionDate : sessionDates) {
            csv.append(',').append(sessionDate).append(" Attendance");
            csv.append(',').append(sessionDate).append(" Participation");
        }
        csv.append(System.lineSeparator());

        for (Person person : persons) {
            csv.append(escape(person.getName().fullName));
            for (LocalDate sessionDate : sessionDates) {
                Attendance attendance = person.getAttendance(activeGroup, sessionDate);
                csv.append(',').append(attendance.value);
                csv.append(',').append(person.getParticipation(activeGroup, sessionDate));
            }
            csv.append(System.lineSeparator());
        }

        validateFilePath(filePath);

        Path outputPath;
        try {
            outputPath = Path.of(filePath);
        } catch (InvalidPathException e) {
            throw new CommandException(String.format(MESSAGE_EXPORT_FAILED, e.getMessage()), e);
        }
        try {
            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }
            Files.writeString(outputPath, csv.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CommandException(String.format(MESSAGE_EXPORT_FAILED, e.getMessage()), e);
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, outputPath.toAbsolutePath()));
    }

    private List<LocalDate> getSessionDates(List<Person> persons, GroupName groupName, Model model) {
        Optional<LocalDate> rangeStart = model.getVisibleSessionRangeStart();
        Optional<LocalDate> rangeEnd = model.getVisibleSessionRangeEnd();
        java.util.stream.Stream<Session> groupSessions = model.findGroupByName(groupName)
                .map(Group::getSessions)
                .orElse(List.of())
                .stream();
        java.util.stream.Stream<Session> personSessions = persons.stream()
                .filter(person -> person.hasGroup(groupName))
                .flatMap(person -> person.getGroupSessions()
                        .getOrDefault(groupName, new SessionList())
                        .getSessions()
                        .stream());
        return java.util.stream.Stream.concat(groupSessions, personSessions)
                .map(Session::getDate)
                .filter(date -> rangeStart.isEmpty() || !date.isBefore(rangeStart.get()))
                .filter(date -> rangeEnd.isEmpty() || !date.isAfter(rangeEnd.get()))
                .distinct()
                .sorted()
                .toList();
    }

    private void validateFilePath(String path) throws CommandException {
        if (!path.isEmpty() && isPathSeparator(path.charAt(path.length() - 1))) {
            throw new CommandException(String.format(
                    MESSAGE_INVALID_FILE_NAME, getTrailingFileName(path), path.charAt(path.length() - 1)));
        }

        String[] segments = path.split("[/\\\\]");
        for (String segment : segments) {
            if (segment.isEmpty() || isWindowsDriveSpecifier(segment)) {
                continue;
            }

            for (int i = 0; i < segment.length(); i++) {
                char currentChar = segment.charAt(i);
                if (INVALID_FILE_NAME_CHARACTERS.indexOf(currentChar) >= 0 || Character.isISOControl(currentChar)) {
                    throw new CommandException(String.format(MESSAGE_INVALID_FILE_NAME, segment, currentChar));
                }
            }
        }
    }

    private boolean isWindowsDriveSpecifier(String segment) {
        return segment.length() == 2 && Character.isLetter(segment.charAt(0)) && segment.charAt(1) == ':';
    }

    private boolean isPathSeparator(char character) {
        return character == '/' || character == '\\';
    }

    private String getTrailingFileName(String path) {
        String trimmedPath = path.substring(0, path.length() - 1);
        int lastUnixSeparator = trimmedPath.lastIndexOf('/');
        int lastWindowsSeparator = trimmedPath.lastIndexOf('\\');
        int lastSeparator = Math.max(lastUnixSeparator, lastWindowsSeparator);
        return trimmedPath.substring(lastSeparator + 1);
    }

    private String escape(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ExportViewCommand)) {
            return false;
        }
        ExportViewCommand otherCommand = (ExportViewCommand) other;
        return filePath.equals(otherCommand.filePath);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("filePath", filePath)
                .toString();
    }
}
