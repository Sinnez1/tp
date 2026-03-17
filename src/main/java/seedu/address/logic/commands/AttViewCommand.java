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
 * Filters the current view to persons with the specified attendance status.
 */
public class AttViewCommand extends Command {

    public static final String COMMAND_WORD = "attview";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows attendance view for a specific session in the current or specified tutorial group.\n"
            + "Parameters: [STATUS] [d/YYYY-MM-DD] [g/GROUP_NAME]\n"
            + "Allowed values: PRESENT, ABSENT, UNINITIALISED\n"
            + "Examples: " + COMMAND_WORD + "\n"
            + "          " + COMMAND_WORD + " PRESENT d/2026-03-16\n"
            + "          " + COMMAND_WORD + " d/2026-03-16 g/T01\n"
            + "          " + COMMAND_WORD + " ABSENT d/2026-03-16 g/T01";

    public static final String MESSAGE_SUCCESS =
            "Listed %1$d students with attendance %2$s in class space %3$s for session %4$s";
    public static final String MESSAGE_VIEW_SUCCESS =
            "Showing attendance and participation for %1$d students in class space %2$s for session %3$s";
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

    /**
     * Creates an attendance view command for the current view without filtering by attendance status.
     */
    public AttViewCommand() {
        this(Optional.empty(), Optional.empty(), Optional.empty());
    }

    /**
     * Creates an attendance view command filtered by the specified attendance status.
     *
     * @param attendance Attendance status to filter by.
     */
    public AttViewCommand(Attendance attendance) {
        this(Optional.of(attendance), Optional.empty(), Optional.empty());
    }

    /**
     * Creates an attendance view command filtered by attendance status for the specified session date.
     */
    public AttViewCommand(Attendance attendance, LocalDate sessionDate) {
        this(Optional.of(attendance), Optional.empty(), Optional.of(sessionDate));
    }

    /**
     * Creates an attendance view command filtered by attendance status within the specified class space.
     *
     * @param attendance Attendance status to filter by.
     * @param classSpaceName Name of the class space to switch to before filtering.
     */
    public AttViewCommand(Attendance attendance, ClassSpaceName classSpaceName) {
        this(Optional.of(attendance), Optional.of(classSpaceName), Optional.empty());
    }

    /**
     * Creates an attendance view command filtered by attendance status within the specified class space
     * and session date.
     */
    public AttViewCommand(Attendance attendance, ClassSpaceName classSpaceName, LocalDate sessionDate) {
        this(Optional.of(attendance), Optional.of(classSpaceName), Optional.of(sessionDate));
    }

    /**
     * Creates an attendance view command for the specified class space without attendance filtering.
     *
     * @param classSpaceName Name of the class space to switch to.
     */
    public AttViewCommand(ClassSpaceName classSpaceName) {
        this(Optional.empty(), Optional.of(classSpaceName), Optional.empty());
    }

    /**
     * Creates an attendance view command for the specified class space and session date.
     */
    public AttViewCommand(ClassSpaceName classSpaceName, LocalDate sessionDate) {
        this(Optional.empty(), Optional.of(classSpaceName), Optional.of(sessionDate));
    }

    /**
     * Creates an attendance view command for the specified session date.
     */
    public AttViewCommand(LocalDate sessionDate) {
        this(Optional.empty(), Optional.empty(), Optional.of(sessionDate));
    }

    private AttViewCommand(Optional<Attendance> attendance,
                           Optional<ClassSpaceName> classSpaceName,
                           Optional<LocalDate> sessionDate) {
        requireNonNull(attendance);
        requireNonNull(classSpaceName);
        requireNonNull(sessionDate);
        this.attendance = attendance;
        this.classSpaceName = classSpaceName;
        this.sessionDate = sessionDate;
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
        if (resolvedSessionDate.isEmpty()) {
            throw new CommandException(MESSAGE_NO_ACTIVE_SESSION);
        }
        LocalDate targetSessionDate = resolvedSessionDate.get();

        model.setActiveSessionDate(targetSessionDate);
        model.setAttendanceViewActive(true);
        initializeMissingSessionsForCurrentView(model, targetClassSpace, targetSessionDate);

        if (attendance.isEmpty()) {
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
            return new CommandResult(String.format(
                    MESSAGE_VIEW_SUCCESS, model.getFilteredPersonList().size(), targetClassSpace, targetSessionDate));
        }

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
                        new Participation(0));
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

        if (!(other instanceof AttViewCommand otherAttViewCommand)) {
            return false;
        }

        return attendance.equals(otherAttViewCommand.attendance)
                && classSpaceName.equals(otherAttViewCommand.classSpaceName)
                && sessionDate.equals(otherAttViewCommand.sessionDate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("attendance", attendance)
                .add("classSpaceName", classSpaceName)
                .add("sessionDate", sessionDate)
                .toString();
    }
}
