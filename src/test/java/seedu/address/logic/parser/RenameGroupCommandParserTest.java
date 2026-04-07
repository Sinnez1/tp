package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RenameGroupCommand;
import seedu.address.model.group.GroupName;

public class RenameGroupCommandParserTest {

    private final RenameGroupCommandParser parser = new RenameGroupCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        assertParseSuccess(parser, " g/T01 new/T02",
                new RenameGroupCommand(new GroupName("T01"), new GroupName("T02")));
    }

    @Test
    public void parse_missingNewName_failure() {
        assertParseFailure(parser, " g/T01",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, RenameGroupCommand.MESSAGE_USAGE));
    }
}
