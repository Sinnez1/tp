package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Person;

/**
 * Removes one or more students from a group.
 */
// @@author ongrussell
public class RemoveFromGroupCommand extends GroupMembershipCommand {

    public static final String COMMAND_WORD = "removefromgroup";
    public static final String COMMAND_PARAMETERS =
            "g/GROUP_NAME (m/MATRIC_NUMBER [m/MATRIC_NUMBER]... | i/INDEX_EXPRESSION)";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Removes one or more students from a group.\n"
            + "Parameters: " + COMMAND_PARAMETERS + "\n"
            + "Examples:\n"
            + COMMAND_WORD + " g/T01 m/A1234567X m/A2345678L\n"
            + COMMAND_WORD + " g/T01 i/1,3-5";

    public static final String MESSAGE_GROUP_NOT_FOUND = "This group does not exist.";

    private RemoveFromGroupCommand(GroupName groupName, List<Index> targetIndexes) {
        super(groupName, targetIndexes);
    }

    private RemoveFromGroupCommand(GroupName groupName, List<MatricNumber> targetMatricNumbers,
                                   boolean useMatricNumbers) {
        super(groupName, targetMatricNumbers, useMatricNumbers);
    }

    public static RemoveFromGroupCommand forIndexes(GroupName groupName, List<Index> targetIndexes) {
        return new RemoveFromGroupCommand(groupName, targetIndexes);
    }

    public static RemoveFromGroupCommand forMatricNumbers(GroupName groupName,
                                                          List<MatricNumber> targetMatricNumbers) {
        return new RemoveFromGroupCommand(groupName, targetMatricNumbers, true);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.findGroupByName(groupName).isEmpty()) {
            throw new CommandException(MESSAGE_GROUP_NOT_FOUND);
        }

        List<Person> targetPersons = resolveTargetPersons(model);
        List<String> removedStudents = new ArrayList<>();
        List<String> notMembers = new ArrayList<>();

        for (Person person : targetPersons) {
            if (!person.hasGroup(groupName)) {
                notMembers.add(person.getName().fullName);
                continue;
            }

            Person updatedPerson = person.withoutGroupData(groupName);
            model.setPerson(person, updatedPerson);
            removedStudents.add(person.getName().fullName);
        }

        return new CommandResult(buildFeedbackMessage(removedStudents, notMembers));
    }

    private String buildFeedbackMessage(List<String> removedStudents, List<String> notMembers) {
        List<String> feedbackParts = new ArrayList<>();
        if (!removedStudents.isEmpty()) {
            feedbackParts.add(String.format("Removed %s from %s.", joinNames(removedStudents), groupName.value));
        }
        if (!notMembers.isEmpty()) {
            feedbackParts.add(String.format("Not in %s: %s.", groupName.value, joinNames(notMembers)));
        }
        if (feedbackParts.isEmpty()) {
            return String.format("No students were removed from %s.", groupName.value);
        }
        return String.join(" ", feedbackParts);
    }

    private String joinNames(List<String> names) {
        return names.stream().sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.joining(", "));
    }
}
// @@author
