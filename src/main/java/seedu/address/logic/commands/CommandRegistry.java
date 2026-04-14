package seedu.address.logic.commands;

import java.util.Map;

/**
 * Central registry of all command words.
 */
public class CommandRegistry {

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
            Map.entry(ExitCommand.COMMAND_WORD, ExitCommand.COMMAND_PARAMETERS),
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
            Map.entry(UnmarkCommand.COMMAND_WORD, UnmarkCommand.COMMAND_PARAMETERS),
            Map.entry(ViewCommand.COMMAND_WORD, ViewCommand.COMMAND_PARAMETERS)
    );

    public static final Map<String, String> COMMAND_ALIASES = Map.of(
            CreateAssignmentCommand.SHORT_COMMAND_WORD, CreateAssignmentCommand.COMMAND_PARAMETERS,
            EditAssignmentCommand.SHORT_COMMAND_WORD, EditAssignmentCommand.COMMAND_PARAMETERS,
            DeleteAssignmentCommand.SHORT_COMMAND_WORD, DeleteAssignmentCommand.COMMAND_PARAMETERS,
            ListAssignmentsCommand.SHORT_COMMAND_WORD, ListAssignmentsCommand.COMMAND_PARAMETERS,
            GradeAssignmentCommand.SHORT_COMMAND_WORD, GradeAssignmentCommand.COMMAND_PARAMETERS
    );

    private CommandRegistry() {} // prevent instantiation
}
