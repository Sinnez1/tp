package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddToGroupCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.testutil.PersonBuilder;

public class AddToGroupCommandParserTest {

    private static final GroupName T01 = new GroupName("T01");

    private final AddToGroupCommandParser parser = new AddToGroupCommandParser();

    @Test
    public void parse_indexTargets_success() throws Exception {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.addPerson(new PersonBuilder().withName("Alice")
                .withMatricNumber("A1234567X")
                .withEmail("alice@example.com")
                .withPhone("91234567")
                .build());

        AddToGroupCommand command = parser.parse(" g/T01 i/1");
        CommandResult result = command.execute(model);

        assertEquals(new CommandResult("Added Alice to T01."), result);
        assertTrue(model.getFilteredPersonList().get(0).hasGroup(T01));
    }

    @Test
    public void parse_matricTargets_success() throws Exception {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.addPerson(new PersonBuilder().withName("Alice")
                .withMatricNumber("A1234567X")
                .withEmail("alice@example.com")
                .withPhone("91234567")
                .build());

        AddToGroupCommand command = parser.parse(" g/T01 m/A1234567X");
        CommandResult result = command.execute(model);

        assertEquals(new CommandResult("Added Alice to T01."), result);
        assertTrue(model.getFilteredPersonList().get(0).hasGroup(T01));
    }

    @Test
    public void parse_bothMatricAndIndexTargets_failure() {
        assertParseFailure(parser, " g/T01 m/A1234567X i/1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddToGroupCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingGroupPrefix_failure() {
        assertParseFailure(parser, " i/1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddToGroupCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateIndexPrefix_failure() {
        assertParseFailure(parser, " g/weird name i/2 i/1 i/3",
                "There should only be one of this prefix: i/");
    }
}
