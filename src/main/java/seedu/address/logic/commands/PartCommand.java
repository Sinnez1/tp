package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Participation;
import seedu.address.model.person.Person;

/**
 * Assigns a participation value to a person identified using the displayed index.
 */
public class PartCommand extends Command {

    public static final String COMMAND_WORD = "part";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Assigns participation to the person identified by the index number used in the displayed person list.\n"
            + "Parameters: INDEX PARTICIPATION\n"
            + "Participation must be an integer from 0 to 5.\n"
            + "Example: " + COMMAND_WORD + " 1 4";

    public static final String MESSAGE_PARTICIPATION_SUCCESS =
            "Updated participation for Person: %1$s";

    private final Index targetIndex;
    private final Participation participation;

    public PartCommand(Index targetIndex, Participation participation) {
        requireNonNull(targetIndex);
        requireNonNull(participation);
        this.targetIndex = targetIndex;
        this.participation = participation;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUpdate = lastShownList.get(targetIndex.getZeroBased());
        Person updatedPerson = new Person(
                personToUpdate.getName(),
                personToUpdate.getPhone(),
                personToUpdate.getEmail(),
                personToUpdate.getMatricNumber(),
                participation,
                personToUpdate.getTags(),
                personToUpdate.getClassSpaces());

        model.setPerson(personToUpdate, updatedPerson);
        return new CommandResult(String.format(MESSAGE_PARTICIPATION_SUCCESS, Messages.format(updatedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof PartCommand)) {
            return false;
        }

        PartCommand otherPartCommand = (PartCommand) other;
        return targetIndex.equals(otherPartCommand.targetIndex)
                && participation.equals(otherPartCommand.participation);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("participation", participation)
                .toString();
    }
}