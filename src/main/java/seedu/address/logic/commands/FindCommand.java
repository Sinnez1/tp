package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesFieldsPredicate;

/**
 * Finds and lists all people in the address book matching any of the supplied parameters.
 * Matching is case-insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";
    public static final String COMMAND_PARAMETERS =
            "[n/NAME]... "
            + "[p/PHONE]... "
            + "[e/EMAIL]... "
            + "[m/MATRIC_NUMBER]... "
            + "[t/TAG]...";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all people whose fields match any of the "
            + "specified parameters (case-insensitive) and displays them as a list with better matches first.\n"
            + "Parameters: " + COMMAND_PARAMETERS + "\n"
            + "At least one parameter must be provided.\n"
            + "Example: " + COMMAND_WORD + " "
            + "n/John Doe "
            + "p/98765432 "
            + "e/johnd@example.com "
            + "m/A1234567X "
            + "t/scholar ";

    private final PersonMatchesFieldsPredicate predicate;

    public FindCommand(PersonMatchesFieldsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setAttendanceViewActive(false);

        Comparator<Person> relevanceComparator = Comparator
                .comparingInt((Person person) -> predicate.getMatchedCriteriaCount(person))
                .thenComparingInt(person -> predicate.getExactMatchCount(person))
                .reversed()
                .thenComparing(person -> person.getName().toString(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(person -> person.getMatricNumber().toString(), String.CASE_INSENSITIVE_ORDER);

        model.updateFilteredPersonList(predicate, relevanceComparator);

        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
