package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;

public class ListGroupsCommandTest {

    @Test
    public void execute_noGroups_returnsNoGroupsMessage() throws Exception {
        Model model = new ModelManager();

        assertEquals(new CommandResult(ListGroupsCommand.MESSAGE_NO_GROUPS), new ListGroupsCommand().execute(model));
    }

    @Test
    public void execute_groupsPresent_returnsSortedGroupList() throws Exception {
        Model model = new ModelManager();
        model.addGroup(new Group(new GroupName("T02")));
        model.addGroup(new Group(new GroupName("b01")));
        model.addGroup(new Group(new GroupName("T01")));

        assertEquals(new CommandResult("Groups:\n1. b01\n2. T01\n3. T02"),
                new ListGroupsCommand().execute(model));
    }
}
