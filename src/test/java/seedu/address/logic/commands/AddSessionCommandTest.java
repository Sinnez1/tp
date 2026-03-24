package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classspace.Group;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.MatricNumber;
import seedu.address.testutil.PersonBuilder;

public class AddSessionCommandTest {
    private static final ClassSpaceName T01 = new ClassSpaceName("T01");
    private static final LocalDate SESSION_DATE = LocalDate.of(2026, 3, 16);

    @Test
    public void execute_addsSessionForCurrentGroup() {
        Model model = new ModelManager();
        model.addClassSpace(new Group(T01));
        model.switchToClassSpaceView(T01);
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567").withClassSpaces("T01").build());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToClassSpaceView(T01);
        var person = expectedModel.findPersonByMatricNumber(new MatricNumber("A1234567X")).orElseThrow();
        expectedModel.setPerson(person, person.withUpdatedSession(T01,
                new seedu.address.model.person.Session(SESSION_DATE,
                        new Attendance(Attendance.Status.UNINITIALISED),
                        new seedu.address.model.person.Participation(0))));
        expectedModel.setActiveSessionDate(SESSION_DATE);

        AddSessionCommand command = new AddSessionCommand(SESSION_DATE);
        assertCommandSuccess(command, model,
                String.format(AddSessionCommand.MESSAGE_SUCCESS, SESSION_DATE, T01, 1), expectedModel);
        assertTrue(model.findPersonByMatricNumber(new MatricNumber("A1234567X")).orElseThrow()
                .getClassSpaceSessions().get(T01).getSession(SESSION_DATE).isPresent());
    }

    @Test
    public void execute_sessionAlreadyExists_throwsCommandException() {
        Model model = new ModelManager();
        model.addClassSpace(new Group(T01));
        model.switchToClassSpaceView(T01);
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567")
                .withSession("T01", SESSION_DATE.toString(), "PRESENT", 1).build());

        AddSessionCommand command = new AddSessionCommand(SESSION_DATE);
        String expectedMessage = String.format(AddSessionCommand.MESSAGE_SESSION_ALREADY_EXISTS, SESSION_DATE, T01);
        assertThrows(CommandException.class, expectedMessage, () -> command.execute(model));
    }

    @Test
    public void execute_noActiveGroup_throwsCommandException() {
        Model model = new ModelManager();
        AddSessionCommand command = new AddSessionCommand(SESSION_DATE);
        assertThrows(CommandException.class, AddSessionCommand.MESSAGE_NO_ACTIVE_CLASS_SPACE, () -> {
            command.execute(model);
        });
    }

    @Test
    public void equals() {
        AddSessionCommand first = new AddSessionCommand(SESSION_DATE);
        AddSessionCommand second = new AddSessionCommand(LocalDate.of(2026, 3, 17));
        AddSessionCommand firstWithGroup = new AddSessionCommand(SESSION_DATE, T01);

        assertTrue(first.equals(first));
        assertTrue(first.equals(new AddSessionCommand(SESSION_DATE)));
        assertFalse(first.equals(second));
        assertFalse(first.equals(firstWithGroup));
        assertFalse(first.equals(null));
        assertFalse(first.equals(1));
    }

    @Test
    public void execute_partialSessionCoverage_addsMissingStudentsOnly() throws Exception {
        Model model = new ModelManager();
        model.addClassSpace(new Group(T01));
        model.switchToClassSpaceView(T01);
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567")
                .withSession("T01", SESSION_DATE.toString(), "PRESENT", 1).build());
        model.addPerson(new PersonBuilder().withName("Bob Lee").withMatricNumber("A1234568W")
                .withEmail("bob@example.com").withPhone("92345678").withClassSpaces("T01").build());

        AddSessionCommand command = new AddSessionCommand(SESSION_DATE);
        CommandResult result = command.execute(model);
        assertEquals(String.format(AddSessionCommand.MESSAGE_SUCCESS_PARTIAL, SESSION_DATE, T01, 1, 1),
                result.getFeedbackToUser());

        assertTrue(model.findPersonByMatricNumber(new MatricNumber("A1234567X")).orElseThrow()
                .getClassSpaceSessions().get(T01).getSession(SESSION_DATE).isPresent());
        assertTrue(model.findPersonByMatricNumber(new MatricNumber("A1234568W")).orElseThrow()
                .getClassSpaceSessions().get(T01).getSession(SESSION_DATE).isPresent());
    }
}
