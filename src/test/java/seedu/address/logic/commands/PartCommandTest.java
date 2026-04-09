package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MATRIC_NUMBER_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MATRIC_NUMBER_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Participation;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;
import seedu.address.testutil.PersonBuilder;

public class PartCommandTest {

    private static final GroupName T01 = new GroupName("T01");
    private static final GroupName T02 = new GroupName("T02");
    private static final LocalDate SESSION_DATE = LocalDate.of(2026, 3, 16);
    private static final LocalDate OTHER_DATE = LocalDate.of(2026, 4, 1);

    // ==================== execute: success paths ====================

    @Test
    public void execute_withoutDate_usesActiveSessionDate() {
        // EP: no explicit date provided — active session date is used instead
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.setActiveSessionDate(SESSION_DATE);

        Person originalPerson = new PersonBuilder().withName("Alice").withMatricNumber(VALID_MATRIC_NUMBER_AMY)
                .withEmail("alice@example.com").withPhone("91234567").withGroups("T01").build();
        model.addPerson(originalPerson);

        Participation newParticipation = new Participation(4);
        Session updatedSession = new Session(SESSION_DATE, originalPerson.getAttendance(T01, SESSION_DATE),
                newParticipation);
        Person updatedPerson = originalPerson.withUpdatedSession(T01, updatedSession);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setPerson(originalPerson, updatedPerson);
        expectedModel.setActiveSessionDate(SESSION_DATE);

        PartCommand command = new PartCommand(
                List.of(Index.fromOneBased(1)), Optional.empty(), Optional.empty(),
                newParticipation);
        String expectedMessage = String.format(PartCommand.MESSAGE_PARTICIPATION_SUCCESS,
                Messages.format(updatedPerson, T01, SESSION_DATE));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(SESSION_DATE, model.getActiveSessionDate().orElseThrow());
    }

    @Test
    public void execute_withExplicitDate_usesProvidedDate() {
        // EP: date is explicitly provided via d/ — should override active session date
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.setActiveSessionDate(SESSION_DATE); // active date differs from explicit date

        Person originalPerson = new PersonBuilder().withName("Alice").withMatricNumber(VALID_MATRIC_NUMBER_AMY)
                .withEmail("alice@example.com").withPhone("91234567").withGroups("T01").build();
        model.addPerson(originalPerson);

        Participation newParticipation = new Participation(3);
        Session updatedSession = new Session(OTHER_DATE, originalPerson.getAttendance(T01, OTHER_DATE),
                newParticipation);
        Person updatedPerson = originalPerson.withUpdatedSession(T01, updatedSession);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setPerson(originalPerson, updatedPerson);
        expectedModel.setActiveSessionDate(OTHER_DATE);

        PartCommand command = new PartCommand(
                List.of(Index.fromOneBased(1)), Optional.of(OTHER_DATE), Optional.empty(),
                newParticipation);
        String expectedMessage = String.format(PartCommand.MESSAGE_PARTICIPATION_SUCCESS,
                Messages.format(updatedPerson, T01, OTHER_DATE));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(OTHER_DATE, model.getActiveSessionDate().orElseThrow());
    }

    @Test
    public void execute_withGroupSwitch_switchesGroupAndSetsParticipation() {
        // EP: g/ provided — command switches to that group before updating participation
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.addGroup(new Group(T02));
        model.switchToGroupView(T01); // currently in T01

        Person originalPerson = new PersonBuilder().withName("Alice").withMatricNumber(VALID_MATRIC_NUMBER_AMY)
                .withEmail("alice@example.com").withPhone("91234567").withGroups("T01", "T02").build();
        model.addPerson(originalPerson);
        model.switchToGroupView(T02); // person is visible in T02

        Participation newParticipation = new Participation(2);
        Session updatedSession = new Session(SESSION_DATE, originalPerson.getAttendance(T02, SESSION_DATE),
                newParticipation);
        Person updatedPerson = originalPerson.withUpdatedSession(T02, updatedSession);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T02);
        expectedModel.setPerson(originalPerson, updatedPerson);
        expectedModel.setActiveSessionDate(SESSION_DATE);

