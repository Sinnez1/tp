package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class DeleteGroupCommandTest {

    private static final GroupName T01 = new GroupName("T01");
    private static final GroupName LAB1 = new GroupName("Lab_1");

    @Test
    public void execute_existingActiveGroup_success() {
        Model model = new ModelManager();
        Assignment quiz1 = new Assignment(new AssignmentName("Quiz 1"), LocalDate.of(2026, 4, 5), 20);
        Group group = new Group(T01, List.of(quiz1));
        Person alice = new PersonBuilder().withName("Alice")
                .withMatricNumber("A1234567X")
                .withEmail("alice@example.com")
                .withPhone("91234567")
                .withGroups("T01")
                .withSession("T01", "2026-03-16", "PRESENT", 3)
                .withAssignmentGrade("T01", "Quiz 1", 18)
                .build();

        model.addGroup(group);
        model.addGroup(new Group(LAB1));
        model.addPerson(alice);
        model.switchToGroupView(T01);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setPerson(alice, alice.withoutGroupData(T01));
        expectedModel.deleteGroup(group);

        DeleteGroupCommand command = new DeleteGroupCommand(T01);

        assertCommandSuccess(command, model,
                String.format(DeleteGroupCommand.MESSAGE_SUCCESS, T01.value), expectedModel);
        assertTrue(model.getActiveGroupName().isEmpty());
    }

    @Test
    public void execute_missingGroup_failure() {
        Model model = new ModelManager();

        assertCommandFailure(new DeleteGroupCommand(T01), model, DeleteGroupCommand.MESSAGE_GROUP_NOT_FOUND);
    }

    @Test
    public void equals() {
        DeleteGroupCommand deleteT01Command = new DeleteGroupCommand(T01);
        DeleteGroupCommand deleteLab1Command = new DeleteGroupCommand(LAB1);

        org.junit.jupiter.api.Assertions.assertTrue(deleteT01Command.equals(deleteT01Command));
        org.junit.jupiter.api.Assertions.assertTrue(deleteT01Command.equals(new DeleteGroupCommand(T01)));
        org.junit.jupiter.api.Assertions.assertFalse(deleteT01Command.equals(null));
        org.junit.jupiter.api.Assertions.assertFalse(deleteT01Command.equals(5));
        org.junit.jupiter.api.Assertions.assertFalse(deleteT01Command.equals(deleteLab1Command));
    }
}
