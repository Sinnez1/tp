package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;

public class CreateGroupCommandTest {

    private static final Group T01 = new Group(new GroupName("T01"));
    private static final Group LAB1 = new Group(new GroupName("Lab_1"));

    @Test
    public void execute_newGroup_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        expectedModel.addGroup(T01);

        assertCommandSuccess(new CreateGroupCommand(T01), model,
                String.format(CreateGroupCommand.MESSAGE_SUCCESS, T01.getGroupName().value), expectedModel);
    }

    @Test
    public void execute_duplicateGroup_failure() {
        Model model = new ModelManager();
        model.addGroup(T01);

        assertCommandFailure(new CreateGroupCommand(T01), model, CreateGroupCommand.MESSAGE_DUPLICATE_GROUP);
    }

    @Test
    public void equals() {
        CreateGroupCommand createT01Command = new CreateGroupCommand(T01);
        CreateGroupCommand createLab1Command = new CreateGroupCommand(LAB1);

        org.junit.jupiter.api.Assertions.assertTrue(createT01Command.equals(createT01Command));
        org.junit.jupiter.api.Assertions.assertTrue(createT01Command.equals(new CreateGroupCommand(T01)));
        org.junit.jupiter.api.Assertions.assertFalse(createT01Command.equals(null));
        org.junit.jupiter.api.Assertions.assertFalse(createT01Command.equals(5));
        org.junit.jupiter.api.Assertions.assertFalse(createT01Command.equals(createLab1Command));
    }
}
