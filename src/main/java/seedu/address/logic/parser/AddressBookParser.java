package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddSessionCommand;
import seedu.address.logic.commands.AddToGroupCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CreateAssignmentCommand;
import seedu.address.logic.commands.CreateGroupCommand;
import seedu.address.logic.commands.DeleteAssignmentCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteGroupCommand;
import seedu.address.logic.commands.DeleteSessionCommand;
import seedu.address.logic.commands.EditAssignmentCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditSessionCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.ExportViewCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.GradeAssignmentCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListAssignmentsCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListGroupsCommand;
import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.commands.PartCommand;
import seedu.address.logic.commands.RemoveFromGroupCommand;
import seedu.address.logic.commands.RenameGroupCommand;
import seedu.address.logic.commands.SwitchGroupCommand;
import seedu.address.logic.commands.UndoSessionCommand;
import seedu.address.logic.commands.UnmarkCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class AddressBookParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    private static final Logger logger = LogsCenter.getLogger(AddressBookParser.class);

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        logger.fine("Command word: " + commandWord + "; Arguments: " + arguments);

        switch (commandWord) {

        // Contact list commands
        case AddCommand.COMMAND_WORD:
            return new AddCommandParser().parse(arguments);
        case ListCommand.COMMAND_WORD:
            return new ListCommand();
        case EditCommand.COMMAND_WORD:
            return new EditCommandParser().parse(arguments);
        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommandParser().parse(arguments);
        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();
        case FindCommand.COMMAND_WORD:
            return new FindCommandParser().parse(arguments);

        // Assignment commands
        case CreateAssignmentCommand.COMMAND_WORD:
            // Fallthrough
        case CreateAssignmentCommand.SHORT_COMMAND_WORD:
            return new CreateAssignmentCommandParser().parse(arguments);
        case ListAssignmentsCommand.COMMAND_WORD:
            // Fallthrough
        case ListAssignmentsCommand.SHORT_COMMAND_WORD:
            return new ListAssignmentsCommandParser().parse(arguments);
        case GradeAssignmentCommand.COMMAND_WORD:
            // Fallthrough
        case GradeAssignmentCommand.SHORT_COMMAND_WORD:
            return new GradeAssignmentCommandParser().parse(arguments);
        case EditAssignmentCommand.COMMAND_WORD:
            // Fallthrough
        case EditAssignmentCommand.SHORT_COMMAND_WORD:
            return new EditAssignmentCommandParser().parse(arguments);
        case DeleteAssignmentCommand.COMMAND_WORD:
            // Fallthrough
        case DeleteAssignmentCommand.SHORT_COMMAND_WORD:
            return new DeleteAssignmentCommandParser().parse(arguments);

        // Group commands
        case CreateGroupCommand.COMMAND_WORD:
            return new CreateGroupCommandParser().parse(arguments);
        case ListGroupsCommand.COMMAND_WORD:
            return new ListGroupsCommandParser().parse(arguments);
        case RenameGroupCommand.COMMAND_WORD:
            return new RenameGroupCommandParser().parse(arguments);
        case SwitchGroupCommand.COMMAND_WORD:
            return new SwitchGroupCommandParser().parse(arguments);
        case AddToGroupCommand.COMMAND_WORD:
            return new AddToGroupCommandParser().parse(arguments);
        case RemoveFromGroupCommand.COMMAND_WORD:
            return new RemoveFromGroupCommandParser().parse(arguments);
        case DeleteGroupCommand.COMMAND_WORD:
            return new DeleteGroupCommandParser().parse(arguments);

        // Session commands
        case ViewCommand.COMMAND_WORD:
            return new ViewCommandParser().parse(arguments);
        case AddSessionCommand.COMMAND_WORD:
            return new AddSessionCommandParser().parse(arguments);
        case EditSessionCommand.COMMAND_WORD:
            return new EditSessionCommandParser().parse(arguments);
        case UndoSessionCommand.COMMAND_WORD:
            return new UndoSessionCommand();
        case DeleteSessionCommand.COMMAND_WORD:
            return new DeleteSessionCommandParser().parse(arguments);
        case ExportViewCommand.COMMAND_WORD:
            return new ExportViewCommandParser().parse(arguments);

        case MarkCommand.COMMAND_WORD:
            return new MarkCommandParser().parse(arguments);
        case UnmarkCommand.COMMAND_WORD:
            return new UnmarkCommandParser().parse(arguments);
        case PartCommand.COMMAND_WORD:
            return new PartCommandParser().parse(arguments);

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();
        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        default:
            logger.finer("This user input caused a ParseException: " + userInput);
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

}
