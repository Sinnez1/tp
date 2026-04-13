package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonListPanelTest {

    @Test
    public void spansMultipleYears_singleYear_returnsFalse() {
        List<LocalDate> sessionDates = List.of(
                LocalDate.of(2026, 1, 12),
                LocalDate.of(2026, 3, 16),
                LocalDate.of(2026, 9, 1));

        assertFalse(PersonListPanel.spansMultipleYears(sessionDates));
    }

    @Test
    public void formatMatrixHeaderDate_crossYear_omitsYearFromIndividualDateHeaders() {
        List<LocalDate> sessionDates = List.of(
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2026, 1, 12),
                LocalDate.of(2026, 2, 9));

        assertEquals("10 Nov", PersonListPanel.formatMatrixHeaderDate(sessionDates, 0));
        assertEquals("01 Dec", PersonListPanel.formatMatrixHeaderDate(sessionDates, 1));
        assertEquals("12 Jan", PersonListPanel.formatMatrixHeaderDate(sessionDates, 2));
        assertEquals("09 Feb", PersonListPanel.formatMatrixHeaderDate(sessionDates, 3));
    }

    @Test
    public void buildYearHeaderGroups_crossYear_returnsGroupedSpans() {
        List<LocalDate> sessionDates = List.of(
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2026, 1, 12),
                LocalDate.of(2026, 2, 9),
                LocalDate.of(2027, 3, 3));

        List<PersonListPanel.YearHeaderGroup> groups = PersonListPanel.buildYearHeaderGroups(sessionDates);

        assertEquals(3, groups.size());
        assertEquals(new PersonListPanel.YearHeaderGroup(2025, 0, 2), groups.get(0));
        assertEquals(new PersonListPanel.YearHeaderGroup(2026, 2, 2), groups.get(1));
        assertEquals(new PersonListPanel.YearHeaderGroup(2027, 4, 1), groups.get(2));
    }

    @Test
    public void yearToneStyleClass_sameYearAcrossFilteredViews_returnsSameColorClass() {
        String colorFor2026FullView = PersonListPanel.yearToneStyleClass(2026);
        String colorFor2026FilteredView = PersonListPanel.yearToneStyleClass(2026);
        String colorFor2025 = PersonListPanel.yearToneStyleClass(2025);

        assertEquals(colorFor2026FullView, colorFor2026FilteredView);
        assertFalse(colorFor2026FullView.isBlank());
        assertFalse(colorFor2025.isBlank());
    }

    @Test
    public void formatMatrixStudentLabel_includesMatricNumberOnNextLine() {
        String label = PersonListPanel.formatMatrixStudentLabel(
                new PersonBuilder().withName("Alex Tan").withMatricNumber("A1234567X").build(), 0);

        assertEquals("1. Alex Tan" + System.lineSeparator() + "A1234567X", label);
    }

    @Test
    public void formatMatrixStudentLabel_secondRowStillIncludesMatricNumber() {
        String label = PersonListPanel.formatMatrixStudentLabel(
                new PersonBuilder().withName("Jamie Lim").withMatricNumber("A1111111M").build(), 1);

        assertEquals("2. Jamie Lim" + System.lineSeparator() + "A1111111M", label);
    }
}
