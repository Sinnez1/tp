package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GRADE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEXES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRIC_NUMBER;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GradeAssignmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.person.MatricNumber;

/**
 * Parses input arguments and creates a new GradeAssignmentCommand object.
 */
public class GradeAssignmentCommandParser implements Parser<GradeAssignmentCommand> {

    @Override
    public GradeAssignmentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ASSIGNMENT, PREFIX_INDEXES,
                PREFIX_MATRIC_NUMBER, PREFIX_GRADE);

        if (!arePrefixesPresent(argMultimap, PREFIX_ASSIGNMENT, PREFIX_GRADE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    GradeAssignmentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ASSIGNMENT, PREFIX_INDEXES, PREFIX_GRADE);

        AssignmentName assignmentName = ParserUtil.parseAssignmentName(argMultimap.getValue(PREFIX_ASSIGNMENT).get());
        double grade = ParserUtil.parseGrade(argMultimap.getValue(PREFIX_GRADE).get());

        boolean hasIndexTargets = argMultimap.getValue(PREFIX_INDEXES).isPresent();
        boolean hasMatricTargets = !argMultimap.getAllValues(PREFIX_MATRIC_NUMBER).isEmpty();
        if (hasIndexTargets == hasMatricTargets) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    GradeAssignmentCommand.MESSAGE_USAGE));
        }

        if (hasIndexTargets) {
            List<Index> indexes = ParserUtil.parseIndexes(argMultimap.getValue(PREFIX_INDEXES).get());
            return GradeAssignmentCommand.forIndexes(assignmentName, indexes, grade);
        }

        List<MatricNumber> matricNumbers = new ArrayList<>();
        for (String value : argMultimap.getAllValues(PREFIX_MATRIC_NUMBER)) {
            matricNumbers.add(ParserUtil.parseMatricNumber(value));
        }
        return GradeAssignmentCommand.forMatricNumbers(assignmentName, matricNumbers, grade);
    }

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
