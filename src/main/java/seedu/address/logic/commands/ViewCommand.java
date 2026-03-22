package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Participation;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;

/**
 * Shows attendance and participation information for the current class space.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows attendance and participation view for a specific session or the whole class overview.\n"
            + "Parameters: [STATUS] [d/YYYY-MM-DD] [g/GROUP_NAME] [from/YYYY-MM-DD] [to/YYYY-MM-DD]\n"
            + "Allowed values: PRESENT, ABSENT, UNINITIALISED\n"
            + "Examples: " + COMMAND_WORD + "\n"
            + "          " + COMMAND_WORD + " PRESENT d/2026-03-16\n"
            + "          " + COMMAND_WORD + " d/2026-03-16 g/T01\n"
            + "          " + COMMAND_WORD + " ABSENT d/2026-03-16 g/T01\n"
            + "          " + COMMAND_WORD + " from/2026-03-01 to/2026-03-31";

    public static final String MESSAGE_SUCCESS =
            "Listed %1$d students with attendance %2$s in class space %3$s for session %4$s";
    public static final String MESSAGE_VIEW_SUCCESS =
            "Showing attendance and participation for %1$d students in class space %2$s for session %3$s";
    public static final String MESSAGE_OVERVIEW_SUCCESS =
            "Showing attendance and participation overview for %1$d students in class space %2$s";
    public static final String MESSAGE_NO_MATCHES =
            "No students with attendance %1$s were found in class space %2$s for session %3$s";
    public static final String MESSAGE_GROUP_NOT_FOUND =
            "This class space does not exist.";
    public static final String MESSAGE_NO_ACTIVE_CLASS_SPACE =
            "No class space selected. Enter a class space first or provide g/GROUP_NAME.";
    public static final String MESSAGE_NO_ACTIVE_SESSION =
            "No session selected. Provide d/YYYY-MM-DD or mark attendance/participation for a session first.";

    private final Optional<Attendance> attendance;
    private final Optional<ClassSpaceName> classSpaceName;
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
     * Creates a view command filtered by attendance and class space.
     */
    public ViewCommand(Attendance attendance, ClassSpaceName classSpaceName) {
        this(Optional.of(attendance), Optional.of(classSpaceName), Optional.empty(),
                Optional.empty(), Optional.empty());
    }

    /**
     * Creates a view command filtered by attendance, class space, and session date.
     */
    public ViewCommand(Attendance attendance, ClassSpaceName classSpaceName, LocalDate sessionDate) {
        this(Optional.of(attendance), Optional.of(classSpaceName), Optional.of(sessionDate),
                Optional.empty(), Optional.empty());
    }

    /**
     * Creates a view command scoped to a class space.
     */
    public ViewCommand(ClassSpaceName classSpaceName) {
        this(Optional.empty(), Optional.of(classSpaceName), Optional.empty(), Optional.empty(), Optional.empty());
    }

    /**
     * Creates a view command scoped to a class space and highlighted session date.
     */
    public ViewCommand(ClassSpaceName classSpaceName, LocalDate sessionDate) {
        this(Optional.empty(), Optional.of(classSpaceName), Optional.of(sessionDate),
                Optional.empty(), Optional.empty());
    }

    /**
     * Creates a view command highlighted on a specific session date.
     */
    public ViewCommand(LocalDate sessionDate) {
        this(Optional.empty(), Optional.empty(), Optional.of(sessionDate), Optional.empty(), Optional.empty());
    }

    public ViewCommand(Optional<Attendance> attendance,
                       Optional<ClassSpaceName> classSpaceName,
                       Optional<LocalDate> sessionDate,
                       Optional<LocalDate> rangeStartDate,
                       Optional<LocalDate> rangeEndDate) {
        requireNonNull(attendance);
        requireNonNull(classSpaceName);
        requireNonNull(sessionDate);
        requireNonNull(rangeStartDate);
        requireNonNull(rangeEndDate);
        this.attendance = attendance;
        this.classSpaceName = classSpaceName;
        this.sessionDate = sessionDate;
        this.rangeStartDate = rangeStartDate;
        this.rangeEndDate = rangeEndDate;
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
        Optional<LocalDate> resolvedSessionDate = sessionDate.isPresent()
                ? sessionDate
                : model.getActiveSessionDate();
        model.setAttendanceViewActive(true);
        if (rangeStartDate.isPresent() || rangeEndDate.isPresent()) {
            model.setVisibleSessionRange(rangeStartDate.orElse(null), rangeEndDate.orElse(null));
        } else {
            model.clearVisibleSessionRange();
        }

        if (resolvedSessionDate.isPresent()) {
            LocalDate targetSessionDate = resolvedSessionDate.get();
            model.setActiveSessionDate(targetSessionDate);
            initializeMissingSessionsForCurrentView(model, targetClassSpace, targetSessionDate);
        }

        if (attendance.isEmpty()) {
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
            if (resolvedSessionDate.isPresent()) {
                return new CommandResult(String.format(
                        MESSAGE_VIEW_SUCCESS, model.getFilteredPersonList().size(),
                        targetClassSpace, resolvedSessionDate.get()));
            }
            return new CommandResult(String.format(
                    MESSAGE_OVERVIEW_SUCCESS, model.getFilteredPersonList().size(), targetClassSpace));
        }

        if (resolvedSessionDate.isEmpty()) {
            throw new CommandException(MESSAGE_NO_ACTIVE_SESSION);
        }

        LocalDate targetSessionDate = resolvedSessionDate.get();
        Attendance targetAttendance = attendance.get();
        model.updateFilteredPersonList(person -> person.getAttendance(targetClassSpace, targetSessionDate)
                .equals(targetAttendance));
        int matchCount = model.getFilteredPersonList().size();
        if (matchCount == 0) {
            return new CommandResult(String.format(
                    MESSAGE_NO_MATCHES, targetAttendance, targetClassSpace, targetSessionDate));
        }

        return new CommandResult(String.format(
                MESSAGE_SUCCESS, matchCount, targetAttendance, targetClassSpace, targetSessionDate));
    }

    private void initializeMissingSessionsForCurrentView(Model model, ClassSpaceName classSpaceName, LocalDate date) {
        List<Person> personsInCurrentView = new ArrayList<>(model.getFilteredPersonList());
        for (Person person : personsInCurrentView) {
            boolean sessionExists = Optional.ofNullable(person.getClassSpaceSessions().get(classSpaceName))
                    .flatMap(sessionList -> sessionList.getSession(date))
                    .isPresent();
            if (!sessionExists) {
                Session defaultSession = new Session(date, new Attendance(Attendance.Status.UNINITIALISED),
                        new Participation(0), "");
                Person updatedPerson = person.withUpdatedSession(classSpaceName, defaultSession);
                model.setPerson(person, updatedPerson);
            }
        }
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
                && classSpaceName.equals(otherViewCommand.classSpaceName)
                && sessionDate.equals(otherViewCommand.sessionDate)
                && rangeStartDate.equals(otherViewCommand.rangeStartDate)
                && rangeEndDate.equals(otherViewCommand.rangeEndDate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("attendance", attendance)
                .add("classSpaceName", classSpaceName)
                .add("sessionDate", sessionDate)
                .add("rangeStartDate", rangeStartDate)
                .add("rangeEndDate", rangeEndDate)
                .toString();
    }
}
