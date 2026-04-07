package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.testutil.PersonBuilder;

public class ListAssignmentsCommandTest {

    private static final GroupName T01 = new GroupName("T01");

    @Test
    public void execute_allStudentsView_failure() {
        Model model = new ModelManager();

        assertCommandFailure(new ListAssignmentsCommand(), model,
                ClassScopedAssignmentCommand.MESSAGE_REQUIRE_ACTIVE_GROUP);
    }

    @Test
    public void execute_noAssignments_returnsNoAssignmentsMessage() throws Exception {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);

        assertEquals(new CommandResult("No assignments in T01."), new ListAssignmentsCommand().execute(model));
    }

    @Test
    public void execute_assignmentsPresent_returnsAssignmentSummaryWithGradedCounts() throws Exception {
        Model model = new ModelManager();
        Assignment quiz1 = new Assignment(new AssignmentName("Quiz 1"), LocalDate.of(2026, 4, 5), 20);
        Assignment quiz2 = new Assignment(new AssignmentName("Quiz 2"), LocalDate.of(2026, 4, 12), 25);
        model.addGroup(new Group(T01, List.of(quiz1, quiz2)));
        model.addPerson(new PersonBuilder().withName("Alice")
                .withMatricNumber("A1234567X")
                .withEmail("alice@example.com")
                .withPhone("91234567")
                .withGroups("T01")
                .withAssignmentGrade("T01", "Quiz 1", 18)
                .build());
        model.addPerson(new PersonBuilder().withName("Bob")
                .withMatricNumber("A2345678L")
                .withEmail("bob@example.com")
                .withPhone("92345678")
                .withGroups("T01")
                .withAssignmentGrade("T01", "Quiz 1", 15)
                .withAssignmentGrade("T01", "Quiz 2", 20)
                .build());
        model.switchToGroupView(T01);

        String expectedMessage = "Assignments in T01:\n"
                + "Quiz 1 | due 2026-04-05 | max 20 | 2/2 graded\n"
                + "Quiz 2 | due 2026-04-12 | max 25 | 1/2 graded";

        assertEquals(new CommandResult(expectedMessage), new ListAssignmentsCommand().execute(model));
    }
}
