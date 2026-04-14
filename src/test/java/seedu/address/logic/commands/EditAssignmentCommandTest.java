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

public class EditAssignmentCommandTest {

    private static final GroupName T01 = new GroupName("T01");

    @Test
    public void execute_validEdit_success() {
        Model model = new ModelManager();
        Assignment originalAssignment = new Assignment(new AssignmentName("Quiz 1"), LocalDate.of(2026, 4,
                5), 20);
        Group originalGroup = new Group(T01, List.of(originalAssignment));
        Person alice = new PersonBuilder().withName("Alice")
                .withMatricNumber("A1234567X")
                .withEmail("alice@example.com")
                .withPhone("91234567")
                .withGroups("T01")
                .withAssignmentGrade("T01", "Quiz 1", 18)
                .build();

        model.addGroup(originalGroup);
        model.addPerson(alice);
        model.switchToGroupView(T01);

        EditAssignmentCommand.EditAssignmentDescriptor descriptor = new EditAssignmentCommand
                .EditAssignmentDescriptor();
        descriptor.setNewAssignmentName(new AssignmentName("Quiz 1 Revised"));
        descriptor.setDueDate(LocalDate.of(2026, 4, 8));
        descriptor.setMaxMarks(25);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        Assignment editedAssignment = new Assignment(new AssignmentName("Quiz 1 Revised"),
                LocalDate.of(2026, 4, 8), 25);
        expectedModel.setGroup(originalGroup, new Group(T01, List.of(editedAssignment)));
        expectedModel.setPerson(alice, alice.withRenamedAssignmentGrade(T01,
                new AssignmentName("Quiz 1"), new AssignmentName("Quiz 1 Revised")));

        EditAssignmentCommand command = new EditAssignmentCommand(new AssignmentName("Quiz 1"), descriptor);

        assertCommandSuccess(command, model,
                String.format(EditAssignmentCommand.MESSAGE_SUCCESS, "Quiz 1 Revised", T01.value), expectedModel);
    }

    @Test
    public void execute_sameAssignmentDetails_failure() {
        Model model = new ModelManager();
        Assignment originalAssignment = new Assignment(new AssignmentName("Quiz 1"),
                LocalDate.of(2026, 4, 5), 20);
        model.addGroup(new Group(T01, List.of(originalAssignment)));
        model.switchToGroupView(T01);

        EditAssignmentCommand.EditAssignmentDescriptor descriptor =
                new EditAssignmentCommand.EditAssignmentDescriptor();
        descriptor.setNewAssignmentName(new AssignmentName("Quiz 1"));

        assertCommandFailure(new EditAssignmentCommand(new AssignmentName("Quiz 1"), descriptor),
                model, Command.MESSAGE_NOTHING_CHANGED);
    }

    @Test
    public void execute_duplicateAssignmentName_failure() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01, List.of(
                new Assignment(new AssignmentName("Quiz 1"), LocalDate.of(2026, 4, 5),
                        20),
                new Assignment(new AssignmentName("Quiz 2"), LocalDate.of(2026, 4, 12),
                        25))));
        model.switchToGroupView(T01);

        EditAssignmentCommand.EditAssignmentDescriptor descriptor = new EditAssignmentCommand
                .EditAssignmentDescriptor();
        descriptor.setNewAssignmentName(new AssignmentName("Quiz 2"));

        assertCommandFailure(new EditAssignmentCommand(new AssignmentName("Quiz 1"), descriptor), model,
                ClassScopedAssignmentCommand.MESSAGE_DUPLICATE_ASSIGNMENT);
    }

    @Test
    public void execute_newMaxMarksLowerThanExistingGrade_failure() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01,
                List.of(new Assignment(new AssignmentName("Quiz 1"), LocalDate.of(2026, 4, 5),
                        20))));
        model.addPerson(new PersonBuilder().withName("Alice")
                .withMatricNumber("A1234567X")
                .withEmail("alice@example.com")
                .withPhone("91234567")
                .withGroups("T01")
                .withAssignmentGrade("T01", "Quiz 1", 18)
                .build());
        model.switchToGroupView(T01);

        EditAssignmentCommand.EditAssignmentDescriptor descriptor = new EditAssignmentCommand
                .EditAssignmentDescriptor();
        descriptor.setMaxMarks(10);

        assertCommandFailure(new EditAssignmentCommand(new AssignmentName("Quiz 1"), descriptor), model,
                ClassScopedAssignmentCommand.MESSAGE_INVALID_MAX_MARKS_FOR_EXISTING_GRADES);
    }
}
