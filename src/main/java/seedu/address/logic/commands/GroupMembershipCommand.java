package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Person;

/**
 * Shared logic for commands that target students for group membership updates.
 */
// @@author ongrussell
abstract class GroupMembershipCommand extends Command {

    protected final GroupName groupName;
    protected final List<Index> targetIndexes;
    protected final List<MatricNumber> targetMatricNumbers;

    GroupMembershipCommand(GroupName groupName, List<Index> targetIndexes) {
        requireNonNull(groupName);
        requireNonNull(targetIndexes);
        this.groupName = groupName;
        this.targetIndexes = List.copyOf(targetIndexes);
        this.targetMatricNumbers = List.of();
    }

    GroupMembershipCommand(GroupName groupName, List<MatricNumber> targetMatricNumbers,
                           boolean ignored) {
        requireNonNull(groupName);
        requireNonNull(targetMatricNumbers);
        this.groupName = groupName;
        this.targetIndexes = List.of();
        this.targetMatricNumbers = List.copyOf(targetMatricNumbers);
    }

    protected List<Person> resolveTargetPersons(Model model) throws CommandException {
        Set<Person> resolvedPersons = new LinkedHashSet<>();
        if (!targetIndexes.isEmpty()) {
            List<Person> lastShownList = model.getFilteredPersonList();
            for (Index targetIndex : targetIndexes) {
                if (targetIndex.getZeroBased() >= lastShownList.size()) {
                    throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
                }
                resolvedPersons.add(lastShownList.get(targetIndex.getZeroBased()));
            }
        } else {
            for (MatricNumber matricNumber : targetMatricNumbers) {
                Optional<Person> matchedPerson = model.findPersonByMatricNumber(matricNumber);
                if (matchedPerson.isEmpty()) {
                    throw new CommandException(String.format("No student with matric number %s was found.",
                            matricNumber.value));
                }
                resolvedPersons.add(matchedPerson.get());
            }
        }
        return new ArrayList<>(resolvedPersons);
    }
}
// @@author
