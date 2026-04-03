package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.MatricNumber;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code ViewCommand}.
 */
public class ViewCommandTest {
    private static final GroupName T01 = new GroupName("T01");
    private static final GroupName T02 = new GroupName("T02");
    private static final LocalDate SESSION_DATE = LocalDate.of(2026, 3, 16);

    @Test
    public void equals() {
        ViewCommand presentCommand = new ViewCommand(new Attendance("PRESENT"), SESSION_DATE);
        ViewCommand absentCommand = new ViewCommand(new Attendance("ABSENT"), SESSION_DATE);
        ViewCommand groupCommand = new ViewCommand(T01, SESSION_DATE);

        assertTrue(presentCommand.equals(presentCommand));
        assertTrue(presentCommand.equals(new ViewCommand(new Attendance("PRESENT"), SESSION_DATE)));
        assertTrue(groupCommand.equals(new ViewCommand(T01, SESSION_DATE)));
        assertFalse(presentCommand.equals(1));
        assertFalse(presentCommand.equals(null));
        assertFalse(presentCommand.equals(absentCommand));
    }

    @Test
    public void execute_presentFilter_showsMatchingPersons() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.addPerson(new PersonBuilder().withName("Alice Present").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567")
                .withSession("T01", SESSION_DATE.toString(), "PRESENT", 1).build());
        model.addPerson(new PersonBuilder().withName("Bob Absent").withMatricNumber("A1234568W")
                .withEmail("bob@example.com").withPhone("92345678")
                .withSession("T01", SESSION_DATE.toString(), "ABSENT", 0).build());
        model.addPerson(new PersonBuilder().withName("Cara Present").withMatricNumber("A1234569U")
                .withEmail("cara@example.com").withPhone("93456789")
                .withSession("T01", SESSION_DATE.toString(), "PRESENT", 2).build());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setActiveSessionDate(SESSION_DATE);
        Attendance attendance = new Attendance(Attendance.Status.PRESENT);
        expectedModel.setAttendanceViewActive(true);
        expectedModel.updateFilteredPersonList(person -> person.getAttendance(T01, SESSION_DATE).equals(attendance));

