package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;

/**
 * Marks a person as present using the displayed index.
 */
public class MarkCommand extends Command {

    public static final String COMMAND_WORD = "mark";
    public static final String COMMAND_PARAMETERS = "i/INDEX d/YYYY-MM-DD";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks the person identified by the index number used in the displayed person list as PRESENT.\n"
            + "Parameters: " + COMMAND_PARAMETERS + "\n"
            + "Examples:\n"
            + COMMAND_WORD + " i/1 d/2026-03-14\n"
            + COMMAND_WORD + " i/1 (after 'view d/2026-03-14')";

    public static final String MESSAGE_MARK_SUCCESS =
            "Marked Person as PRESENT: %1$s";

    public static final String MESSAGE_NO_ACTIVE_GROUP =
            "No group selected. Enter a group first or provide g/GROUP_NAME.";
    public static final String MESSAGE_REQUIRES_GROUP_VIEW =
            "Mark attendance from a group view only. Use switchgroup g/GROUP_NAME first.";
    public static final String MESSAGE_NO_ACTIVE_SESSION =
            "No session selected. Provide d/YYYY-MM-DD or run view with d/YYYY-MM-DD first.";

    public static final String MESSAGE_GROUP_NOT_FOUND =
            "This group does not exist.";

    private final Index targetIndex;
    private final Optional<LocalDate> date;
    private final Optional<GroupName> groupName;

    /**
     * Creates a MarkCommand to mark the person identified by the given {@code Index}
     * as present.
     *
     * @param targetIndex Index of the person in the filtered person list to be marked as present.
     * @param date Date of the session to mark attendance for.
     * @param groupName Group containing this session.
     */
    public MarkCommand(Index targetIndex, Optional<LocalDate> date, Optional<GroupName> groupName) {
        requireAllNonNull(targetIndex, date, groupName);
        this.targetIndex = targetIndex;
        this.date = date;
        this.groupName = groupName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.getActiveGroupName().isEmpty()) {
            throw new CommandException(MESSAGE_REQUIRES_GROUP_VIEW);
        }

        // Step 1: switch group if g/ provided
        if (groupName.isPresent()) {
            GroupName targetName = groupName.get();

            if (model.findGroupByName(targetName).isEmpty()) {
                throw new CommandException(MESSAGE_GROUP_NOT_FOUND);
            }

            model.switchToGroupView(targetName);
        }

        // Step 2: resolve active group
        Optional<GroupName> activeGroup = model.getActiveGroupName();

        if (activeGroup.isEmpty()) {
            throw new CommandException(MESSAGE_NO_ACTIVE_GROUP);
        }

        GroupName group = activeGroup.get();
        Optional<LocalDate> resolvedDate = date.isPresent() ? date : model.getActiveSessionDate();
        if (resolvedDate.isEmpty()) {
            throw new CommandException(MESSAGE_NO_ACTIVE_SESSION);
        }
        LocalDate targetDate = resolvedDate.get();
        SessionCommandHistory.record(model, COMMAND_WORD + " i/" + targetIndex.getOneBased() + " d/" + targetDate);

        // Step 3: get person
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUpdate = lastShownList.get(targetIndex.getZeroBased());

        // Step 4: get session
        Session currentSession = personToUpdate.getOrCreateSession(group, targetDate);

        // Step 5: update attendance
        Session updatedSession = new Session(
                targetDate,
                new Attendance(Attendance.Status.PRESENT),
                currentSession.getParticipation(),
                currentSession.getNote()
        );

        // Step 6: update person
        Person updatedPerson = personToUpdate.withUpdatedSession(group, updatedSession);

        // Step 7: update model
        model.setPerson(personToUpdate, updatedPerson);
        model.setActiveSessionDate(targetDate);

        return new CommandResult(
                String.format(MESSAGE_MARK_SUCCESS, Messages.format(updatedPerson, group, targetDate))
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof MarkCommand otherMarkCommand)) {
            return false;
        }

        return targetIndex.equals(otherMarkCommand.targetIndex)
                && date.equals(otherMarkCommand.date)
                && groupName.equals(otherMarkCommand.groupName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("date", date)
                .add("groupName", groupName)
                .toString();
    }
}
