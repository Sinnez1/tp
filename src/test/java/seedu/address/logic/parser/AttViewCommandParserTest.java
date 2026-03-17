package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AttViewCommand;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Attendance;

public class AttViewCommandParserTest {

    private final AttViewCommandParser parser = new AttViewCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseSuccess(parser, "     ", new AttViewCommand());
    }

    @Test
    public void parse_invalidArg_throwsParseException() {
        assertParseFailure(parser, "late",
                AttViewCommandParser.MESSAGE_INVALID_ATTENDANCE_STATUS + "\n" + AttViewCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_extraArgs_throwsParseException() {
        assertParseFailure(parser, "present absent",
                AttViewCommandParser.MESSAGE_TOO_MANY_ARGUMENTS + "\n" + AttViewCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_validArgs_returnsAttViewCommand() {
        assertParseSuccess(parser, "", new AttViewCommand());
        assertParseSuccess(parser, "present", new AttViewCommand(new Attendance("PRESENT")));
        assertParseSuccess(parser, "  ABSENT  ", new AttViewCommand(new Attendance("ABSENT")));
        assertParseSuccess(parser, "d/2026-03-16", new AttViewCommand(LocalDate.of(2026, 3, 16)));
        assertParseSuccess(parser, "g/T01", new AttViewCommand(new ClassSpaceName("T01")));
        assertParseSuccess(parser, "d/2026-03-16 g/T01",
                new AttViewCommand(new ClassSpaceName("T01"), LocalDate.of(2026, 3, 16)));
        assertParseSuccess(parser, "present d/2026-03-16",
                new AttViewCommand(new Attendance("PRESENT"), LocalDate.of(2026, 3, 16)));
        assertParseSuccess(parser, "present d/2026-03-16 g/T01",
                new AttViewCommand(new Attendance("PRESENT"),
                        new ClassSpaceName("T01"), LocalDate.of(2026, 3, 16)));
    }
}
