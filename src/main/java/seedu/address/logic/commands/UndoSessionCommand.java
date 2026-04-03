package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Undoes the last session-related mutation.
 */
public class UndoSessionCommand extends Command {
    public static final String COMMAND_WORD = "undosession";
    public static final String COMMAND_PARAMETERS = "";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Undoes the last session-related change.";
    public static final String MESSAGE_NO_HISTORY = "There is no session change to undo.";
    public static final String MESSAGE_SUCCESS = "Undid session change: %1$s";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        SessionCommandHistory.SessionCommandSnapshot snapshot = SessionCommandHistory.getLastSnapshot()
                .orElseThrow(() -> new CommandException(MESSAGE_NO_HISTORY));

        model.setAddressBook(snapshot.addressBook());
        if (snapshot.activeGroupName().isPresent()) {
            model.switchToGroupView(snapshot.activeGroupName().get());
        } else {
            model.switchToAllStudentsView();
        }
        if (snapshot.activeSessionDate().isPresent()) {
            model.setActiveSessionDate(snapshot.activeSessionDate().get());
        } else {
            model.clearActiveSessionDate();
        }
        if (snapshot.visibleRangeStart().isPresent() || snapshot.visibleRangeEnd().isPresent()) {
            model.setVisibleSessionRange(
                    snapshot.visibleRangeStart().orElse(null),
                    snapshot.visibleRangeEnd().orElse(null));
        } else {
            model.clearVisibleSessionRange();
        }
        model.setAttendanceViewActive(snapshot.attendanceViewActive());
        model.updateFilteredPersonList(seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS);
        SessionCommandHistory.clear();
        return new CommandResult(String.format(MESSAGE_SUCCESS, snapshot.description()));
    }
}
