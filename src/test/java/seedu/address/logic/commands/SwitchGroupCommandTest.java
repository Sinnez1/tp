package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;

public class SwitchGroupCommandTest {

    private static final GroupName T01 = new GroupName("T01");
    private static final GroupName T02 = new GroupName("T02");

    @Test
    public void execute_switchToAllStudentsView_success() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.setAttendanceViewActive(true);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setAttendanceViewActive(false);
        expectedModel.switchToAllStudentsView();

        assertCommandSuccess(new SwitchGroupCommand(), model,
                SwitchGroupCommand.MESSAGE_SWITCHED_TO_ALL, expectedModel);
        assertFalse(model.isAttendanceViewActive());
    }

    @Test
    public void execute_switchToExistingGroup_success() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.addGroup(new Group(T02));
        model.setAttendanceViewActive(true);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setAttendanceViewActive(false);
        expectedModel.switchToGroupView(T02);

        assertCommandSuccess(new SwitchGroupCommand(T02), model,
                String.format(SwitchGroupCommand.MESSAGE_SWITCHED_TO_GROUP, T02.value), expectedModel);
        assertFalse(model.isAttendanceViewActive());
    }

    @Test
    public void execute_missingGroup_failure() {
        Model model = new ModelManager();

        assertCommandFailure(new SwitchGroupCommand(T01), model, SwitchGroupCommand.MESSAGE_GROUP_NOT_FOUND);
    }

    @Test
    public void equals() {
        SwitchGroupCommand switchAllCommand = new SwitchGroupCommand();
        SwitchGroupCommand switchT01Command = new SwitchGroupCommand(T01);
        SwitchGroupCommand switchT02Command = new SwitchGroupCommand(T02);

        org.junit.jupiter.api.Assertions.assertTrue(switchAllCommand.equals(new SwitchGroupCommand()));
        org.junit.jupiter.api.Assertions.assertTrue(switchT01Command.equals(new SwitchGroupCommand(T01)));
        org.junit.jupiter.api.Assertions.assertFalse(switchT01Command.equals(switchAllCommand));
        org.junit.jupiter.api.Assertions.assertFalse(switchT01Command.equals(switchT02Command));
        org.junit.jupiter.api.Assertions.assertFalse(switchT01Command.equals(null));
    }
}
