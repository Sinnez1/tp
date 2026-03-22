package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;

/**
 * Moves a session date for a class space across all students in that class space.
 */
public class EditSessionCommand extends Command {

    public static final String COMMAND_WORD = "editsession";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Changes a session date or note for a class space.\n"
            + "Parameters: d/OLD_DATE [nd/NEW_DATE] [nn/NEW_NOTE] [g/GROUP_NAME]\n"
            + "At least one of nd/ or nn/ must be provided.\n"
            + "Example: " + COMMAND_WORD + " d/2026-03-16 nd/2026-03-23 nn/tutorial g/T01";

    public static final String MESSAGE_SUCCESS = "Updated session %1$s in class space %2$s.";
    public static final String MESSAGE_GROUP_NOT_FOUND = "This class space does not exist.";
    public static final String MESSAGE_NO_ACTIVE_CLASS_SPACE =
            "No class space selected. Enter a class space first or provide g/GROUP_NAME.";
    public static final String MESSAGE_SESSION_NOT_FOUND =
            "No session on %1$s was found in class space %2$s.";
    public static final String MESSAGE_TARGET_SESSION_ALREADY_EXISTS =
            "Cannot move session to %1$s because that date already exists in class space %2$s.";

    private final LocalDate originalDate;
    private final Optional<LocalDate> newDate;
    private final Optional<String> newNote;
    private final Optional<ClassSpaceName> classSpaceName;

    public EditSessionCommand(LocalDate originalDate, LocalDate newDate) {
        this(originalDate, Optional.of(newDate), Optional.empty(), Optional.empty());
    }

    public EditSessionCommand(LocalDate originalDate, LocalDate newDate, ClassSpaceName classSpaceName) {
        this(originalDate, Optional.of(newDate), Optional.empty(), Optional.of(classSpaceName));
    }

    /**
     * Creates an edit-session command that can update the date, the note, or both.
     */
    public EditSessionCommand(LocalDate originalDate, Optional<LocalDate> newDate,
                              Optional<String> newNote, Optional<ClassSpaceName> classSpaceName) {
        requireNonNull(originalDate);
        requireNonNull(newDate);
        requireNonNull(newNote);
        requireNonNull(classSpaceName);
        this.originalDate = originalDate;
        this.newDate = newDate;
        this.newNote = newNote.map(String::trim);
        this.classSpaceName = classSpaceName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (classSpaceName.isPresent()) {
            ClassSpaceName targetName = classSpaceName.get();
            if (model.findClassSpaceByName(targetName).isEmpty()) {
                throw new CommandException(MESSAGE_GROUP_NOT_FOUND);
            }
            model.switchToClassSpaceView(targetName);
        }

        ClassSpaceName targetClassSpace = model.getActiveClassSpaceName()
                .orElseThrow(() -> new CommandException(MESSAGE_NO_ACTIVE_CLASS_SPACE));
        LocalDate targetDate = newDate.orElse(originalDate);
        String commandDescription = COMMAND_WORD + " d/" + originalDate
                + newDate.map(date -> " nd/" + date).orElse("")
                + newNote.filter(note -> !note.isBlank()).map(note -> " nn/" + note).orElse("");
        SessionCommandHistory.record(model, commandDescription);

        boolean foundOriginal = false;
        for (Person person : List.copyOf(model.getAddressBook().getPersonList())) {
            if (!person.hasClassSpace(targetClassSpace)) {
                continue;
            }
            boolean hasOriginal = Optional.ofNullable(person.getClassSpaceSessions().get(targetClassSpace))
                    .flatMap(sessionList -> sessionList.getSession(originalDate))
                    .isPresent();
            boolean hasTarget = !targetDate.equals(originalDate)
                    && Optional.ofNullable(person.getClassSpaceSessions().get(targetClassSpace))
                    .flatMap(sessionList -> sessionList.getSession(targetDate))
                    .isPresent();
            if (hasOriginal) {
                foundOriginal = true;
            }
            if (hasOriginal && hasTarget) {
                throw new CommandException(String.format(
                        MESSAGE_TARGET_SESSION_ALREADY_EXISTS, targetDate, targetClassSpace));
            }
        }

        if (!foundOriginal) {
            throw new CommandException(String.format(MESSAGE_SESSION_NOT_FOUND, originalDate, targetClassSpace));
        }

        for (Person person : List.copyOf(model.getAddressBook().getPersonList())) {
            if (!person.hasClassSpace(targetClassSpace)) {
                continue;
            }
            Optional<Session> originalSession = Optional.ofNullable(
                    person.getClassSpaceSessions().get(targetClassSpace))
                    .flatMap(sessionList -> sessionList.getSession(originalDate));
            if (originalSession.isEmpty()) {
                continue;
            }

            Session updatedSession = new Session(
                    targetDate,
                    originalSession.get().getAttendance(),
                    originalSession.get().getParticipation(),
                    newNote.orElse(originalSession.get().getNote()));
            Person updatedPerson = person.withoutSession(targetClassSpace, originalDate)
                    .withUpdatedSession(targetClassSpace, updatedSession);
            model.setPerson(person, updatedPerson);
        }

        if (model.getActiveSessionDate().filter(originalDate::equals).isPresent()
                && !targetDate.equals(originalDate)) {
            model.setActiveSessionDate(targetDate);
        }

        String sessionDescription = targetDate.equals(originalDate)
                ? originalDate.toString()
                : originalDate + " -> " + targetDate;
        if (newNote.isPresent()) {
            String noteDescription = newNote.get().isBlank()
                    ? "cleared note"
                    : "note \"" + newNote.get() + "\"";
            sessionDescription = sessionDescription + " (" + noteDescription + ")";
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, sessionDescription, targetClassSpace));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof EditSessionCommand)) {
            return false;
        }

        EditSessionCommand otherCommand = (EditSessionCommand) other;
        return originalDate.equals(otherCommand.originalDate)
                && newDate.equals(otherCommand.newDate)
                && newNote.equals(otherCommand.newNote)
                && classSpaceName.equals(otherCommand.classSpaceName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("originalDate", originalDate)
                .add("newDate", newDate)
                .add("newNote", newNote)
                .add("classSpaceName", classSpaceName)
                .toString();
    }
}
