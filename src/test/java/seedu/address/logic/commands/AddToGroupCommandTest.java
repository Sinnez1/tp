package seedu.address.logic.commands;

import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class AddToGroupCommandTest {

    private static final GroupName T01 = new GroupName("T01");

    @Test
    public void execute_indexTargets_success() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));

        Person alice = new PersonBuilder().withName("Alice")
                .withMatricNumber("A1234567X")
                .withEmail("alice@example.com")
                .withPhone("91234567")
                .build();
        Person bob = new PersonBuilder().withName("Bob")
                .withMatricNumber("A2345678L")
                .withEmail("bob@example.com")
                .withPhone("92345678")
                .withGroups("T01")
                .build();
        model.addPerson(alice);
        model.addPerson(bob);

        AddToGroupCommand command = AddToGroupCommand.forIndexes(T01,
                java.util.List.of(Index.fromOneBased(1), Index.fromOneBased(2)));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(alice, new Person(alice, java.util.Set.of(T01)));

        assertCommandSuccess(command, model,
                "Added Alice to T01. Already in T01: Bob.", expectedModel);
    }

    @Test
    public void execute_matricTargets_success() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));

        Person alice = new PersonBuilder().withName("Alice")
                .withMatricNumber("A1234567X")
                .withEmail("alice@example.com")
                .withPhone("91234567")
                .build();
        model.addPerson(alice);

        AddToGroupCommand command = AddToGroupCommand.forMatricNumbers(T01,
                java.util.List.of(new MatricNumber("A1234567X")));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(alice, new Person(alice, java.util.Set.of(T01)));

        assertCommandSuccess(command, model, "Added Alice to T01.", expectedModel);
    }

    @Test
    public void execute_missingGroup_failure() {
        Model model = new ModelManager();

        AddToGroupCommand command = AddToGroupCommand.forIndexes(T01, java.util.List.of(Index.fromOneBased(1)));

        assertCommandFailure(command, model, AddToGroupCommand.MESSAGE_GROUP_NOT_FOUND);
    }

    @Test
    public void execute_invalidIndex_failure() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.addPerson(new PersonBuilder().withName("Alice")
                .withMatricNumber("A1234567X")
                .withEmail("alice@example.com")
                .withPhone("91234567")
                .build());

        AddToGroupCommand command = AddToGroupCommand.forIndexes(T01, java.util.List.of(Index.fromOneBased(2)));

        assertCommandFailure(command, model, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }
}
