package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
 * Marks one or more persons as present using the displayed indexes.
 */
public class MarkCommand extends Command {

    public static final String COMMAND_WORD = "mark";
    public static final String COMMAND_PARAMETERS = "i/INDEX_EXPRESSION d/YYYY-MM-DD";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks the persons identified by the index expression in the displayed person list as PRESENT.\n"
            + "Parameters: " + COMMAND_PARAMETERS + "\n"
            + "Examples:\n"
            + COMMAND_WORD + " i/1 d/2026-03-14\n"
            + COMMAND_WORD + " i/1,2,3 d/2026-03-14\n"
            + COMMAND_WORD + " i/1-3 d/2026-03-14\n"
            + COMMAND_WORD + " i/1 (after 'view d/2026-03-14')";

    public static final String MESSAGE_MARK_SUCCESS =
            "Marked Person as PRESENT: %1$s";
    public static final String MESSAGE_MULTIPLE_MARK_SUCCESS =
            "Marked Persons as PRESENT: %1$s";

    public static final String MESSAGE_NO_ACTIVE_GROUP =
            "No group selected. Enter a group first or provide g/GROUP_NAME.";
    public static final String MESSAGE_REQUIRES_GROUP_VIEW =
            "Mark attendance from a group view only. Use switchgroup g/GROUP_NAME first.";
    public static final String MESSAGE_NO_ACTIVE_SESSION =
            "No session selected. Provide d/YYYY-MM-DD or run view with d/YYYY-MM-DD first.";

    public static final String MESSAGE_GROUP_NOT_FOUND =
            "This group does not exist.";

    private final List<Index> targetIndexes;
    private final Optional<LocalDate> date;
    private final Optional<GroupName> groupName;

    /**
     * Creates a MarkCommand to mark the persons identified by the given {@code Index} list
     * as present.
     *
     * @param targetIndexes Indexes of the persons in the filtered person list to be marked as present.
     * @param date Date of the session to mark attendance for.
     * @param groupName Group containing this session.
     */
    public MarkCommand(List<Index> targetIndexes, Optional<LocalDate> date, Optional<GroupName> groupName) {
        requireAllNonNull(targetIndexes, date, groupName);
        this.targetIndexes = new ArrayList<>(targetIndexes);
        this.date = date;
        this.groupName = groupName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        GroupName group;
        List<Person> lastShownList;

        if (groupName.isPresent()) {
            group = groupName.get();

            if (model.findGroupByName(group).isEmpty()) {
                throw new CommandException(MESSAGE_GROUP_NOT_FOUND);
            }

            lastShownList = model.getAddressBook().getPersonList().stream()
                    .filter(person -> person.hasGroup(group))
                    .collect(Collectors.toList());
        } else {
            Optional<GroupName> activeGroup = model.getActiveGroupName();

            if (activeGroup.isEmpty()) {
                throw new CommandException(MESSAGE_REQUIRES_GROUP_VIEW);
            }

            group = activeGroup.get();
            lastShownList = model.getFilteredPersonList();
        }

        Optional<LocalDate> resolvedDate = date;
        if (resolvedDate.isEmpty() && groupName.isEmpty()) {
            resolvedDate = model.getActiveSessionDate();
        }
        if (resolvedDate.isEmpty()) {
            throw new CommandException(MESSAGE_NO_ACTIVE_SESSION);
        }
        LocalDate targetDate = resolvedDate.get();

        List<Person> personsToUpdate = new ArrayList<>();
        for (Index targetIndex : targetIndexes) {
            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            personsToUpdate.add(lastShownList.get(targetIndex.getZeroBased()));
        }

        if (groupName.isPresent()) {
            model.switchToGroupView(group);
        }

        // Step 4 - 7: update each person
        List<String> updatedPersons = new ArrayList<>();
        for (Person personToUpdate : personsToUpdate) {
            Session currentSession = personToUpdate.getOrCreateSession(group, targetDate);

            Session updatedSession = new Session(
                    targetDate,
                    new Attendance(Attendance.Status.PRESENT),
                    currentSession.getParticipation(),
                    currentSession.getNote()
            );

            Person updatedPerson = personToUpdate.withUpdatedSession(group, updatedSession);
            model.setPerson(personToUpdate, updatedPerson);
            updatedPersons.add(Messages.format(updatedPerson, group, targetDate));
        }

        model.setActiveSessionDate(targetDate);

        String joinedPersons = updatedPersons.stream().collect(Collectors.joining("\n"));
        if (updatedPersons.size() == 1) {
            return new CommandResult(String.format(MESSAGE_MARK_SUCCESS, joinedPersons));
        }
        return new CommandResult(String.format(MESSAGE_MULTIPLE_MARK_SUCCESS, joinedPersons));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof MarkCommand otherMarkCommand)) {
            return false;
        }

        return targetIndexes.equals(otherMarkCommand.targetIndexes)
                && date.equals(otherMarkCommand.date)
                && groupName.equals(otherMarkCommand.groupName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndexes", targetIndexes)
                .add("date", date)
                .add("groupName", groupName)
                .toString();
    }
}
