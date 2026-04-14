package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;

/**
 * Moves a session date for a group across all students in that group.
 */
public class EditSessionCommand extends Command {

    public static final String COMMAND_WORD = "editsession";
    public static final String COMMAND_PARAMETERS =
            "d/OLD_DATE (at least one of: nd/NEW_DATE, nn/NEW_NOTE) [g/GROUP_NAME]";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Changes a session date or note for a group.\n"
            + "Parameters: " + COMMAND_PARAMETERS + "\n"
            + "Example: " + COMMAND_WORD + " d/2026-03-16 nd/2026-03-23 nn/tutorial g/T01";

    public static final String MESSAGE_SUCCESS = "Updated session %1$s in group %2$s.";
    public static final String MESSAGE_GROUP_NOT_FOUND = "This group does not exist.";
    public static final String MESSAGE_NO_ACTIVE_GROUP =
            "No group selected. Enter a group first or provide g/GROUP_NAME.";
    public static final String MESSAGE_SESSION_NOT_FOUND =
            "No session on %1$s was found in group %2$s.";
    public static final String MESSAGE_TARGET_SESSION_ALREADY_EXISTS =
            "Cannot move session to %1$s because that date already exists in group %2$s.";

    private final LocalDate originalDate;
    private final Optional<LocalDate> newDate;
    private final Optional<String> newNote;
    private final Optional<GroupName> groupName;

    public EditSessionCommand(LocalDate originalDate, LocalDate newDate) {
        this(originalDate, Optional.of(newDate), Optional.empty(), Optional.empty());
    }

    public EditSessionCommand(LocalDate originalDate, LocalDate newDate, GroupName groupName) {
        this(originalDate, Optional.of(newDate), Optional.empty(), Optional.of(groupName));
    }

    /**
     * Creates an edit-session command that can update the date, the note, or both.
     */
    public EditSessionCommand(LocalDate originalDate, Optional<LocalDate> newDate,
                              Optional<String> newNote, Optional<GroupName> groupName) {
        requireNonNull(originalDate);
        requireNonNull(newDate);
        requireNonNull(newNote);
        requireNonNull(groupName);
        this.originalDate = originalDate;
        this.newDate = newDate;
        this.newNote = newNote.map(String::trim);
        this.groupName = groupName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Optional<GroupName> originalActiveGroup = model.getActiveGroupName();
        if (groupName.isPresent()) {
            GroupName targetName = groupName.get();
            if (model.findGroupByName(targetName).isEmpty()) {
                throw new CommandException(MESSAGE_GROUP_NOT_FOUND);
            }
        }

        GroupName targetGroup = groupName.or(() -> originalActiveGroup)
                .orElseThrow(() -> new CommandException(MESSAGE_NO_ACTIVE_GROUP));
        Group group = model.findGroupByName(targetGroup)
                .orElseThrow(() -> new CommandException(MESSAGE_GROUP_NOT_FOUND));
        LocalDate targetDate = newDate.orElse(originalDate);
        Optional<Session> originalGroupSession = group.getSession(originalDate);
        boolean foundOriginal = originalGroupSession.isPresent();
        if (!targetDate.equals(originalDate) && group.getSession(targetDate).isPresent()) {
            throw new CommandException(String.format(
                    MESSAGE_TARGET_SESSION_ALREADY_EXISTS, targetDate, targetGroup));
        }
        for (Person person : List.copyOf(model.getAddressBook().getPersonList())) {
            if (!person.hasGroup(targetGroup)) {
                continue;
            }
            boolean hasOriginal = Optional.ofNullable(person.getGroupSessions().get(targetGroup))
                    .flatMap(sessionList -> sessionList.getSession(originalDate))
                    .isPresent();
            boolean hasTarget = !targetDate.equals(originalDate)
                    && Optional.ofNullable(person.getGroupSessions().get(targetGroup))
                    .flatMap(sessionList -> sessionList.getSession(targetDate))
                    .isPresent();
            if (hasOriginal) {
                foundOriginal = true;
            }
            if (hasOriginal && hasTarget) {
                throw new CommandException(String.format(
                        MESSAGE_TARGET_SESSION_ALREADY_EXISTS, targetDate, targetGroup));
            }
        }

        if (!foundOriginal) {
            throw new CommandException(String.format(MESSAGE_SESSION_NOT_FOUND, originalDate, targetGroup));
        }

        Optional<Session> fallbackPersonSession = List.copyOf(model.getAddressBook().getPersonList())
                .stream()
                .filter(person -> person.hasGroup(targetGroup))
                .map(person -> Optional.ofNullable(person.getGroupSessions().get(targetGroup))
                        .flatMap(sessionList -> sessionList.getSession(originalDate)))
                .flatMap(Optional::stream)
                .findFirst();
        Session sourceGroupSession;
        if (originalGroupSession.isPresent()) {
            sourceGroupSession = originalGroupSession.get();
        } else {
            sourceGroupSession = fallbackPersonSession
                    .orElseThrow(() -> new CommandException(
                            String.format(MESSAGE_SESSION_NOT_FOUND, originalDate, targetGroup)));
        }
        Session updatedGroupSession = new Session(
                targetDate,
                sourceGroupSession.getAttendance(),
                sourceGroupSession.getParticipation(),
                newNote.orElse(sourceGroupSession.getNote()));

        if (sourceGroupSession.equals(updatedGroupSession)) {
            throw new CommandException(MESSAGE_NOTHING_CHANGED);
        }

        model.setGroup(group, group.withoutSession(originalDate).withUpdatedSession(updatedGroupSession));

        for (Person person : List.copyOf(model.getAddressBook().getPersonList())) {
            if (!person.hasGroup(targetGroup)) {
                continue;
            }
            Optional<Session> originalSession = Optional.ofNullable(
                    person.getGroupSessions().get(targetGroup))
                    .flatMap(sessionList -> sessionList.getSession(originalDate));
            if (originalSession.isEmpty()) {
                continue;
            }

            Session updatedSession = new Session(
                    targetDate,
                    originalSession.get().getAttendance(),
                    originalSession.get().getParticipation(),
                    newNote.orElse(originalSession.get().getNote()));
            Person updatedPerson = person.withoutSession(targetGroup, originalDate)
                    .withUpdatedSession(targetGroup, updatedSession);
            model.setPerson(person, updatedPerson);
        }

        if (originalActiveGroup.filter(targetGroup::equals).isPresent()
                && model.getActiveSessionDate().filter(originalDate::equals).isPresent()
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

        return new CommandResult(String.format(MESSAGE_SUCCESS, sessionDescription, targetGroup));
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
                && groupName.equals(otherCommand.groupName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("originalDate", originalDate)
                .add("newDate", newDate)
                .add("newNote", newNote)
                .add("groupName", groupName)
                .toString();
    }
}
