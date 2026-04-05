package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEXES;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddSessionCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.CreateAssignmentCommand;
import seedu.address.logic.commands.DeleteAssignmentCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteSessionCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.EditSessionCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.ExportViewCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.GradeAssignmentCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesFieldsPredicate;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        // EP: valid add command input -> returns AddCommand
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_addSession() throws Exception {
        // EP: valid add session command input -> returns AddSessionCommand
        assertTrue(parser.parseCommand(AddSessionCommand.COMMAND_WORD
                + " d/2026-03-16") instanceof AddSessionCommand);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        // EP: clear command with no argument -> returns ClearCommand
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        // EP: clear command with trailing arguments -> returns ClearCommand
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        // EP: valid delete command with index -> returns DeleteCommand
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + PREFIX_INDEXES + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        // EP: valid edit command with index and descriptor -> returns EditCommand
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + PREFIX_INDEXES + INDEX_FIRST_PERSON.getOneBased() + " "
                + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        // EP: exit command with no argument -> returns ExitCommand
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        // EP: exit command with trailing arguments -> returns ExitCommand
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        // EP: valid find command with multiple prefixed keywords -> returns FindCommand
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " n/foo n/bar n/baz");
        assertEquals(new FindCommand(new PersonMatchesFieldsPredicate(
                Arrays.asList("foo", "bar", "baz"),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList())), command);
    }

    @Test
    public void parseCommand_createAssignmentAlias() throws Exception {
        // EP: valid short form of create assignment command -> returns CreateAssignmentCommand
        assertTrue(parser.parseCommand(CreateAssignmentCommand.SHORT_COMMAND_WORD
                + " a/Quiz 1 d/2026-04-05 mm/20") instanceof CreateAssignmentCommand);
    }

    @Test
    public void parseCommand_gradeAssignmentAlias() throws Exception {
        // EP: valid short form of grade assignment command -> returns GradeAssignmentCommand
        assertTrue(parser.parseCommand(GradeAssignmentCommand.SHORT_COMMAND_WORD
                + " a/Quiz 1 i/1 gr/17") instanceof GradeAssignmentCommand);
    }

    @Test
    public void parseCommand_deleteAssignmentAlias() throws Exception {
        // EP: valid short form of delete assignment command -> returns DeleteAssignmentCommand
        assertTrue(parser.parseCommand(DeleteAssignmentCommand.SHORT_COMMAND_WORD
                + " a/Quiz 1") instanceof DeleteAssignmentCommand);
    }

    @Test
    public void parseCommand_deleteSession() throws Exception {
        // EP: valid delete session command input -> returns DeleteSessionCommand
        assertTrue(parser.parseCommand(DeleteSessionCommand.COMMAND_WORD
                + " d/2026-03-16") instanceof DeleteSessionCommand);
    }

    @Test
    public void parseCommand_editSession() throws Exception {
        // EP: valid edit session command input -> returns EditSessionCommand
        assertTrue(parser.parseCommand(EditSessionCommand.COMMAND_WORD
                + " d/2026-03-16 nd/2026-03-23") instanceof EditSessionCommand);
    }

    @Test
    public void parseCommand_exportView() throws Exception {
        // EP: valid export view command input -> returns ExportViewCommand
        assertTrue(parser.parseCommand(ExportViewCommand.COMMAND_WORD
                + " f/view.csv") instanceof ExportViewCommand);
    }

    @Test
    public void parseCommand_view() throws Exception {
        // EP: valid view command input -> returns ViewCommand
        assertTrue(parser.parseCommand(ViewCommand.COMMAND_WORD + " d/2026-03-16") instanceof ViewCommand);
    }

    @Test
    public void parseCommand_help() throws Exception {
        // EP: help command with no argument -> returns HelpCommand
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        // EP: help command with trailing arguments -> returns HelpCommand
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        // EP: list command with no argument -> returns ListCommand
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        // EP: list command with trailing arguments -> returns ListCommand
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        // EP: empty command input -> throws ParseException
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
                -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        // EP: unknown command -> throws ParseException
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
