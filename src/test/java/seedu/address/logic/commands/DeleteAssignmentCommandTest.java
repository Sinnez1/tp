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

public class DeleteAssignmentCommandTest {

    private static final GroupName T01 = new GroupName("T01");

    @Test
    public void execute_validActiveGroup_success() {
        Model model = new ModelManager();
        Assignment quiz1 = new Assignment(new AssignmentName("Quiz 1"), LocalDate.of(2026, 4, 5), 20);
        Assignment quiz2 = new Assignment(new AssignmentName("Quiz 2"), LocalDate.of(2026, 4, 12), 25);
        Group group = new Group(T01, List.of(quiz1, quiz2));
        Person alice = new PersonBuilder().withName("Alice")
                .withMatricNumber("A1234567X")
                .withEmail("alice@example.com")
                .withPhone("91234567")
                .withGroups("T01")
                .withAssignmentGrade("T01", "Quiz 1", 18)
                .build();

        model.addGroup(group);
        model.addPerson(alice);
        model.switchToGroupView(T01);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setGroup(group, new Group(T01, List.of(quiz2)));
        expectedModel.setPerson(alice, alice.withoutAssignmentGrade(T01, new AssignmentName("Quiz 1")));

        DeleteAssignmentCommand command = new DeleteAssignmentCommand(new AssignmentName("Quiz 1"));

        assertCommandSuccess(command, model,
                String.format(DeleteAssignmentCommand.MESSAGE_SUCCESS, "Quiz 1", T01.value), expectedModel);
    }

    @Test
    public void execute_allStudentsView_failure() {
        Model model = new ModelManager();

        assertCommandFailure(new DeleteAssignmentCommand(new AssignmentName("Quiz 1")), model,
                ClassScopedAssignmentCommand.MESSAGE_REQUIRE_ACTIVE_GROUP);
    }

    @Test
    public void execute_missingAssignment_failure() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);

        assertCommandFailure(new DeleteAssignmentCommand(new AssignmentName("Quiz 1")), model,
                ClassScopedAssignmentCommand.MESSAGE_ASSIGNMENT_NOT_FOUND);
    }
}
