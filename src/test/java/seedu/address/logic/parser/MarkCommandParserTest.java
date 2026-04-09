package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Session;

public class MarkCommandParserTest {

    private final MarkCommandParser parser = new MarkCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        assertParseSuccess(parser, " i/1 d/2026-03-16 g/T01",
                new MarkCommand(
                        List.of(Index.fromOneBased(1)),
                        Optional.of(LocalDate.of(2026, 3, 16)),
                        Optional.of(new GroupName("T01"))));
    }

    @Test
    public void parse_onlyIndex_success() {
        assertParseSuccess(parser, " i/1",
                new MarkCommand(
                        List.of(Index.fromOneBased(1)),
                        Optional.empty(), Optional.empty()));
    }

    @Test
    public void parse_missingIndex_failure() {
        assertParseFailure(parser, " d/2026-03-16",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_preamble_failure() {
        assertParseFailure(parser, " 1 i/1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidDate_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(" i/1 d/2026-13-01"));
    }

    //@@author Ch3ngK
    @Test
    public void parse_invalidDate_failure() {
        assertParseFailure(parser, " i/1 d/2026-04-300", Session.MESSAGE_CONSTRAINTS);
    }
}
