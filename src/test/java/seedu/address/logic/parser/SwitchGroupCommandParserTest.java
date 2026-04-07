package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SwitchGroupCommand;
import seedu.address.model.group.GroupName;

public class SwitchGroupCommandParserTest {

    private final SwitchGroupCommandParser parser = new SwitchGroupCommandParser();

    @Test
    public void parse_allKeyword_success() {
        assertParseSuccess(parser, " all", new SwitchGroupCommand());
    }

    @Test
    public void parse_groupPrefix_success() {
        assertParseSuccess(parser, " g/T01", new SwitchGroupCommand(new GroupName("T01")));
    }

    @Test
    public void parse_invalidArgs_failure() {
        assertParseFailure(parser, " T01",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, SwitchGroupCommand.MESSAGE_USAGE));
    }
}
