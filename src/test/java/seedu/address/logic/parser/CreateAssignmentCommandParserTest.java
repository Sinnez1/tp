package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CreateAssignmentCommand;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;

public class CreateAssignmentCommandParserTest {

    private final CreateAssignmentCommandParser parser = new CreateAssignmentCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        assertParseSuccess(parser, " a/Quiz 1 d/2026-04-05 mm/20",
                new CreateAssignmentCommand(new AssignmentName("Quiz 1"), LocalDate.of(2026, 4, 5), 20));
    }

    @Test
    public void parse_missingAssignmentPrefix_failure() {
        assertParseFailure(parser, " Quiz 1 d/2026-04-05 mm/20",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CreateAssignmentCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_maxMarksTooLarge_failure() {
        assertParseFailure(parser, " a/Quiz 1 d/2026-04-05 mm/1000000000000000",
                Assignment.MESSAGE_MAX_MARKS_CONSTRAINTS);
    }
}
