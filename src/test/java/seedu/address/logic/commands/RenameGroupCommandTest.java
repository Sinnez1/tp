package seedu.address.logic.commands;

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

public class RenameGroupCommandTest {

    private static final GroupName T01 = new GroupName("T01");
    private static final GroupName T02 = new GroupName("T02");
    private static final GroupName LAB1 = new GroupName("Lab_1");

    @Test
    public void execute_existingGroup_success() {
        Model model = new ModelManager();
        Group originalGroup = new Group(T01,
                List.of(new Assignment(new AssignmentName("Quiz 1"), LocalDate.of(2026, 4, 5), 20)));
        Person alice = new PersonBuilder().withName("Alice")
                .withMatricNumber("A1234567X")
                .withEmail("alice@example.com")
                .withPhone("91234567")
                .withGroups("T01")
                .withSession("T01", "2026-03-16", "PRESENT", 2)
                .withAssignmentGrade("T01", "Quiz 1", 17)
                .build();

        model.addGroup(originalGroup);
        model.addGroup(new Group(LAB1));
        model.addPerson(alice);
        model.switchToGroupView(T01);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setPerson(alice, alice.withRenamedGroup(T01, T02));
        expectedModel.setGroup(originalGroup, new Group(T02, List.copyOf(originalGroup.getAssignments())));

        RenameGroupCommand command = new RenameGroupCommand(T01, T02);

        assertCommandSuccess(command, model,
                String.format(RenameGroupCommand.MESSAGE_SUCCESS, T01.value, T02.value), expectedModel);
    }

    @Test
    public void execute_missingGroup_failure() {
        Model model = new ModelManager();

        assertCommandFailure(new RenameGroupCommand(T01, T02), model, RenameGroupCommand.MESSAGE_GROUP_NOT_FOUND);
    }

    @Test
    public void execute_duplicateTargetGroup_failure() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.addGroup(new Group(T02));

        assertCommandFailure(new RenameGroupCommand(T01, T02), model, RenameGroupCommand.MESSAGE_DUPLICATE_GROUP);
    }

    @Test
    public void execute_sameGroupName_failure() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));

        assertCommandFailure(new RenameGroupCommand(T01, T01), model, Command.MESSAGE_NOTHING_CHANGED);
    }

    @Test
    public void equals() {
        RenameGroupCommand renameToT02Command = new RenameGroupCommand(T01, T02);
        RenameGroupCommand renameToLab1Command = new RenameGroupCommand(T01, LAB1);

        org.junit.jupiter.api.Assertions.assertTrue(renameToT02Command.equals(renameToT02Command));
        org.junit.jupiter.api.Assertions.assertTrue(renameToT02Command.equals(new RenameGroupCommand(T01, T02)));
        org.junit.jupiter.api.Assertions.assertFalse(renameToT02Command.equals(null));
        org.junit.jupiter.api.Assertions.assertFalse(renameToT02Command.equals(5));
        org.junit.jupiter.api.Assertions.assertFalse(renameToT02Command.equals(renameToLab1Command));
    }
}
