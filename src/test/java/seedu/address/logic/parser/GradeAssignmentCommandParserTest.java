package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GradeAssignmentCommand;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.person.MatricNumber;

public class GradeAssignmentCommandParserTest {

    private final GradeAssignmentCommandParser parser = new GradeAssignmentCommandParser();

    @Test
    public void parse_indexTarget_success() {
        assertParseSuccess(parser, " a/Quiz 1 i/1,3-4 gr/17",
                GradeAssignmentCommand.forIndexes(new AssignmentName("Quiz 1"),
                        List.of(Index.fromOneBased(1), Index.fromOneBased(3), Index.fromOneBased(4)), 17));
    }

    @Test
    public void parse_matricTargets_success() {
        assertParseSuccess(parser, " a/Quiz 1 m/A1234567X m/A2345678L gr/17",
                GradeAssignmentCommand.forMatricNumbers(new AssignmentName("Quiz 1"),
                        List.of(new MatricNumber("A1234567X"), new MatricNumber("A2345678L")), 17));
    }

    @Test
    public void parse_mixedTargets_failure() {
        assertParseFailure(parser, " a/Quiz 1 i/1 m/A1234567X gr/17",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, GradeAssignmentCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_decimalGrade_success() {
        assertParseSuccess(parser, " a/Quiz 1 i/1 gr/8.5",
                GradeAssignmentCommand.forIndexes(new AssignmentName("Quiz 1"),
                        List.of(Index.fromOneBased(1)), 8.5));
    }

    @Test
    public void parse_moreThanThreeDecimalPlaces_failure() {
        assertParseFailure(parser, " a/Quiz 1 i/1 gr/8.1234",
                "Grade should be a non-negative number with at most 3 decimal places.");
    }
}
