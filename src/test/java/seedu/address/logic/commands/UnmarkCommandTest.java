package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classspace.ClassSpace;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;
import seedu.address.testutil.PersonBuilder;

public class UnmarkCommandTest {

    private static final ClassSpaceName T01 = new ClassSpaceName("T01");
    private static final LocalDate SESSION_DATE = LocalDate.of(2026, 3, 16);

    @Test
    public void execute_withoutDate_usesActiveSessionDate() {
        Model model = new ModelManager();
        model.addClassSpace(new ClassSpace(T01));
        model.switchToClassSpaceView(T01);
        model.setActiveSessionDate(SESSION_DATE);

        Person originalPerson = new PersonBuilder().withName("Alice").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567").withClassSpaces("T01").build();
        model.addPerson(originalPerson);

        Session unmarkedSession = new Session(SESSION_DATE, new Attendance(Attendance.Status.ABSENT),
                originalPerson.getParticipation(T01, SESSION_DATE));
        Person updatedPerson = originalPerson.withUpdatedSession(T01, unmarkedSession);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToClassSpaceView(T01);
        expectedModel.setPerson(originalPerson, updatedPerson);
        expectedModel.setActiveSessionDate(SESSION_DATE);

        UnmarkCommand command = new UnmarkCommand(Index.fromOneBased(1), Optional.empty(), Optional.empty());
        String expectedMessage = String.format(UnmarkCommand.MESSAGE_UNMARK_SUCCESS,
                seedu.address.logic.Messages.format(updatedPerson, T01, SESSION_DATE));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(SESSION_DATE, model.getActiveSessionDate().orElseThrow());
    }

    @Test
    public void execute_withoutDateAndNoActiveSession_throwsCommandException() {
        Model model = new ModelManager();
        model.addClassSpace(new ClassSpace(T01));
        model.switchToClassSpaceView(T01);
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567").withClassSpaces("T01").build());

        UnmarkCommand command = new UnmarkCommand(Index.fromOneBased(1), Optional.empty(), Optional.empty());
        assertThrows(CommandException.class, UnmarkCommand.MESSAGE_NO_ACTIVE_SESSION, () -> command.execute(model));
    }
}
