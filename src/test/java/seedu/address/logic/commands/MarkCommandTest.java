package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MATRIC_NUMBER_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MATRIC_NUMBER_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
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
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;
import seedu.address.testutil.PersonBuilder;

public class MarkCommandTest {

    private static final GroupName T01 = new GroupName("T01");
    private static final GroupName T02 = new GroupName("T02");
    private static final LocalDate SESSION_DATE = LocalDate.of(2026, 3, 16);
    private static final LocalDate OTHER_DATE = LocalDate.of(2026, 4, 1);

    // ==================== execute: success paths ====================

    @Test
    public void execute_withoutDate_usesActiveSessionDate() {
        // EP: no explicit date provided -> use active session date instead
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.setActiveSessionDate(SESSION_DATE);

        Person originalPerson = new PersonBuilder().withName("Alice").withMatricNumber(VALID_MATRIC_NUMBER_AMY)
                .withEmail("alice@example.com").withPhone("91234567").withGroups("T01").build();
        model.addPerson(originalPerson);

        Session markedSession = new Session(SESSION_DATE, new Attendance(Attendance.Status.PRESENT),
                originalPerson.getParticipation(T01, SESSION_DATE));
        Person updatedPerson = originalPerson.withUpdatedSession(T01, markedSession);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setPerson(originalPerson, updatedPerson);
        expectedModel.setActiveSessionDate(SESSION_DATE);

        MarkCommand command = new MarkCommand(Index.fromOneBased(1), Optional.empty(), Optional.empty());
        String expectedMessage = String.format(MarkCommand.MESSAGE_MARK_SUCCESS,
                Messages.format(updatedPerson, T01, SESSION_DATE));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(SESSION_DATE, model.getActiveSessionDate().orElseThrow());
    }

    @Test
    public void execute_withExplicitDate_usesProvidedDate() {
        // EP: date is explicitly provided via d/ -> override active session date
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.setActiveSessionDate(SESSION_DATE); // active date differs from explicit date

        Person originalPerson = new PersonBuilder().withName("Alice").withMatricNumber(VALID_MATRIC_NUMBER_AMY)
                .withEmail("alice@example.com").withPhone("91234567").withGroups("T01").build();
        model.addPerson(originalPerson);

        Session markedSession = new Session(OTHER_DATE, new Attendance(Attendance.Status.PRESENT),
                originalPerson.getParticipation(T01, OTHER_DATE));
        Person updatedPerson = originalPerson.withUpdatedSession(T01, markedSession);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setPerson(originalPerson, updatedPerson);
        expectedModel.setActiveSessionDate(OTHER_DATE);

        MarkCommand command = new MarkCommand(Index.fromOneBased(1), Optional.of(OTHER_DATE), Optional.empty());
        String expectedMessage = String.format(MarkCommand.MESSAGE_MARK_SUCCESS,
                Messages.format(updatedPerson, T01, OTHER_DATE));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(OTHER_DATE, model.getActiveSessionDate().orElseThrow());
    }

    @Test
    public void execute_withGroupSwitch_switchesGroupAndMarks() {
        // EP: g/ provided -> command switches to that group before marking
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.addGroup(new Group(T02));
        model.switchToGroupView(T01); // currently in T01

        Person originalPerson = new PersonBuilder().withName("Alice").withMatricNumber(VALID_MATRIC_NUMBER_AMY)
                .withEmail("alice@example.com").withPhone("91234567").withGroups("T01", "T02").build();
        model.addPerson(originalPerson);
        model.switchToGroupView(T02); // person is visible in T02

        Session markedSession = new Session(SESSION_DATE, new Attendance(Attendance.Status.PRESENT),
                originalPerson.getParticipation(T02, SESSION_DATE));
        Person updatedPerson = originalPerson.withUpdatedSession(T02, markedSession);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T02);
        expectedModel.setPerson(originalPerson, updatedPerson);
        expectedModel.setActiveSessionDate(SESSION_DATE);

        MarkCommand command = new MarkCommand(Index.fromOneBased(1), Optional.of(SESSION_DATE),
                Optional.of(T02));
        String expectedMessage = String.format(MarkCommand.MESSAGE_MARK_SUCCESS,
                Messages.format(updatedPerson, T02, SESSION_DATE));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    // ==================== execute: BVA for index ====================

    @Test
    public void execute_indexAtLastPerson_success() {
        // BVA: index equals the size of the list (last valid index)
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
        Session markedSession = new Session(SESSION_DATE, new Attendance(Attendance.Status.PRESENT),
                bob.getParticipation(T01, SESSION_DATE));
        Person updatedBob = bob.withUpdatedSession(T01, markedSession);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setPerson(bob, updatedBob);
        expectedModel.setActiveSessionDate(SESSION_DATE);

        MarkCommand command = new MarkCommand(Index.fromOneBased(2), Optional.empty(), Optional.empty());
        String expectedMessage = String.format(MarkCommand.MESSAGE_MARK_SUCCESS,
                Messages.format(updatedBob, T01, SESSION_DATE));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    // ==================== execute: error paths ====================

    @Test
    public void execute_notInGroupView_throwsCommandException() {
        // EP: model is in all-students view (no active group) -> throw not in group view error
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber(VALID_MATRIC_NUMBER_AMY)
                .withEmail("alice@example.com").withPhone("91234567").build());

        MarkCommand command = new MarkCommand(Index.fromOneBased(1), Optional.of(SESSION_DATE), Optional.empty());
        assertThrows(CommandException.class, MarkCommand.MESSAGE_REQUIRES_GROUP_VIEW, () -> command.execute(model));
    }

    @Test
    public void execute_withoutDateAndNoActiveSession_throwsCommandException() {
        // EP: no date provided and no active session date set
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber(VALID_MATRIC_NUMBER_AMY)
                .withEmail("alice@example.com").withPhone("91234567").withGroups("T01").build());

        MarkCommand command = new MarkCommand(Index.fromOneBased(1), Optional.empty(), Optional.empty());
        assertThrows(CommandException.class, MarkCommand.MESSAGE_NO_ACTIVE_SESSION, () -> command.execute(model));
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
        MarkCommand command = new MarkCommand(Index.fromOneBased(1), Optional.of(SESSION_DATE),
                Optional.of(nonExistentGroup));
        assertThrows(CommandException.class, MarkCommand.MESSAGE_GROUP_NOT_FOUND, () -> command.execute(model));
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
        MarkCommand command = new MarkCommand(Index.fromOneBased(2), Optional.empty(), Optional.empty());
        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    // ==================== equals ====================

    @Test
    public void equals() {
        MarkCommand commandA = new MarkCommand(Index.fromOneBased(1), Optional.of(SESSION_DATE), Optional.empty());
        MarkCommand commandB = new MarkCommand(Index.fromOneBased(1), Optional.of(SESSION_DATE), Optional.empty());
        MarkCommand commandDiffIndex = new MarkCommand(Index.fromOneBased(2), Optional.of(SESSION_DATE),
                Optional.empty());
        MarkCommand commandDiffDate = new MarkCommand(Index.fromOneBased(1), Optional.of(OTHER_DATE),
                Optional.empty());
        MarkCommand commandDiffGroup = new MarkCommand(Index.fromOneBased(1), Optional.of(SESSION_DATE),
                Optional.of(T01));
        MarkCommand commandNoDate = new MarkCommand(Index.fromOneBased(1), Optional.empty(), Optional.empty());

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
    }
}