        PartCommand command = new PartCommand(
                List.of(Index.fromOneBased(1)), Optional.of(SESSION_DATE), Optional.of(T02),
                newParticipation);
        String expectedMessage = String.format(PartCommand.MESSAGE_PARTICIPATION_SUCCESS,
                Messages.format(updatedPerson, T02, SESSION_DATE));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    // ==================== execute: BVA for participation value ====================

    @Test
    public void execute_participationAtMinimumBoundary_success() {
        // BVA: participation value = 0 (minimum valid value)
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.setActiveSessionDate(SESSION_DATE);

        Person originalPerson = new PersonBuilder().withName("Alice").withMatricNumber(VALID_MATRIC_NUMBER_AMY)
                .withEmail("alice@example.com").withPhone("91234567").withGroups("T01").build();
        model.addPerson(originalPerson);

        Participation minParticipation = new Participation(0);
        Session updatedSession = new Session(SESSION_DATE, originalPerson.getAttendance(T01, SESSION_DATE),
                minParticipation);
        Person updatedPerson = originalPerson.withUpdatedSession(T01, updatedSession);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setPerson(originalPerson, updatedPerson);
        expectedModel.setActiveSessionDate(SESSION_DATE);

        PartCommand command = new PartCommand(
                List.of(Index.fromOneBased(1)), Optional.empty(), Optional.empty(),
                minParticipation);
        String expectedMessage = String.format(PartCommand.MESSAGE_PARTICIPATION_SUCCESS,
                Messages.format(updatedPerson, T01, SESSION_DATE));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_participationAtMaximumBoundary_success() {
        // BVA: participation value = 5 (maximum valid value)
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.setActiveSessionDate(SESSION_DATE);

        Person originalPerson = new PersonBuilder().withName("Alice").withMatricNumber(VALID_MATRIC_NUMBER_AMY)
                .withEmail("alice@example.com").withPhone("91234567").withGroups("T01").build();
        model.addPerson(originalPerson);

        Participation maxParticipation = new Participation(5);
        Session updatedSession = new Session(SESSION_DATE, originalPerson.getAttendance(T01, SESSION_DATE),
                maxParticipation);
        Person updatedPerson = originalPerson.withUpdatedSession(T01, updatedSession);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setPerson(originalPerson, updatedPerson);
        expectedModel.setActiveSessionDate(SESSION_DATE);

        PartCommand command = new PartCommand(
                List.of(Index.fromOneBased(1)), Optional.empty(), Optional.empty(),
                maxParticipation);
        String expectedMessage = String.format(PartCommand.MESSAGE_PARTICIPATION_SUCCESS,
                Messages.format(updatedPerson, T01, SESSION_DATE));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    // ==================== execute: BVA for index ====================


    @Test
    public void execute_indexAtLastPerson_success() {
        // BVA: index equals the size of the list (upper boundary — last valid index)
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.setActiveSessionDate(SESSION_DATE);

        Person alice = new PersonBuilder().withName("Alice").withMatricNumber(VALID_MATRIC_NUMBER_AMY)
                .withEmail("alice@example.com").withPhone("91234567").withGroups("T01").build();
        Person bob = new PersonBuilder().withName("Bob").withMatricNumber(VALID_MATRIC_NUMBER_BOB)
                .withEmail("bob@example.com").withPhone("98765432").withGroups("T01").build();
        model.addPerson(alice);
        model.addPerson(bob);

        // Index 2 is the last valid index (list size = 2)
        Participation newParticipation = new Participation(3);
        Session updatedSession = new Session(SESSION_DATE, bob.getAttendance(T01, SESSION_DATE),
                newParticipation);
        Person updatedBob = bob.withUpdatedSession(T01, updatedSession);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setPerson(bob, updatedBob);
        expectedModel.setActiveSessionDate(SESSION_DATE);

        PartCommand command = new PartCommand(
                List.of(Index.fromOneBased(2)), Optional.empty(), Optional.empty(),
                newParticipation);
        String expectedMessage = String.format(PartCommand.MESSAGE_PARTICIPATION_SUCCESS,
                Messages.format(updatedBob, T01, SESSION_DATE));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    // ==================== execute: error paths ====================

    @Test
    public void execute_notInGroupView_throwsCommandException() {
        // EP: model is in all-students view (no active group) — not in group view
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber(VALID_MATRIC_NUMBER_AMY)
                .withEmail("alice@example.com").withPhone("91234567").build());

        PartCommand command = new PartCommand(
                List.of(Index.fromOneBased(1)), Optional.of(SESSION_DATE), Optional.empty(),
                new Participation(3));
        assertThrows(CommandException.class, PartCommand.MESSAGE_REQUIRES_GROUP_VIEW, () -> command.execute(model));
    }

    @Test
    public void execute_withoutDateAndNoActiveSession_throwsCommandException() {
        // EP: no date provided and no active session date set
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber(VALID_MATRIC_NUMBER_AMY)
                .withEmail("alice@example.com").withPhone("91234567").withGroups("T01").build());

        PartCommand command = new PartCommand(
                List.of(Index.fromOneBased(1)), Optional.empty(), Optional.empty(),
                new Participation(3));
        assertThrows(CommandException.class, PartCommand.MESSAGE_NO_ACTIVE_SESSION, () -> command.execute(model));
    }

    @Test
    public void execute_groupNotFound_throwsCommandException() {
        // EP: g/ provided but the group does not exist in the model
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber(VALID_MATRIC_NUMBER_AMY)
                .withEmail("alice@example.com").withPhone("91234567").withGroups("T01").build());

        GroupName nonExistentGroup = new GroupName("T99");
        PartCommand command = new PartCommand(
                List.of(Index.fromOneBased(1)), Optional.of(SESSION_DATE),
                Optional.of(nonExistentGroup), new Participation(3));
        assertThrows(CommandException.class, PartCommand.MESSAGE_GROUP_NOT_FOUND, () -> command.execute(model));
    }

