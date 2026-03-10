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
 * Adds one or more students to a class space.
 */
public class AddToGroupCommand extends GroupMembershipCommand {

    public static final String COMMAND_WORD = "addtogroup";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds one or more students to a class space.\n"
            + "Parameters: g/GROUP_NAME (m/MATRIC_NUMBER [m/MATRIC_NUMBER]... | i/INDEX_EXPRESSION)\n"
            + "Examples: " + COMMAND_WORD + " g/T01 m/A1234567B m/A2345678C\n"
            + "          " + COMMAND_WORD + " g/T01 i/1,3-5";

    public static final String MESSAGE_GROUP_NOT_FOUND = "This class space does not exist.";

    private AddToGroupCommand(ClassSpaceName classSpaceName, List<Index> targetIndexes) {
        super(classSpaceName, targetIndexes);
    }

    private AddToGroupCommand(ClassSpaceName classSpaceName, List<MatricNumber> targetMatricNumbers,
                              boolean useMatricNumbers) {
        super(classSpaceName, targetMatricNumbers, useMatricNumbers);
    }

    public static AddToGroupCommand forIndexes(ClassSpaceName classSpaceName, List<Index> targetIndexes) {
        return new AddToGroupCommand(classSpaceName, targetIndexes);
    }

    public static AddToGroupCommand forMatricNumbers(ClassSpaceName classSpaceName,
                                                      List<MatricNumber> targetMatricNumbers) {
        return new AddToGroupCommand(classSpaceName, targetMatricNumbers, true);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.findClassSpaceByName(classSpaceName).isEmpty()) {
            throw new CommandException(MESSAGE_GROUP_NOT_FOUND);
        }

        List<Person> targetPersons = resolveTargetPersons(model);
        List<String> addedStudents = new ArrayList<>();
        List<String> alreadyMembers = new ArrayList<>();

        for (Person resolvedPerson : targetPersons) {
            Person person = model.findPersonByMatricNumber(resolvedPerson.getMatricNumber())
                    .orElse(resolvedPerson);

            if (person.hasClassSpace(classSpaceName)) {
                alreadyMembers.add(person.getName().fullName);
                continue;
            }

            HashSet<ClassSpaceName> updatedClassSpaces = new HashSet<>(person.getClassSpaces());
            updatedClassSpaces.add(classSpaceName);
            Person updatedPerson = new Person(person.getName(), person.getPhone(), person.getEmail(),
                    person.getMatricNumber(), person.getParticipation(), person.getTags(), updatedClassSpaces);
            model.setPerson(person, updatedPerson);
            addedStudents.add(person.getName().fullName);
        }

        return new CommandResult(buildFeedbackMessage(addedStudents, alreadyMembers));
    }

    private String buildFeedbackMessage(List<String> addedStudents, List<String> alreadyMembers) {
        List<String> feedbackParts = new ArrayList<>();
        if (!addedStudents.isEmpty()) {
            feedbackParts.add(String.format("Added %s to %s.", joinNames(addedStudents), classSpaceName.value));
        }
        if (!alreadyMembers.isEmpty()) {
            feedbackParts.add(String.format("Already in %s: %s.", classSpaceName.value, joinNames(alreadyMembers)));
        }
        if (feedbackParts.isEmpty()) {
            return String.format("No students were added to %s.", classSpaceName.value);
        }
        return String.join(" ", feedbackParts);
    }

    private String joinNames(List<String> names) {
        return names.stream().sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.joining(", "));
    }
}
