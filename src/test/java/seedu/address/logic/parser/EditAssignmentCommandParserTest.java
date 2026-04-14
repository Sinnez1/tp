package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditAssignmentCommand;
import seedu.address.logic.commands.EditAssignmentCommand.EditAssignmentDescriptor;
import seedu.address.model.assignment.AssignmentName;

public class EditAssignmentCommandParserTest {

    private final EditAssignmentCommandParser parser = new EditAssignmentCommandParser();

    @Test
    public void parse_someFieldsPresent_success() {
        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();
        descriptor.setNewAssignmentName(new AssignmentName("Quiz 1 Revised"));
        descriptor.setDueDate(LocalDate.of(2026, 4, 8));
        descriptor.setMaxMarks(25);
        assertParseSuccess(parser, " a/Quiz 1 na/Quiz 1 Revised d/2026-04-08 mm/25",
                new EditAssignmentCommand(new AssignmentName("Quiz 1"), descriptor));
    }

    @Test
    public void parse_noFields_failure() {
        assertParseFailure(parser, " a/Quiz 1", EditAssignmentCommand.MESSAGE_NOT_EDITED);
    }

    @Test
    public void parse_trailingTokensAfterDate_throwsParseException() {
        // EP: trailing tokens after d/ value
        assertParseFailure(parser, " a/Quiz 1 d/2026-04-08 garbage mm/25",
                String.format(seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                        EditAssignmentCommand.MESSAGE_USAGE));
    }
}
