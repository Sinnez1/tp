package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import seedu.address.model.classspace.ClassSpace;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Participation;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;
import seedu.address.testutil.PersonBuilder;

public class PartCommandTest {

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

        Participation newParticipation = new Participation(4);
        Session updatedSession = new Session(SESSION_DATE, originalPerson.getAttendance(T01, SESSION_DATE),
                newParticipation);
        Person updatedPerson = originalPerson.withUpdatedSession(T01, updatedSession);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToClassSpaceView(T01);
        expectedModel.setPerson(originalPerson, updatedPerson);
        expectedModel.setActiveSessionDate(SESSION_DATE);

        PartCommand command = new PartCommand(Index.fromOneBased(1), Optional.empty(), Optional.empty(),
                newParticipation);
        String expectedMessage = String.format(PartCommand.MESSAGE_PARTICIPATION_SUCCESS,
                Messages.format(updatedPerson, T01, SESSION_DATE));

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

        PartCommand command = new PartCommand(Index.fromOneBased(1), Optional.empty(), Optional.empty(),
                new Participation(3));
        assertThrows(CommandException.class, PartCommand.MESSAGE_NO_ACTIVE_SESSION, () -> command.execute(model));
    }
}
