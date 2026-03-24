package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classspace.Group;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.MatricNumber;
import seedu.address.testutil.PersonBuilder;

public class DeleteSessionCommandTest {
    private static final ClassSpaceName T01 = new ClassSpaceName("T01");
    private static final LocalDate SESSION_DATE = LocalDate.of(2026, 3, 16);
    private static final LocalDate OTHER_DATE = LocalDate.of(2026, 3, 17);

    @Test
    public void execute_deletesSessionForCurrentGroup() {
        Model model = new ModelManager();
        model.addClassSpace(new Group(T01));
        model.switchToClassSpaceView(T01);
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567")
                .withSession("T01", SESSION_DATE.toString(), "PRESENT", 1)
                .withSession("T01", OTHER_DATE.toString(), "ABSENT", 0).build());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToClassSpaceView(T01);
        var person = expectedModel.findPersonByMatricNumber(new MatricNumber("A1234567X")).orElseThrow();
        expectedModel.setPerson(person, person.withoutSession(T01, SESSION_DATE));

        DeleteSessionCommand command = new DeleteSessionCommand(SESSION_DATE, Optional.empty(), true);
        assertCommandSuccess(command, model,
                String.format(DeleteSessionCommand.MESSAGE_SUCCESS, SESSION_DATE, T01), expectedModel);

        assertFalse(model.findPersonByMatricNumber(new MatricNumber("A1234567X")).orElseThrow()
                .getClassSpaceSessions().get(T01).getSession(SESSION_DATE).isPresent());
        assertTrue(model.findPersonByMatricNumber(new MatricNumber("A1234567X")).orElseThrow()
                .getClassSpaceSessions().get(T01).getSession(OTHER_DATE).isPresent());
    }

    @Test
    public void execute_activeSessionDeleted_clearsActiveSessionDate() {
        Model model = new ModelManager();
        model.addClassSpace(new Group(T01));
        model.switchToClassSpaceView(T01);
        model.setActiveSessionDate(SESSION_DATE);
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567")
                .withSession("T01", SESSION_DATE.toString(), "PRESENT", 1).build());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToClassSpaceView(T01);
        var person = expectedModel.findPersonByMatricNumber(new MatricNumber("A1234567X")).orElseThrow();
        expectedModel.setPerson(person, person.withoutSession(T01, SESSION_DATE));
        expectedModel.clearActiveSessionDate();
        expectedModel.updateFilteredPersonList(seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS);

        DeleteSessionCommand command = new DeleteSessionCommand(SESSION_DATE, Optional.empty(), true);
        assertCommandSuccess(command, model,
                String.format(DeleteSessionCommand.MESSAGE_SUCCESS, SESSION_DATE, T01), expectedModel);
        assertTrue(model.getActiveSessionDate().isEmpty());
    }

    @Test
    public void execute_sessionMissing_throwsCommandException() {
        Model model = new ModelManager();
        model.addClassSpace(new Group(T01));
        model.switchToClassSpaceView(T01);

        DeleteSessionCommand command = new DeleteSessionCommand(SESSION_DATE, Optional.empty(), true);
        String expectedMessage = String.format(DeleteSessionCommand.MESSAGE_SESSION_NOT_FOUND, SESSION_DATE, T01);
        assertThrows(CommandException.class, expectedMessage, () -> command.execute(model));
    }

    @Test
    public void execute_noActiveGroup_throwsCommandException() {
        Model model = new ModelManager();
        DeleteSessionCommand command = new DeleteSessionCommand(SESSION_DATE);
        assertThrows(CommandException.class, DeleteSessionCommand.MESSAGE_NO_ACTIVE_CLASS_SPACE, () -> {
            command.execute(model);
        });
    }

    @Test
    public void equals() {
        DeleteSessionCommand first = new DeleteSessionCommand(SESSION_DATE);
        DeleteSessionCommand second = new DeleteSessionCommand(OTHER_DATE);
        DeleteSessionCommand firstWithGroup = new DeleteSessionCommand(SESSION_DATE, T01);

        assertTrue(first.equals(first));
        assertTrue(first.equals(new DeleteSessionCommand(SESSION_DATE)));
        assertFalse(first.equals(second));
        assertFalse(first.equals(firstWithGroup));
        assertFalse(first.equals(null));
        assertFalse(first.equals(1));
    }

    @Test
    public void toStringMethod() {
        DeleteSessionCommand command = new DeleteSessionCommand(SESSION_DATE, T01);
        String expected = DeleteSessionCommand.class.getCanonicalName()
                + "{sessionDate=" + SESSION_DATE + ", classSpaceName=Optional[" + T01 + "]}";
        assertEquals(expected, command.toString());
    }
}
