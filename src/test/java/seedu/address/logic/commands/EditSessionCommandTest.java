package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.MatricNumber;
import seedu.address.testutil.PersonBuilder;

public class EditSessionCommandTest {
    private static final GroupName T01 = new GroupName("T01");
    private static final LocalDate ORIGINAL_DATE = LocalDate.of(2026, 3, 16);
    private static final LocalDate NEW_DATE = LocalDate.of(2026, 3, 23);

    @Test
    public void execute_movesSessionDate() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.setActiveSessionDate(ORIGINAL_DATE);
        Group group = model.findGroupByName(T01).orElseThrow();
        model.setGroup(group, group.withUpdatedSession(new seedu.address.model.person.Session(ORIGINAL_DATE,
                new seedu.address.model.person.Attendance("PRESENT"),
                new seedu.address.model.person.Participation(2))));
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567")
                .withSession("T01", ORIGINAL_DATE.toString(), "PRESENT", 2).build());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        Group originalGroup = expectedModel.findGroupByName(T01).orElseThrow();
        expectedModel.setGroup(originalGroup, originalGroup.withoutSession(ORIGINAL_DATE)
                .withUpdatedSession(new seedu.address.model.person.Session(NEW_DATE,
                        new seedu.address.model.person.Attendance("PRESENT"),
                        new seedu.address.model.person.Participation(2))));
        var person = expectedModel.findPersonByMatricNumber(new MatricNumber("A1234567X")).orElseThrow();
        var updatedPerson = person.withoutSession(T01, ORIGINAL_DATE)
                .withUpdatedSession(T01, new seedu.address.model.person.Session(NEW_DATE,
                        person.getAttendance(T01, ORIGINAL_DATE), person.getParticipation(T01, ORIGINAL_DATE)));
        expectedModel.setPerson(person, updatedPerson);
        expectedModel.setActiveSessionDate(NEW_DATE);

        EditSessionCommand command = new EditSessionCommand(ORIGINAL_DATE, NEW_DATE);
        assertCommandSuccess(command, model,
                String.format(EditSessionCommand.MESSAGE_SUCCESS, ORIGINAL_DATE + " -> " + NEW_DATE, T01),
                expectedModel);
    }

    @Test
    public void execute_missingSession_throwsCommandException() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);

        EditSessionCommand command = new EditSessionCommand(ORIGINAL_DATE, NEW_DATE);
        String expectedMessage = String.format(EditSessionCommand.MESSAGE_SESSION_NOT_FOUND, ORIGINAL_DATE, T01);
        assertThrows(CommandException.class, expectedMessage, () -> command.execute(model));
    }

    @Test
    public void execute_sameSessionDate_failure() {
        Model model = new ModelManager();
        Group group = new Group(T01).withUpdatedSession(new seedu.address.model.person.Session(ORIGINAL_DATE,
                new seedu.address.model.person.Attendance("PRESENT"),
                new seedu.address.model.person.Participation(2)));
        model.addGroup(group);
        model.switchToGroupView(T01);

        EditSessionCommand command = new EditSessionCommand(ORIGINAL_DATE, ORIGINAL_DATE);
        assertThrows(CommandException.class, Command.MESSAGE_NOTHING_CHANGED, () -> command.execute(model));
    }

    @Test
    public void execute_withSpecifiedGroupFromAllStudentsView_retainsCurrentView() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567")
                .withSession("T01", ORIGINAL_DATE.toString(), "PRESENT", 2).build());
        Group group = model.findGroupByName(T01).orElseThrow();
        model.setGroup(group, group.withUpdatedSession(new seedu.address.model.person.Session(ORIGINAL_DATE,
                new seedu.address.model.person.Attendance("PRESENT"),
                new seedu.address.model.person.Participation(2))));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Group originalGroup = expectedModel.findGroupByName(T01).orElseThrow();
        expectedModel.setGroup(originalGroup, originalGroup.withoutSession(ORIGINAL_DATE)
                .withUpdatedSession(new seedu.address.model.person.Session(NEW_DATE,
                        new seedu.address.model.person.Attendance("PRESENT"),
                        new seedu.address.model.person.Participation(2))));
        var person = expectedModel.findPersonByMatricNumber(new MatricNumber("A1234567X")).orElseThrow();
        var updatedPerson = person.withoutSession(T01, ORIGINAL_DATE)
                .withUpdatedSession(T01, new seedu.address.model.person.Session(NEW_DATE,
                        person.getAttendance(T01, ORIGINAL_DATE), person.getParticipation(T01, ORIGINAL_DATE)));
        expectedModel.setPerson(person, updatedPerson);

        EditSessionCommand command = new EditSessionCommand(ORIGINAL_DATE, NEW_DATE, T01);
        assertCommandSuccess(command, model,
                String.format(EditSessionCommand.MESSAGE_SUCCESS, ORIGINAL_DATE + " -> " + NEW_DATE, T01),
                expectedModel);
        assertTrue(model.getActiveGroupName().isEmpty());
    }

    @Test
    public void toStringMethod() {
        EditSessionCommand command = new EditSessionCommand(ORIGINAL_DATE, NEW_DATE, T01);
        String expected = EditSessionCommand.class.getCanonicalName()
                + "{originalDate=" + ORIGINAL_DATE + ", newDate=Optional[" + NEW_DATE
                + "], newNote=Optional.empty, groupName=Optional[" + T01 + "]}";
        assertEquals(expected, command.toString());
    }

    @Test
    public void execute_movesGroupLevelSessionWithoutStudents() {
        Model model = new ModelManager();
        Group group = new Group(T01).withUpdatedSession(new seedu.address.model.person.Session(ORIGINAL_DATE,
                new seedu.address.model.person.Attendance("UNINITIALISED"),
                new seedu.address.model.person.Participation(0),
                "tutorial"));
        model.addGroup(group);
        model.switchToGroupView(T01);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        Group originalGroup = expectedModel.findGroupByName(T01).orElseThrow();
        expectedModel.setGroup(originalGroup, originalGroup.withoutSession(ORIGINAL_DATE)
                .withUpdatedSession(new seedu.address.model.person.Session(NEW_DATE,
                        new seedu.address.model.person.Attendance("UNINITIALISED"),
                        new seedu.address.model.person.Participation(0),
                        "tutorial")));

        EditSessionCommand command = new EditSessionCommand(ORIGINAL_DATE, NEW_DATE);
        assertCommandSuccess(command, model,
                String.format(EditSessionCommand.MESSAGE_SUCCESS, ORIGINAL_DATE + " -> " + NEW_DATE, T01),
                expectedModel);
    }
}
