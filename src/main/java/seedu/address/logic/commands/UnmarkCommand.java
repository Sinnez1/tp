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
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;

/**
 * Marks a person as absent for a specified session date in the current or specified class space.
 */
public class UnmarkCommand extends Command {

    public static final String COMMAND_WORD = "unmark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks the person identified by the index number used in the displayed person list as ABSENT.\n"
            + "Parameters: i/INDEX [d/YYYY-MM-DD] [g/CLASSSPACE]\n"
            + "Example: " + COMMAND_WORD + " i/1 d/2026-03-16 g/T02\n"
            + "         " + COMMAND_WORD + " i/1";

    public static final String MESSAGE_UNMARK_SUCCESS =
            "Marked Person as ABSENT: %1$s";

    public static final String MESSAGE_GROUP_NOT_FOUND =
            "This class space does not exist.";

    public static final String MESSAGE_NO_ACTIVE_CLASS_SPACE =
            "No class space selected. Enter a class space first or provide g/CLASSSPACE.";
    public static final String MESSAGE_NO_ACTIVE_SESSION =
            "No session selected. Provide d/YYYY-MM-DD or run attview with d/YYYY-MM-DD first.";

    private final Index targetIndex;
    private final Optional<LocalDate> date;
    private final Optional<ClassSpaceName> classSpaceName;

    /**
     * Creates an UnmarkCommand to mark the person identified by the given {@code Index}
     * as absent for a given session date and class space.
     *
     * @param targetIndex Index of the person in the filtered person list to be marked as absent.
     * @param date Date of the session to mark attendance for.
     * @param classSpaceName Class space containing this session, if explicitly provided.
     */
    public UnmarkCommand(Index targetIndex, Optional<LocalDate> date, Optional<ClassSpaceName> classSpaceName) {
        requireAllNonNull(targetIndex, date, classSpaceName);
        this.targetIndex = targetIndex;
        this.date = date;
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

        Optional<ClassSpaceName> activeClassSpace = model.getActiveClassSpaceName();

        if (activeClassSpace.isEmpty()) {
            throw new CommandException(MESSAGE_NO_ACTIVE_CLASS_SPACE);
        }

        ClassSpaceName classSpace = activeClassSpace.get();
        Optional<LocalDate> resolvedDate = date.isPresent() ? date : model.getActiveSessionDate();
        if (resolvedDate.isEmpty()) {
            throw new CommandException(MESSAGE_NO_ACTIVE_SESSION);
        }
        LocalDate targetDate = resolvedDate.get();

        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUpdate = lastShownList.get(targetIndex.getZeroBased());

        Session currentSession = personToUpdate.getOrCreateSession(classSpace, targetDate);

        Session updatedSession = new Session(
                targetDate,
                new Attendance(Attendance.Status.ABSENT),
                currentSession.getParticipation()
        );

        Person updatedPerson = personToUpdate.withUpdatedSession(classSpace, updatedSession);

        model.setPerson(personToUpdate, updatedPerson);
        model.setActiveSessionDate(targetDate);

        return new CommandResult(
                String.format(MESSAGE_UNMARK_SUCCESS, Messages.format(updatedPerson, classSpace, targetDate))
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UnmarkCommand otherUnmarkCommand)) {
            return false;
        }

        return targetIndex.equals(otherUnmarkCommand.targetIndex)
                && date.equals(otherUnmarkCommand.date)
                && classSpaceName.equals(otherUnmarkCommand.classSpaceName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("date", date)
                .add("classSpaceName", classSpaceName)
                .toString();
    }
}
