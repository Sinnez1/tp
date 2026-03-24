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
import seedu.address.model.classspace.Group;
import seedu.address.model.classspace.ClassSpaceName;

public class CreateAssignmentCommandTest {

    private static final ClassSpaceName T01 = new ClassSpaceName("T01");

    @Test
    public void execute_validClassContext_success() {
        Model model = new ModelManager();
        model.addClassSpace(new Group(T01));
        model.switchToClassSpaceView(T01);

        CreateAssignmentCommand command = new CreateAssignmentCommand(new AssignmentName("Quiz 1"),
                LocalDate.of(2026, 4, 5), 20);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToClassSpaceView(T01);
        expectedModel.setClassSpace(new Group(T01), new Group(T01,
                List.of(new Assignment(new AssignmentName("Quiz 1"), LocalDate.of(2026, 4, 5), 20))));

        assertCommandSuccess(command, model, "Created assignment Quiz 1 in T01.", expectedModel);
    }

    @Test
    public void execute_allStudentsView_throwsCommandException() {
        Model model = new ModelManager();
        assertCommandFailure(new CreateAssignmentCommand(new AssignmentName("Quiz 1"),
                LocalDate.of(2026, 4, 5), 20), model,
                ClassScopedAssignmentCommand.MESSAGE_REQUIRE_ACTIVE_CLASS_SPACE);
    }
}