    @Test
    public void execute_invalidIndexOutOfBounds_throwsCommandException() {
        // BVA: index is one beyond the last valid index (list size + 1)
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.setActiveSessionDate(SESSION_DATE);
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber(VALID_MATRIC_NUMBER_AMY)
                .withEmail("alice@example.com").withPhone("91234567").withGroups("T01").build());

        // List has 1 person; index 2 is one past the boundary
        PartCommand command = new PartCommand(
                List.of(Index.fromOneBased(2)), Optional.empty(), Optional.empty(),
                new Participation(3));
        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    // ==================== equals ====================

    @Test
    public void equals() {
        Participation participation = new Participation(3);
        Participation diffParticipation = new Participation(5);

        PartCommand commandA = new PartCommand(
                List.of(Index.fromOneBased(1)), Optional.of(SESSION_DATE), Optional.empty(), participation);
        PartCommand commandB = new PartCommand(
                List.of(Index.fromOneBased(1)), Optional.of(SESSION_DATE), Optional.empty(), participation);
        PartCommand commandDiffIndex = new PartCommand(
                List.of(Index.fromOneBased(2)), Optional.of(SESSION_DATE), Optional.empty(), participation);
        PartCommand commandDiffDate = new PartCommand(
                List.of(Index.fromOneBased(1)), Optional.of(OTHER_DATE), Optional.empty(), participation);
        PartCommand commandDiffGroup = new PartCommand(
                List.of(Index.fromOneBased(1)), Optional.of(SESSION_DATE), Optional.of(T01), participation);
        PartCommand commandNoDate = new PartCommand(
                List.of(Index.fromOneBased(1)), Optional.empty(), Optional.empty(), participation);
        PartCommand commandDiffParticipation = new PartCommand(
                List.of(Index.fromOneBased(1)), Optional.of(SESSION_DATE), Optional.empty(), diffParticipation);

        // EP: same object -> equal
        assertEquals(commandA, commandA);

        // EP: same values -> equal
        assertEquals(commandA, commandB);

        // EP: null -> not equal
        assertNotEquals(null, commandA);

        // EP: different type -> not equal
        assertNotEquals("not a command", commandA);

        // EP: different index -> not equal
        assertNotEquals(commandA, commandDiffIndex);

        // EP: different date -> not equal
        assertNotEquals(commandA, commandDiffDate);

        // EP: different group -> not equal
        assertNotEquals(commandA, commandDiffGroup);

        // EP: one has date, other does not -> not equal
        assertNotEquals(commandA, commandNoDate);

        // EP: different participation value -> not equal
        assertNotEquals(commandA, commandDiffParticipation);
    }
}
