package seedu.address.logic.commands;

import java.util.List;
import java.util.Map;

/**
 * Central registry of all command words.
 */
public class CommandRegistry {

    // Commands are listed in alphabetical order

    public static final List<String> COMMAND_WORDS = List.of(
            AddCommand.COMMAND_WORD,
            AddSessionCommand.COMMAND_WORD,
            AddToGroupCommand.COMMAND_WORD,
            ClearCommand.COMMAND_WORD,
            CreateAssignmentCommand.COMMAND_WORD,
            CreateGroupCommand.COMMAND_WORD,
            DeleteCommand.COMMAND_WORD,
            DeleteAssignmentCommand.COMMAND_WORD,
            DeleteGroupCommand.COMMAND_WORD,
            DeleteSessionCommand.COMMAND_WORD,
            EditCommand.COMMAND_WORD,
            EditAssignmentCommand.COMMAND_WORD,
            EditSessionCommand.COMMAND_WORD,
            ExitCommand.COMMAND_WORD,
            ExportViewCommand.COMMAND_WORD,
            FindCommand.COMMAND_WORD,
            GradeAssignmentCommand.COMMAND_WORD,
            HelpCommand.COMMAND_WORD,
            ListCommand.COMMAND_WORD,
            ListAssignmentsCommand.COMMAND_WORD,
            ListGroupsCommand.COMMAND_WORD,
            MarkCommand.COMMAND_WORD,
            PartCommand.COMMAND_WORD,
            RemoveFromGroupCommand.COMMAND_WORD,
            RenameGroupCommand.COMMAND_WORD,
            SwitchGroupCommand.COMMAND_WORD,
            UndoSessionCommand.COMMAND_WORD,
            UnmarkCommand.COMMAND_WORD,
            ViewCommand.COMMAND_WORD
    );

    public static final Map<String, String> COMMAND_ATTRIBUTES = Map.ofEntries(
            Map.entry(AddCommand.COMMAND_WORD, AddCommand.COMMAND_PARAMETERS),
            Map.entry(AddSessionCommand.COMMAND_WORD, AddSessionCommand.COMMAND_PARAMETERS),
            Map.entry(AddToGroupCommand.COMMAND_WORD, AddToGroupCommand.COMMAND_PARAMETERS),
            Map.entry(ClearCommand.COMMAND_WORD, ClearCommand.COMMAND_PARAMETERS),
            Map.entry(CreateAssignmentCommand.COMMAND_WORD, CreateAssignmentCommand.COMMAND_PARAMETERS),
            Map.entry(CreateGroupCommand.COMMAND_WORD, CreateGroupCommand.COMMAND_PARAMETERS),
            Map.entry(DeleteCommand.COMMAND_WORD, DeleteCommand.COMMAND_PARAMETERS),
            Map.entry(DeleteAssignmentCommand.COMMAND_WORD, DeleteAssignmentCommand.COMMAND_PARAMETERS),
            Map.entry(DeleteGroupCommand.COMMAND_WORD, DeleteGroupCommand.COMMAND_PARAMETERS),
            Map.entry(DeleteSessionCommand.COMMAND_WORD, DeleteSessionCommand.COMMAND_PARAMETERS),
            Map.entry(EditCommand.COMMAND_WORD, EditCommand.COMMAND_PARAMETERS),
            Map.entry(EditAssignmentCommand.COMMAND_WORD, EditAssignmentCommand.COMMAND_PARAMETERS),
            Map.entry(EditSessionCommand.COMMAND_WORD, EditSessionCommand.COMMAND_PARAMETERS),
            Map.entry(ExportViewCommand.COMMAND_WORD, ExportViewCommand.COMMAND_PARAMETERS),
            Map.entry(FindCommand.COMMAND_WORD, FindCommand.COMMAND_PARAMETERS),
            Map.entry(GradeAssignmentCommand.COMMAND_WORD, GradeAssignmentCommand.COMMAND_PARAMETERS),
            Map.entry(ListAssignmentsCommand.COMMAND_WORD, ListAssignmentsCommand.COMMAND_PARAMETERS),
            Map.entry(ListGroupsCommand.COMMAND_WORD, ListGroupsCommand.COMMAND_PARAMETERS),
            Map.entry(MarkCommand.COMMAND_WORD, MarkCommand.COMMAND_PARAMETERS),
            Map.entry(PartCommand.COMMAND_WORD, PartCommand.COMMAND_PARAMETERS),
            Map.entry(RemoveFromGroupCommand.COMMAND_WORD, RemoveFromGroupCommand.COMMAND_PARAMETERS),
            Map.entry(RenameGroupCommand.COMMAND_WORD, RenameGroupCommand.COMMAND_PARAMETERS),
            Map.entry(SwitchGroupCommand.COMMAND_WORD, SwitchGroupCommand.COMMAND_PARAMETERS),
            Map.entry(UndoSessionCommand.COMMAND_WORD, UndoSessionCommand.COMMAND_PARAMETERS),
            Map.entry(UnmarkCommand.COMMAND_WORD, UnmarkCommand.COMMAND_PARAMETERS),
            Map.entry(ViewCommand.COMMAND_WORD, ViewCommand.COMMAND_PARAMETERS)
    );

    private CommandRegistry() {} // prevent instantiation
}
