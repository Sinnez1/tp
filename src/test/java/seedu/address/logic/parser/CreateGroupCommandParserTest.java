package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CreateGroupCommand;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;

public class CreateGroupCommandParserTest {

    private final CreateGroupCommandParser parser = new CreateGroupCommandParser();

    @Test
    public void parse_validArgs_success() {
        assertParseSuccess(parser, " g/T01", new CreateGroupCommand(new Group(new GroupName("T01"))));
    }

    @Test
    public void parse_missingGroupPrefix_failure() {
        assertParseFailure(parser, " T01",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CreateGroupCommand.MESSAGE_USAGE));
    }
}
