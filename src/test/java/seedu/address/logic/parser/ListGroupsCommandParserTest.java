package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListGroupsCommand;

public class ListGroupsCommandParserTest {

    private final ListGroupsCommandParser parser = new ListGroupsCommandParser();

    @Test
    public void parse_blankArgs_success() throws Exception {
        assertTrue(parser.parse("   ") instanceof ListGroupsCommand);
    }

    @Test
    public void parse_nonBlankArgs_failure() {
        assertParseFailure(parser, " extra",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListGroupsCommand.MESSAGE_USAGE));
    }
}
