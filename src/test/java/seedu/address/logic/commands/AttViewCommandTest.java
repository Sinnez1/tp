package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classspace.ClassSpace;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Attendance;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AttViewCommand}.
 */
public class AttViewCommandTest {

    @Test
    public void equals() {
        AttViewCommand presentCommand = new AttViewCommand(new Attendance("PRESENT"));
        AttViewCommand absentCommand = new AttViewCommand(new Attendance("ABSENT"));
        AttViewCommand groupCommand = new AttViewCommand(new ClassSpaceName("T01"));

        assertTrue(presentCommand.equals(presentCommand));
        assertTrue(presentCommand.equals(new AttViewCommand(new Attendance("PRESENT"))));
        assertTrue(groupCommand.equals(new AttViewCommand(new ClassSpaceName("T01"))));
        assertFalse(presentCommand.equals(1));
        assertFalse(presentCommand.equals(null));
        assertFalse(presentCommand.equals(absentCommand));
    }

    @Test
    public void execute_presentFilter_showsMatchingPersons() {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Alice Present").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567").withAttendance("PRESENT").build());
        model.addPerson(new PersonBuilder().withName("Bob Absent").withMatricNumber("A1234568W")
                .withEmail("bob@example.com").withPhone("92345678").withAttendance("ABSENT").build());
        model.addPerson(new PersonBuilder().withName("Cara Present").withMatricNumber("A1234569U")
                .withEmail("cara@example.com").withPhone("93456789").withAttendance("PRESENT").build());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Attendance attendance = new Attendance(Attendance.Status.PRESENT);
        expectedModel.setAttendanceViewActive(true);
        expectedModel.updateFilteredPersonList(person -> person.getAttendance().equals(attendance));

        AttViewCommand command = new AttViewCommand(attendance);
        String expectedMessage = String.format(AttViewCommand.MESSAGE_SUCCESS, 2, attendance);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(
                new PersonBuilder().withName("Alice Present").withMatricNumber("A1234567X")
                        .withEmail("alice@example.com").withPhone("91234567").withAttendance("PRESENT").build(),
                new PersonBuilder().withName("Cara Present").withMatricNumber("A1234569U")
                        .withEmail("cara@example.com").withPhone("93456789").withAttendance("PRESENT").build()
        ), model.getFilteredPersonList());
    }

    @Test
    public void execute_noFilter_showsCurrentView() {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Alice Present").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567").withAttendance("PRESENT").build());
        model.addPerson(new PersonBuilder().withName("Bob Absent").withMatricNumber("A1234568W")
                .withEmail("bob@example.com").withPhone("92345678").withAttendance("ABSENT").build());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setAttendanceViewActive(true);
        expectedModel.updateFilteredPersonList(seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS);

        AttViewCommand command = new AttViewCommand();
        String expectedMessage = String.format(AttViewCommand.MESSAGE_VIEW_SUCCESS, 2);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(2, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_groupView_showsWholeGroup() {
        Model model = new ModelManager();
        model.addClassSpace(new ClassSpace(new ClassSpaceName("T01")));
        model.addClassSpace(new ClassSpace(new ClassSpaceName("T02")));
        model.addPerson(new PersonBuilder().withName("Alice Present").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567").withAttendance("PRESENT")
                .withClassSpaces("T01").build());
        model.addPerson(new PersonBuilder().withName("Bob Absent").withMatricNumber("A1234568W")
                .withEmail("bob@example.com").withPhone("92345678").withAttendance("ABSENT")
                .withClassSpaces("T01").build());
        model.addPerson(new PersonBuilder().withName("Cara Elsewhere").withMatricNumber("A1234569U")
                .withEmail("cara@example.com").withPhone("93456789").withAttendance("PRESENT")
                .withClassSpaces("T02").build());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToClassSpaceView(new ClassSpaceName("T01"));
        expectedModel.setAttendanceViewActive(true);
        expectedModel.updateFilteredPersonList(seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS);

        AttViewCommand command = new AttViewCommand(new ClassSpaceName("T01"));
        String expectedMessage = String.format(AttViewCommand.MESSAGE_VIEW_SUCCESS, 2);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(2, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_missingGroup_throwsCommandException() {
        Model model = new ModelManager();
        AttViewCommand command = new AttViewCommand(new ClassSpaceName("Missing"));
        assertThrows(CommandException.class, AttViewCommand.MESSAGE_GROUP_NOT_FOUND, () -> command.execute(model));
    }

    @Test
    public void execute_noMatches_returnsNoMatchesMessage() {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Only Present").withMatricNumber("A1234567X")
                .withEmail("present@example.com").withPhone("94567890").withAttendance("PRESENT").build());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Attendance attendance = new Attendance(Attendance.Status.ABSENT);
        expectedModel.setAttendanceViewActive(true);
        expectedModel.updateFilteredPersonList(person -> person.getAttendance().equals(attendance));

        AttViewCommand command = new AttViewCommand(attendance);
        String expectedMessage = String.format(AttViewCommand.MESSAGE_NO_MATCHES, attendance);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        Attendance attendance = new Attendance("ABSENT");
        AttViewCommand command = new AttViewCommand(attendance);
        String expected = AttViewCommand.class.getCanonicalName()
                + "{attendance=Optional[" + attendance + "], classSpaceName=Optional.empty}";
        assertEquals(expected, command.toString());
    }
}
