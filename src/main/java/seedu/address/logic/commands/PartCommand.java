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
import seedu.address.model.person.Participation;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;

/**
 * Assigns a participation value to a person identified using the displayed index
 * for a specified session date in the current or specified class space.
 */
public class PartCommand extends Command {

    public static final String COMMAND_WORD = "part";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Assigns participation to the person identified by the index number in the displayed person list.\n"
            + "Parameters: i/INDEX [d/YYYY-MM-DD] [g/CLASSSPACE] pv/PARTICIPATION_VALUE\n"
            + "Participation must be an integer from 0 to 5.\n"
            + "Example: " + COMMAND_WORD + " i/1 d/2026-03-16 g/T02 pv/4\n"
            + "         " + COMMAND_WORD + " i/1 pv/4";

    public static final String MESSAGE_PARTICIPATION_SUCCESS =
            "Updated participation for Person: %1$s";

    public static final String MESSAGE_CLASS_SPACE_NOT_FOUND =
            "This class space does not exist.";

    public static final String MESSAGE_NO_ACTIVE_CLASS_SPACE =
            "No class space selected. Enter a class space first or provide g/CLASS_SPACE.";
    public static final String MESSAGE_NO_ACTIVE_SESSION =
            "No session selected. Provide d/YYYY-MM-DD or run attview with d/YYYY-MM-DD first.";

    private final Index targetIndex;
    private final Optional<LocalDate> date;
    private final Optional<ClassSpaceName> classSpaceName;
    private final Participation participation;

    /**
     * Creates a PartCommand to assign the specified {@code Participation}
     * value to the person identified by the given {@code Index}, for the
     * specified session date and current or specified class space.
     *
     * @param targetIndex Index of the person in the filtered person list whose participation
     *                    level is to be updated.
     * @param date Date of the session to update participation for.
     * @param classSpaceName Class space containing this session, if explicitly provided.
     * @param participation Participation value to assign to the specified person.
     */
    public PartCommand(Index targetIndex, Optional<LocalDate> date, Optional<ClassSpaceName> classSpaceName,
                       Participation participation) {
        requireAllNonNull(targetIndex, date, classSpaceName, participation);
        this.targetIndex = targetIndex;
        this.date = date;
        this.classSpaceName = classSpaceName;
        this.participation = participation;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        // Step 1: switch class space if g/ provided
        if (classSpaceName.isPresent()) {
            ClassSpaceName targetName = classSpaceName.get();

            if (model.findClassSpaceByName(targetName).isEmpty()) {
                throw new CommandException(MESSAGE_CLASS_SPACE_NOT_FOUND);
            }

            model.switchToClassSpaceView(targetName);
        }

        // Step 2: resolve active class space
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

        // Step 3: get person
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUpdate = lastShownList.get(targetIndex.getZeroBased());

        // Step 4: get session
        Session currentSession = personToUpdate.getOrCreateSession(classSpace, targetDate);

        // Step 5: update participation
        Session updatedSession = new Session(
                targetDate,
                currentSession.getAttendance(),
                participation
        );

        // Step 6: update person
        Person updatedPerson = personToUpdate.withUpdatedSession(classSpace, updatedSession);

        // Step 7: update model
        model.setPerson(personToUpdate, updatedPerson);
        model.setActiveSessionDate(targetDate);

        return new CommandResult(String.format(
                MESSAGE_PARTICIPATION_SUCCESS,
                Messages.format(updatedPerson, classSpace, targetDate))
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof PartCommand otherPartCommand)) {
            return false;
        }

        return targetIndex.equals(otherPartCommand.targetIndex)
                && date.equals(otherPartCommand.date)
                && classSpaceName.equals(otherPartCommand.classSpaceName)
                && participation.equals(otherPartCommand.participation);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("date", date)
                .add("classSpaceName", classSpaceName)
                .add("participation", participation)
                .toString();
    }
}
