package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddSessionCommand;
import seedu.address.model.group.GroupName;

public class AddSessionCommandParserTest {

    private final AddSessionCommandParser parser = new AddSessionCommandParser();

    @Test
    public void parse_validArgs_returnsAddSessionCommand() {
        assertParseSuccess(parser, " d/2026-03-16", new AddSessionCommand(LocalDate.of(2026, 3, 16)));
        assertParseSuccess(parser, " d/2026-03-16 g/T01",
                new AddSessionCommand(LocalDate.of(2026, 3, 16), new GroupName("T01")));
    }

    @Test
    public void parse_missingDate_throwsParseException() {
        assertParseFailure(parser, " g/T01",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        AddSessionCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_trailingTokensAfterDate_throwsParseException() {
        // EP: trailing tokens after date value
        assertParseFailure(parser, " d/2026-03-16 garbage",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddSessionCommand.MESSAGE_USAGE));
    }
}
