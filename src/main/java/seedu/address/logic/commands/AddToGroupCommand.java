package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Person;

/**
 * Adds one or more students to a group.
 */
public class AddToGroupCommand extends GroupMembershipCommand {

    public static final String COMMAND_WORD = "addtogroup";
    public static final String COMMAND_PARAMETERS =
            "g/GROUP_NAME (m/MATRIC_NUMBER [m/MATRIC_NUMBER]... | i/INDEX_EXPRESSION)";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds one or more students to a group.\n"
            + "Parameters: " + COMMAND_PARAMETERS + "\n"
            + "Examples:\n"
            + COMMAND_WORD + " g/T01 m/A1234567B m/A2345678C\n"
            + COMMAND_WORD + " g/T01 i/1,3-5";

    public static final String MESSAGE_GROUP_NOT_FOUND = "This group does not exist.";
    private static final Logger logger = LogsCenter.getLogger(AddToGroupCommand.class);

    private AddToGroupCommand(GroupName groupName, List<Index> targetIndexes) {
        super(groupName, targetIndexes);
    }

    private AddToGroupCommand(GroupName groupName, List<MatricNumber> targetMatricNumbers,
                              boolean useMatricNumbers) {
        super(groupName, targetMatricNumbers, useMatricNumbers);
    }

    public static AddToGroupCommand forIndexes(GroupName groupName, List<Index> targetIndexes) {
        return new AddToGroupCommand(groupName, targetIndexes);
    }

    public static AddToGroupCommand forMatricNumbers(GroupName groupName,
                                                     List<MatricNumber> targetMatricNumbers) {
        return new AddToGroupCommand(groupName, targetMatricNumbers, true);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        logger.info("Attempting to add students to group: " + groupName.value);
        if (model.findGroupByName(groupName).isEmpty()) {
            logger.warning("Attempted to add students to missing group: " + groupName.value);
            throw new CommandException(MESSAGE_GROUP_NOT_FOUND);
        }

        List<Person> targetPersons = resolveTargetPersons(model);
        logger.fine("Resolved " + targetPersons.size() + " student(s) for add-to-group.");

        List<String> addedStudents = new ArrayList<>();
        List<String> alreadyMembers = new ArrayList<>();

        for (Person resolvedPerson : targetPersons) {
            Person person = model.findPersonByMatricNumber(resolvedPerson.getMatricNumber())
                    .orElse(resolvedPerson);

            if (person.hasGroup(groupName)) {
                alreadyMembers.add(person.getName().fullName);
                continue;
            }

            HashSet<GroupName> updatedGroups = new HashSet<>(person.getGroups());
            updatedGroups.add(groupName);
            Person updatedPerson = new Person(person, updatedGroups);
            model.setPerson(person, updatedPerson);
            addedStudents.add(person.getName().fullName); // TODO: Improve Law of Demeter
        }

        logger.info("Completed add-to-group for " + groupName.value
                + ". Added: " + addedStudents.size()
                + ", already members: " + alreadyMembers.size());

        return new CommandResult(buildFeedbackMessage(addedStudents, alreadyMembers));
    }

    private String buildFeedbackMessage(List<String> addedStudents, List<String> alreadyMembers) {
        List<String> feedbackParts = new ArrayList<>();
        if (!addedStudents.isEmpty()) {
            feedbackParts.add(String.format("Added %s to %s.", joinNames(addedStudents), groupName.value));
        }
        if (!alreadyMembers.isEmpty()) {
            feedbackParts.add(String.format("Already in %s: %s.", groupName.value, joinNames(alreadyMembers)));
        }
        if (feedbackParts.isEmpty()) {
            return String.format("No students were added to %s.", groupName.value);
        }
        return String.join(" ", feedbackParts);
    }

    private String joinNames(List<String> names) {
        return names.stream().sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.joining(", "));
    }
}
