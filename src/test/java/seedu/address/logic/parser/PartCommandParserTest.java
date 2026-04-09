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
import seedu.address.logic.commands.PartCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Participation;
import seedu.address.model.person.Session;

public class PartCommandParserTest {

    private final PartCommandParser parser = new PartCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        assertParseSuccess(parser, " i/1 d/2026-03-16 g/T01 pv/4",
                new PartCommand(
                        List.of(Index.fromOneBased(1)),
                        Optional.of(LocalDate.of(2026, 3, 16)),
                        Optional.of(new GroupName("T01")),
                        new Participation(4)));
    }

    @Test
    public void parse_onlyRequiredFields_success() {
        assertParseSuccess(parser, " i/1 pv/3",
                new PartCommand(
                        List.of(Index.fromOneBased(1)),
                        Optional.empty(), Optional.empty(), new Participation(3)));
    }

    @Test
    public void parse_missingParticipation_failure() {
        assertParseFailure(parser, " i/1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, PartCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingIndex_failure() {
        assertParseFailure(parser, " pv/3",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, PartCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidDate_throwsParseException() {
        // to verify that it throws parse exception for an invalid date
        assertThrows(ParseException.class, () -> parser.parse(" i/1 d/2026-13-01 pv/4"));
    }

    //@@author Ch3ngK
    @Test
    public void parse_invalidDate_failure() {
        assertParseFailure(parser, " i/1 pv/3 d/2026-04-300", Session.MESSAGE_CONSTRAINTS);
    }
}
