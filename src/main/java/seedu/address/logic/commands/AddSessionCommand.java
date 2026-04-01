package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Participation;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;

/**
 * Adds a session for a group on a specific date across all students in that group.
 */
public class AddSessionCommand extends Command {

    public static final String COMMAND_WORD = "addsession";
    public static final String COMMAND_PARAMETERS = "d/YYYY-MM-DD [g/GROUP_NAME] [n/NOTE]";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a session for a group on a specific date.\n"
            + "Parameters: " + COMMAND_PARAMETERS + "\n"
            + "Example: " + COMMAND_WORD + " d/2026-03-16 g/T01 n/tutorial";

    public static final String MESSAGE_SUCCESS =
            "Added session %1$s to group %2$s for %3$d students.";
    public static final String MESSAGE_SUCCESS_PARTIAL =
            "Added session %1$s to group %2$s for %3$d students. It already existed for %4$d students.";
    public static final String MESSAGE_GROUP_NOT_FOUND = "This group does not exist.";
    public static final String MESSAGE_NO_ACTIVE_GROUP =
            "No group selected. Enter a group first or provide g/GROUP_NAME.";
    public static final String MESSAGE_SESSION_ALREADY_EXISTS =
            "Session %1$s already exists for all students in group %2$s.";

    private final LocalDate sessionDate;
    private final Optional<GroupName> groupName;
    private final String note;

    public AddSessionCommand(LocalDate sessionDate) {
        this(sessionDate, Optional.empty(), "");
    }

    public AddSessionCommand(LocalDate sessionDate, GroupName groupName) {
        this(sessionDate, Optional.of(groupName), "");
    }

    public AddSessionCommand(LocalDate sessionDate, GroupName groupName, String note) {
        this(sessionDate, Optional.of(groupName), note);
    }

    public AddSessionCommand(LocalDate sessionDate, String note) {
        this(sessionDate, Optional.empty(), note);
    }

    private AddSessionCommand(LocalDate sessionDate, Optional<GroupName> groupName, String note) {
        requireNonNull(sessionDate);
        requireNonNull(groupName);
        requireNonNull(note);
        this.sessionDate = sessionDate;
        this.groupName = groupName;
        this.note = note.trim();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (groupName.isPresent()) {
            GroupName targetName = groupName.get();
            if (model.findGroupByName(targetName).isEmpty()) {
                throw new CommandException(MESSAGE_GROUP_NOT_FOUND);
            }
            model.switchToGroupView(targetName);
        }

        GroupName targetGroup = model.getActiveGroupName()
                .orElseThrow(() -> new CommandException(MESSAGE_NO_ACTIVE_GROUP));
        String commandDescription = COMMAND_WORD + " d/" + sessionDate
                + (note.isBlank() ? "" : " n/" + note);
        SessionCommandHistory.record(model, commandDescription);

        int createdCount = 0;
        int existingCount = 0;
        for (Person person : List.copyOf(model.getAddressBook().getPersonList())) {
            if (!person.hasGroup(targetGroup)) {
                continue;
            }
            boolean sessionExists = Optional.ofNullable(person.getGroupSessions().get(targetGroup))
                    .flatMap(sessionList -> sessionList.getSession(sessionDate))
                    .isPresent();
            if (sessionExists) {
                existingCount++;
                continue;
            }

            Session defaultSession = new Session(sessionDate,
                    new Attendance(Attendance.Status.UNINITIALISED), new Participation(0), note);
            model.setPerson(person, person.withUpdatedSession(targetGroup, defaultSession));
            createdCount++;
        }

        if (createdCount == 0) {
            throw new CommandException(String.format(MESSAGE_SESSION_ALREADY_EXISTS, sessionDate, targetGroup));
        }

        model.setActiveSessionDate(sessionDate);
        if (existingCount > 0) {
            return new CommandResult(String.format(
                    MESSAGE_SUCCESS_PARTIAL, sessionDate, targetGroup, createdCount, existingCount));
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, sessionDate, targetGroup, createdCount));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddSessionCommand)) {
            return false;
        }

        AddSessionCommand otherCommand = (AddSessionCommand) other;
        return sessionDate.equals(otherCommand.sessionDate)
                && groupName.equals(otherCommand.groupName)
                && note.equals(otherCommand.note);
    }
}
