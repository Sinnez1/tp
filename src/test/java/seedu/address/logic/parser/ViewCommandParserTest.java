package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ViewCommand;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Attendance;

public class ViewCommandParserTest {

    private final ViewCommandParser parser = new ViewCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseSuccess(parser, "     ", new ViewCommand());
    }

    @Test
    public void parse_invalidArg_throwsParseException() {
        assertParseFailure(parser, "late",
                ViewCommandParser.MESSAGE_INVALID_ATTENDANCE_STATUS + "\n" + ViewCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_extraArgs_throwsParseException() {
        assertParseFailure(parser, "present absent",
                ViewCommandParser.MESSAGE_TOO_MANY_ARGUMENTS + "\n" + ViewCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_validArgs_returnsViewCommand() {
        assertParseSuccess(parser, "", new ViewCommand());
        assertParseSuccess(parser, "present", new ViewCommand(new Attendance("PRESENT")));
        assertParseSuccess(parser, "  ABSENT  ", new ViewCommand(new Attendance("ABSENT")));
        assertParseSuccess(parser, "d/2026-03-16", new ViewCommand(LocalDate.of(2026, 3, 16)));
        assertParseSuccess(parser, "g/T01", new ViewCommand(new GroupName("T01")));
        assertParseSuccess(parser, "d/2026-03-16 g/T01",
                new ViewCommand(new GroupName("T01"), LocalDate.of(2026, 3, 16)));
        assertParseSuccess(parser, "present d/2026-03-16",
                new ViewCommand(new Attendance("PRESENT"), LocalDate.of(2026, 3, 16)));
        assertParseSuccess(parser, "present d/2026-03-16 g/T01",
                new ViewCommand(new Attendance("PRESENT"),
                        new GroupName("T01"), LocalDate.of(2026, 3, 16)));
    }
}
