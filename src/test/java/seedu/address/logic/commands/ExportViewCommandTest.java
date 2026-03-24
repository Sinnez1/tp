package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.classspace.Group;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.testutil.PersonBuilder;

public class ExportViewCommandTest {
    private static final ClassSpaceName T01 = new ClassSpaceName("T01");

    @Test
    public void execute_exportsCsv() throws Exception {
        Model model = new ModelManager();
        model.addClassSpace(new Group(T01));
        model.switchToClassSpaceView(T01);
        model.addPerson(new PersonBuilder().withName("Alice").withMatricNumber("A1234567X")
                .withEmail("alice@example.com").withPhone("91234567")
                .withSession("T01", LocalDate.of(2026, 3, 16).toString(), "PRESENT", 1).build());

        Path output = Path.of("build", "tmp", "export-view-test.csv");
        ExportViewCommand command = new ExportViewCommand(output.toString());
        command.execute(model);

        assertTrue(Files.exists(output));
        assertTrue(Files.readString(output).contains("Student"));
        assertTrue(Files.readString(output).contains("Alice"));
    }

    @Test
    public void execute_withoutActiveGroup_throwsCommandException() {
        Model model = new ModelManager();
        ExportViewCommand command = new ExportViewCommand();
        assertThrows(CommandException.class, ExportViewCommand.MESSAGE_NO_ACTIVE_CLASS_SPACE, () -> {
            command.execute(model);
        });
    }
}
