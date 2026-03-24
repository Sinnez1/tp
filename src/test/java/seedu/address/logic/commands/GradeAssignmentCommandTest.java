package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.classspace.Group;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class GradeAssignmentCommandTest {

    private static final ClassSpaceName T01 = new ClassSpaceName("T01");

    @Test
    public void execute_validIndexTarget_success() {
        Model model = new ModelManager();
        Group group = new Group(T01,
                List.of(new Assignment(new AssignmentName("Quiz 1"), LocalDate.of(2026, 4, 5), 20)));
        model.addClassSpace(group);
        model.switchToClassSpaceView(T01);

        Person originalPerson = new PersonBuilder().withName("Alice").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567").withClassSpaces("T01").build();
        model.addPerson(originalPerson);

        GradeAssignmentCommand command = GradeAssignmentCommand.forIndexes(new AssignmentName("Quiz 1"),
                List.of(Index.fromOneBased(1)), 17);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToClassSpaceView(T01);
        expectedModel.setPerson(originalPerson,
                originalPerson.withUpdatedAssignmentGrade(T01, new AssignmentName("Quiz 1"), 17));

        assertCommandSuccess(command, model, "Graded Quiz 1 for Alice: 17/20.", expectedModel);
    }

    @Test
    public void execute_gradeAboveMaxMarks_failure() {
        Model model = new ModelManager();
        model.addClassSpace(new Group(T01,
                List.of(new Assignment(new AssignmentName("Quiz 1"), LocalDate.of(2026, 4, 5), 20))));
        model.switchToClassSpaceView(T01);
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567").withClassSpaces("T01").build());

        GradeAssignmentCommand command = GradeAssignmentCommand.forIndexes(new AssignmentName("Quiz 1"),
                List.of(Index.fromOneBased(1)), 25);

        assertCommandFailure(command, model, GradeAssignmentCommand.MESSAGE_GRADE_OUT_OF_RANGE);
    }
}
