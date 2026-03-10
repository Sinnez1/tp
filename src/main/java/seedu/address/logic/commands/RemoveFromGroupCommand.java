package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Person;

/**
 * Removes one or more students from a class space.
 */
public class RemoveFromGroupCommand extends GroupMembershipCommand {

    public static final String COMMAND_WORD = "removefromgroup";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Removes one or more students from a class space.\n"
            + "Parameters: g/GROUP_NAME (m/MATRIC_NUMBER [m/MATRIC_NUMBER]... | i/INDEX_EXPRESSION)\n"
            + "Examples: " + COMMAND_WORD + " g/T01 m/A1234567B m/A2345678C\n"
            + "          " + COMMAND_WORD + " g/T01 i/1,3-5";

    public static final String MESSAGE_GROUP_NOT_FOUND = "This class space does not exist.";

    private RemoveFromGroupCommand(ClassSpaceName classSpaceName, List<Index> targetIndexes) {
        super(classSpaceName, targetIndexes);
    }

    private RemoveFromGroupCommand(ClassSpaceName classSpaceName, List<MatricNumber> targetMatricNumbers,
                                   boolean useMatricNumbers) {
        super(classSpaceName, targetMatricNumbers, useMatricNumbers);
    }

    public static RemoveFromGroupCommand forIndexes(ClassSpaceName classSpaceName, List<Index> targetIndexes) {
        return new RemoveFromGroupCommand(classSpaceName, targetIndexes);
    }

    public static RemoveFromGroupCommand forMatricNumbers(ClassSpaceName classSpaceName,
                                                           List<MatricNumber> targetMatricNumbers) {
        return new RemoveFromGroupCommand(classSpaceName, targetMatricNumbers, true);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.findClassSpaceByName(classSpaceName).isEmpty()) {
            throw new CommandException(MESSAGE_GROUP_NOT_FOUND);
        }

        List<Person> targetPersons = resolveTargetPersons(model);
        List<String> removedStudents = new ArrayList<>();
        List<String> notMembers = new ArrayList<>();

        for (Person person : targetPersons) {
            if (!person.hasClassSpace(classSpaceName)) {
                notMembers.add(person.getName().fullName);
                continue;
            }

            HashSet<ClassSpaceName> updatedClassSpaces = new HashSet<>(person.getClassSpaces());
            updatedClassSpaces.remove(classSpaceName);
            Person updatedPerson = new Person(person.getName(), person.getPhone(), person.getEmail(),
                    person.getMatricNumber(), person.getParticipation(), person.getTags(), updatedClassSpaces);
            model.setPerson(person, updatedPerson);
            removedStudents.add(person.getName().fullName);
        }

        return new CommandResult(buildFeedbackMessage(removedStudents, notMembers));
    }

    private String buildFeedbackMessage(List<String> removedStudents, List<String> notMembers) {
        List<String> feedbackParts = new ArrayList<>();
        if (!removedStudents.isEmpty()) {
            feedbackParts.add(String.format("Removed %s from %s.", joinNames(removedStudents), classSpaceName.value));
        }
        if (!notMembers.isEmpty()) {
            feedbackParts.add(String.format("Not in %s: %s.", classSpaceName.value, joinNames(notMembers)));
        }
        if (feedbackParts.isEmpty()) {
            return String.format("No students were removed from %s.", classSpaceName.value);
        }
        return String.join(" ", feedbackParts);
    }

    private String joinNames(List<String> names) {
        return names.stream().sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.joining(", "));
    }
}
