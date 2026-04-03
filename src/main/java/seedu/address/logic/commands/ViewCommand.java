package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.time.LocalDate;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Attendance;

/**
 * Shows attendance and participation information for the current group.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";
    public static final String COMMAND_PARAMETERS =
            "[g/GROUP_NAME] [PRESENT/ABSENT/UNINITIALISED] [d/YYYY-MM-DD] [from/YYYY-MM-DD] [to/YYYY-MM-DD]";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows attendance and participation view for a specific session or the whole class overview.\n"
            + "Parameters: " + COMMAND_PARAMETERS + "\n"
            + "Examples:\n"
            + COMMAND_WORD + "\n"
            + COMMAND_WORD + " PRESENT d/2026-03-16\n"
            + COMMAND_WORD + " d/2026-03-16 g/T01\n"
            + COMMAND_WORD + " ABSENT d/2026-03-16 g/T01\n"
            + COMMAND_WORD + " from/2026-03-01 to/2026-03-31";

    public static final String MESSAGE_SUCCESS =
            "Listed %1$d students with attendance %2$s in group %3$s for session %4$s";
    public static final String MESSAGE_VIEW_SUCCESS =
            "Showing attendance and participation for %1$d students in group %2$s for session %3$s";
    public static final String MESSAGE_OVERVIEW_SUCCESS =
            "Showing attendance and participation overview for %1$d students in group %2$s";
    public static final String MESSAGE_NO_MATCHES =
            "No students with attendance %1$s were found in group %2$s for session %3$s";
    public static final String MESSAGE_GROUP_NOT_FOUND =
            "This group does not exist.";
    public static final String MESSAGE_NO_ACTIVE_GROUP =
            "No group selected. Enter a group first or provide g/GROUP_NAME.";
    public static final String MESSAGE_SESSION_NOT_CREATED =
            "Session %1$s has not been created in group %2$s yet. "
                    + "Use addsession to create it, or use mark, unmark, or part to create it implicitly.";
    public static final String MESSAGE_NO_ACTIVE_SESSION =
            "No session selected. Provide d/YYYY-MM-DD or mark attendance/participation for a session first.";

    private final Optional<Attendance> attendance;
    private final Optional<GroupName> groupName;
    private final Optional<LocalDate> sessionDate;
    private final Optional<LocalDate> rangeStartDate;
    private final Optional<LocalDate> rangeEndDate;

    public ViewCommand() {
        this(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public ViewCommand(Attendance attendance) {
        this(Optional.of(attendance), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    /**
     * Creates a view command filtered by attendance for a specific session date.
     */
    public ViewCommand(Attendance attendance, LocalDate sessionDate) {
        this(Optional.of(attendance), Optional.empty(), Optional.of(sessionDate),
                Optional.empty(), Optional.empty());
    }

    /**
     * Creates a view command filtered by attendance and group.
     */
    public ViewCommand(Attendance attendance, GroupName groupName) {
        this(Optional.of(attendance), Optional.of(groupName), Optional.empty(),
                Optional.empty(), Optional.empty());
    }

    /**
     * Creates a view command filtered by attendance, group, and session date.
     */
    public ViewCommand(Attendance attendance, GroupName groupName, LocalDate sessionDate) {
        this(Optional.of(attendance), Optional.of(groupName), Optional.of(sessionDate),
                Optional.empty(), Optional.empty());
    }

    /**
     * Creates a view command scoped to a group.
     */
    public ViewCommand(GroupName groupName) {
        this(Optional.empty(), Optional.of(groupName), Optional.empty(), Optional.empty(), Optional.empty());
    }

    /**
     * Creates a view command scoped to a group and highlighted session date.
     */
    public ViewCommand(GroupName groupName, LocalDate sessionDate) {
        this(Optional.empty(), Optional.of(groupName), Optional.of(sessionDate),
                Optional.empty(), Optional.empty());
    }

    /**
     * Creates a view command highlighted on a specific session date.
     */
    public ViewCommand(LocalDate sessionDate) {
        this(Optional.empty(), Optional.empty(), Optional.of(sessionDate), Optional.empty(), Optional.empty());
    }

    /**
     * Creates a view command with optional attendance, group, session date, and visible date range filters.
     */
    public ViewCommand(Optional<Attendance> attendance,
                       Optional<GroupName> groupName,
                       Optional<LocalDate> sessionDate,
                       Optional<LocalDate> rangeStartDate,
                       Optional<LocalDate> rangeEndDate) {
        requireNonNull(attendance);
        requireNonNull(groupName);
        requireNonNull(sessionDate);
        requireNonNull(rangeStartDate);
        requireNonNull(rangeEndDate);
        this.attendance = attendance;
        this.groupName = groupName;
        this.sessionDate = sessionDate;
        this.rangeStartDate = rangeStartDate;
        this.rangeEndDate = rangeEndDate;
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
        Optional<LocalDate> resolvedSessionDate = sessionDate.isPresent()
                ? sessionDate
                : model.getActiveSessionDate();
        model.setAttendanceViewActive(true);
        if (rangeStartDate.isPresent() || rangeEndDate.isPresent()) {
            model.setVisibleSessionRange(rangeStartDate.orElse(null), rangeEndDate.orElse(null));
        } else if (shouldResetVisibleRange()) {
            model.clearVisibleSessionRange();
        }

        if (resolvedSessionDate.isPresent()) {
            LocalDate targetSessionDate = resolvedSessionDate.get();
            if (sessionDate.isPresent() && !sessionExistsForGroup(model, targetGroup, targetSessionDate)) {
                throw new CommandException(String.format(MESSAGE_SESSION_NOT_CREATED, targetSessionDate, targetGroup));
            }
            model.setActiveSessionDate(targetSessionDate);
        }

        if (attendance.isEmpty()) {
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
            if (resolvedSessionDate.isPresent()) {
                return new CommandResult(String.format(
                        MESSAGE_VIEW_SUCCESS, model.getFilteredPersonList().size(),
                        targetGroup, resolvedSessionDate.get()));
            }
            return new CommandResult(String.format(
                    MESSAGE_OVERVIEW_SUCCESS, model.getFilteredPersonList().size(), targetGroup));
        }

        if (resolvedSessionDate.isEmpty()) {
            throw new CommandException(MESSAGE_NO_ACTIVE_SESSION);
        }

        LocalDate targetSessionDate = resolvedSessionDate.get();
        Attendance targetAttendance = attendance.get();
        model.updateFilteredPersonList(person -> person.getAttendance(targetGroup, targetSessionDate)
                .equals(targetAttendance));
        int matchCount = model.getFilteredPersonList().size();
        if (matchCount == 0) {
            return new CommandResult(String.format(
                    MESSAGE_NO_MATCHES, targetAttendance, targetGroup, targetSessionDate));
        }

        return new CommandResult(String.format(
                MESSAGE_SUCCESS, matchCount, targetAttendance, targetGroup, targetSessionDate));
    }

    private boolean sessionExistsForGroup(Model model, GroupName groupName, LocalDate date) {
        return model.getAddressBook().getPersonList().stream()
                .filter(person -> person.hasGroup(groupName))
                .map(person -> person.getGroupSessions().get(groupName))
                .filter(sessionList -> sessionList != null)
                .anyMatch(sessionList -> sessionList.getSession(date).isPresent());
    }

    private boolean shouldResetVisibleRange() {
        return attendance.isEmpty() && groupName.isEmpty() && sessionDate.isEmpty();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ViewCommand otherViewCommand)) {
            return false;
        }

        return attendance.equals(otherViewCommand.attendance)
                && groupName.equals(otherViewCommand.groupName)
                && sessionDate.equals(otherViewCommand.sessionDate)
                && rangeStartDate.equals(otherViewCommand.rangeStartDate)
                && rangeEndDate.equals(otherViewCommand.rangeEndDate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("attendance", attendance)
                .add("groupName", groupName)
                .add("sessionDate", sessionDate)
                .add("rangeStartDate", rangeStartDate)
                .add("rangeEndDate", rangeEndDate)
                .toString();
    }
}
