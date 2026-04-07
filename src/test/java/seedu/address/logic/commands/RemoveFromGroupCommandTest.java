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
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class RemoveFromGroupCommandTest {

    private static final GroupName T01 = new GroupName("T01");

    @Test
    public void execute_indexTargets_success() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        Person alice = new PersonBuilder().withName("Alice")
                .withMatricNumber("A1234567X")
                .withEmail("alice@example.com")
                .withPhone("91234567")
                .withGroups("T01")
                .build();
        model.addPerson(alice);
        model.switchToGroupView(T01);

        RemoveFromGroupCommand command = RemoveFromGroupCommand.forIndexes(T01,
                java.util.List.of(Index.fromOneBased(1)));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setPerson(alice, alice.withoutGroupData(T01));

        assertCommandSuccess(command, model, "Removed Alice from T01.", expectedModel);
    }

    @Test
    public void execute_matricTargets_success() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01,
                List.of(new Assignment(new AssignmentName("Quiz 1"), LocalDate.of(2026, 4, 5), 20))));
        Person alice = new PersonBuilder().withName("Alice")
                .withMatricNumber("A1234567X")
                .withEmail("alice@example.com")
                .withPhone("91234567")
                .withGroups("T01")
                .withAssignmentGrade("T01", "Quiz 1", 18)
                .build();
        Person bob = new PersonBuilder().withName("Bob")
                .withMatricNumber("A2345678L")
                .withEmail("bob@example.com")
                .withPhone("92345678")
                .build();
        model.addPerson(alice);
        model.addPerson(bob);

        RemoveFromGroupCommand command = RemoveFromGroupCommand.forMatricNumbers(T01,
                java.util.List.of(new MatricNumber("A1234567X"), new MatricNumber("A2345678L")));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(alice, alice.withoutGroupData(T01));

        assertCommandSuccess(command, model,
                "Removed Alice from T01. Not in T01: Bob.", expectedModel);
    }

    @Test
    public void execute_missingGroup_failure() {
        Model model = new ModelManager();

        RemoveFromGroupCommand command = RemoveFromGroupCommand.forIndexes(T01,
                java.util.List.of(Index.fromOneBased(1)));

        assertCommandFailure(command, model, RemoveFromGroupCommand.MESSAGE_GROUP_NOT_FOUND);
    }
}