        ViewCommand command = new ViewCommand(attendance, SESSION_DATE);
        String expectedMessage = String.format(ViewCommand.MESSAGE_SUCCESS, 2, attendance, T01, SESSION_DATE);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(
                new PersonBuilder().withName("Alice Present").withMatricNumber("A1234567X")
                        .withEmail("alice@example.com").withPhone("91234567")
                        .withSession("T01", SESSION_DATE.toString(), "PRESENT", 1).build(),
                new PersonBuilder().withName("Cara Present").withMatricNumber("A1234569U")
                        .withEmail("cara@example.com").withPhone("93456789")
                        .withSession("T01", SESSION_DATE.toString(), "PRESENT", 2).build()
        ), model.getFilteredPersonList());
    }

    @Test
    public void execute_noFilter_showsCurrentView() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.setActiveSessionDate(SESSION_DATE);
        model.addPerson(new PersonBuilder().withName("Alice Present").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567")
                .withSession("T01", SESSION_DATE.toString(), "PRESENT", 0).build());
        model.addPerson(new PersonBuilder().withName("Bob Absent").withMatricNumber("A1234568W")
                .withEmail("bob@example.com").withPhone("92345678")
                .withSession("T01", SESSION_DATE.toString(), "ABSENT", 0).build());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setActiveSessionDate(SESSION_DATE);
        expectedModel.setAttendanceViewActive(true);
        expectedModel.updateFilteredPersonList(seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS);

        ViewCommand command = new ViewCommand();
        String expectedMessage = String.format(ViewCommand.MESSAGE_VIEW_SUCCESS, 2, T01, SESSION_DATE);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(2, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_groupView_showsWholeGroup() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.addGroup(new Group(T02));
        model.addPerson(new PersonBuilder().withName("Alice Present").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567")
                .withSession("T01", SESSION_DATE.toString(), "PRESENT", 1).build());
        model.addPerson(new PersonBuilder().withName("Bob Absent").withMatricNumber("A1234568W")
                .withEmail("bob@example.com").withPhone("92345678")
                .withSession("T01", SESSION_DATE.toString(), "ABSENT", 0).build());
        model.addPerson(new PersonBuilder().withName("Cara Elsewhere").withMatricNumber("A1234569U")
                .withEmail("cara@example.com").withPhone("93456789")
                .withSession("T02", SESSION_DATE.toString(), "PRESENT", 4).build());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setActiveSessionDate(SESSION_DATE);
        expectedModel.setAttendanceViewActive(true);
        expectedModel.updateFilteredPersonList(seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS);

        ViewCommand command = new ViewCommand(T01, SESSION_DATE);
        String expectedMessage = String.format(ViewCommand.MESSAGE_VIEW_SUCCESS, 2, T01, SESSION_DATE);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(2, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_missingGroup_throwsCommandException() {
        Model model = new ModelManager();
        ViewCommand command = new ViewCommand(new GroupName("Missing"), SESSION_DATE);
        assertThrows(CommandException.class, ViewCommand.MESSAGE_GROUP_NOT_FOUND, () -> command.execute(model));
    }

    @Test
    public void execute_noMatches_returnsNoMatchesMessage() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.addPerson(new PersonBuilder().withName("Only Present").withMatricNumber("A1234567X")
                .withEmail("present@example.com").withPhone("94567890")
                .withSession("T01", SESSION_DATE.toString(), "PRESENT", 0).build());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setActiveSessionDate(SESSION_DATE);
        Attendance attendance = new Attendance(Attendance.Status.ABSENT);
        expectedModel.setAttendanceViewActive(true);
        expectedModel.updateFilteredPersonList(person -> person.getAttendance(T01, SESSION_DATE).equals(attendance));

        ViewCommand command = new ViewCommand(attendance, SESSION_DATE);
        String expectedMessage = String.format(ViewCommand.MESSAGE_NO_MATCHES, attendance, T01, SESSION_DATE);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_withoutSessionContext_throwsCommandException() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);

        ViewCommand command = new ViewCommand(new Attendance("PRESENT"));
        assertThrows(CommandException.class, ViewCommand.MESSAGE_NO_ACTIVE_SESSION, () -> command.execute(model));
    }

    @Test
    public void execute_noFilterWithoutSessionContext_showsOverview() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567").withGroups("T01").build());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setAttendanceViewActive(true);
        expectedModel.updateFilteredPersonList(seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS);

        ViewCommand command = new ViewCommand();
        assertCommandSuccess(command, model,
                String.format(ViewCommand.MESSAGE_OVERVIEW_SUCCESS, 1, T01), expectedModel);
    }

    @Test
    public void executeNoFilter_missingSession_throwsCommandException() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        String matricNumber = "A1234567X";
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber(matricNumber)
                .withEmail("alice@example.com").withPhone("91234567").withGroups("T01").build());

        ViewCommand command = new ViewCommand(T01, SESSION_DATE);
        String expectedMessage = String.format(ViewCommand.MESSAGE_SESSION_NOT_CREATED, SESSION_DATE, T01);
        assertThrows(CommandException.class,
                expectedMessage, () -> command.execute(model));

        assertTrue(model.findPersonByMatricNumber(new MatricNumber(matricNumber))
                .orElseThrow()
                .getGroupSessions()
                .get(T01) == null);
    }

    @Test
    public void execute_refocusDate_preservesExistingVisibleRange() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.setVisibleSessionRange(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 4, 1));
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567")
                .withSession("T01", SESSION_DATE.toString(), "PRESENT", 0).build());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setVisibleSessionRange(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 4, 1));
        expectedModel.setActiveSessionDate(SESSION_DATE);
        expectedModel.setAttendanceViewActive(true);
        expectedModel.updateFilteredPersonList(seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS);

        ViewCommand command = new ViewCommand(SESSION_DATE);
        assertCommandSuccess(command, model,
                String.format(ViewCommand.MESSAGE_VIEW_SUCCESS, 1, T01, SESSION_DATE), expectedModel);
    }

    @Test
    public void execute_plainView_clearsExistingVisibleRange() {
        Model model = new ModelManager();
        model.addGroup(new Group(T01));
        model.switchToGroupView(T01);
        model.setVisibleSessionRange(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 4, 1));
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567").withGroups("T01").build());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.switchToGroupView(T01);
        expectedModel.setAttendanceViewActive(true);
        expectedModel.updateFilteredPersonList(seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS);

        ViewCommand command = new ViewCommand();
        assertCommandSuccess(command, model,
                String.format(ViewCommand.MESSAGE_OVERVIEW_SUCCESS, 1, T01), expectedModel);
        assertTrue(model.getVisibleSessionRangeStart().isEmpty());
        assertTrue(model.getVisibleSessionRangeEnd().isEmpty());
    }

    @Test
    public void toStringMethod() {
        Attendance attendance = new Attendance("ABSENT");
        ViewCommand command = new ViewCommand(attendance, SESSION_DATE);
        String expected = ViewCommand.class.getCanonicalName()
                + "{attendance=Optional[" + attendance + "], groupName=Optional.empty, "
                + "sessionDate=Optional[" + SESSION_DATE
                + "], rangeStartDate=Optional.empty, rangeEndDate=Optional.empty}";
        assertEquals(expected, command.toString());
    }
}
