package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Person;

/**
 * Grades an assignment for one or more students in the current group.
 */
public class GradeAssignmentCommand extends ClassScopedAssignmentCommand {

    public static final String COMMAND_WORD = "gradeassignment";
    public static final String SHORT_COMMAND_WORD = "gradea";
    public static final String COMMAND_PARAMETERS =
            "a/ASSIGNMENT_NAME (i/INDEX_EXPRESSION | m/MATRIC_NUMBER [m/MATRIC_NUMBER]...) gr/GRADE";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " (alias: " + SHORT_COMMAND_WORD + ")"
            + ": Grades an assignment for one or more students in the current group.\n"
            + "Parameters: " + COMMAND_PARAMETERS + "\n"
            + "Examples:\n"
            + SHORT_COMMAND_WORD + " a/Quiz 1 i/1,3-5 gr/17\n"
            + SHORT_COMMAND_WORD + " a/Quiz 1 m/A1234567X m/A2345678Y gr/17";

    public static final String MESSAGE_GRADE_OUT_OF_RANGE =
            "Grade must be between 0 and the assignment's max marks inclusive.";

    private final AssignmentName assignmentName;
    private final List<Index> targetIndexes;
    private final List<MatricNumber> targetMatricNumbers;
    private final int grade;

    private GradeAssignmentCommand(AssignmentName assignmentName, List<Index> targetIndexes,
                                   List<MatricNumber> targetMatricNumbers, int grade) {
        requireNonNull(assignmentName);
        requireNonNull(targetIndexes);
        requireNonNull(targetMatricNumbers);
        this.assignmentName = assignmentName;
        this.targetIndexes = List.copyOf(targetIndexes);
        this.targetMatricNumbers = List.copyOf(targetMatricNumbers);
        this.grade = grade;
    }

    public static GradeAssignmentCommand forIndexes(AssignmentName assignmentName, List<Index> targetIndexes,
                                                     int grade) {
        return new GradeAssignmentCommand(assignmentName, targetIndexes, List.of(), grade);
    }

    public static GradeAssignmentCommand forMatricNumbers(AssignmentName assignmentName,
                                                           List<MatricNumber> targetMatricNumbers,
                                                           int grade) {
        return new GradeAssignmentCommand(assignmentName, List.of(), targetMatricNumbers, grade);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Group activeGroup = getActiveGroup(model);
        Assignment assignment = getRequiredAssignment(activeGroup, assignmentName);
        if (grade > assignment.getMaxMarks()) {
            throw new CommandException(MESSAGE_GRADE_OUT_OF_RANGE);
        }

        List<Person> targetPersons = resolveTargetPersons(model, activeGroup.getGroupName());
        for (Person person : targetPersons) {
            Person updatedPerson = person.withUpdatedAssignmentGrade(activeGroup.getGroupName(),
                    assignmentName, grade);
            model.setPerson(person, updatedPerson);
        }

        return new CommandResult(String.format("Graded %s for %s: %s.", assignmentName.value,
                joinNames(targetPersons.stream().map(person -> person.getName().fullName).toList()),
                grade + "/" + assignment.getMaxMarks()));
    }

    private List<Person> resolveTargetPersons(Model model, GroupName groupName)
            throws CommandException {
        Set<Person> resolvedPersons = new LinkedHashSet<>();
        if (!targetIndexes.isEmpty()) {
            List<Person> lastShownList = model.getFilteredPersonList();
            for (Index targetIndex : targetIndexes) {
                if (targetIndex.getZeroBased() >= lastShownList.size()) {
                    throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
                }
                Person targetPerson = lastShownList.get(targetIndex.getZeroBased());
                if (!targetPerson.hasGroup(groupName)) {
                    throw new CommandException(String.format("%s is not in %s.",
                            targetPerson.getName().fullName, groupName.value));
                }
                resolvedPersons.add(targetPerson);
            }
        } else {
            for (MatricNumber matricNumber : targetMatricNumbers) {
                Optional<Person> matchedPerson = model.findPersonByMatricNumber(matricNumber);
                if (matchedPerson.isEmpty()) {
                    throw new CommandException(String.format("No student with matric number %s was found.",
                            matricNumber.value));
                }
                Person targetPerson = matchedPerson.get();
                if (!targetPerson.hasGroup(groupName)) {
                    throw new CommandException(String.format("%s is not in %s.",
                            targetPerson.getName().fullName, groupName.value));
                }
                resolvedPersons.add(targetPerson);
            }
        }
        return new ArrayList<>(resolvedPersons);
    }

    private String joinNames(List<String> names) {
        return names.stream().sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.joining(", "));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof GradeAssignmentCommand)) {
            return false;
        }
        GradeAssignmentCommand otherCommand = (GradeAssignmentCommand) other;
        return assignmentName.equals(otherCommand.assignmentName)
                && targetIndexes.equals(otherCommand.targetIndexes)
                && targetMatricNumbers.equals(otherCommand.targetMatricNumbers)
                && grade == otherCommand.grade;
    }
}
