package seedu.address.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Person;
import seedu.address.model.person.Session;
import seedu.address.model.person.SessionList;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private static final DateTimeFormatter MATRIX_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH);
    private static final DateTimeFormatter MATRIX_TITLE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter COMMAND_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final int STUDENT_COLUMN_INDEX = 0;
    private static final int HEADER_ROW_INDEX = 0;
    private static final int FIRST_DATA_ROW_OFFSET = 1;
    private static final int COLUMN_INDEX_OFFSET = 1;
    private static final int EVEN_ROW_MODULUS = 2;
    private static final double STUDENT_COLUMN_WIDTH = 180;
    private static final double MATRIX_CELL_HEIGHT = 64;
    private final ObservableList<Person> allPersons;
    private final CommandBox.CommandExecutor commandExecutor;
    private final ReadOnlyObjectProperty<LocalDate> visibleSessionRangeStart;
    private final ReadOnlyObjectProperty<LocalDate> visibleSessionRangeEnd;
    private GroupName currentActiveGroupName;
    private LocalDate currentActiveSessionDate;
    private LocalDate currentVisibleRangeStart;
    private LocalDate currentVisibleRangeEnd;

    @FXML
    private StackPane attendanceMatrixContainer;

    @FXML
    private Label attendanceMatrixTitle;

    @FXML
    private Label attendanceMatrixSubtitle;

    @FXML
    private FlowPane attendanceMatrixMeta;

    @FXML
    private FlowPane attendanceMatrixLegend;

    @FXML
    private Label attendanceMatrixSummary;

    @FXML
    private Label attendanceMatrixScrollHint;

    @FXML
    private Button attendanceExportButton;

    @FXML
    private GridPane attendanceMatrixGrid;

    @FXML
    private Label attendanceMatrixEmptyState;

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList,
                           ObservableList<Person> allPersons,
                           ObservableList<Group> groups,
                           ReadOnlyBooleanProperty attendanceViewActive,
                           ReadOnlyObjectProperty<GroupName> activeGroupName,
                           ReadOnlyObjectProperty<LocalDate> activeSessionDate,
                           ReadOnlyObjectProperty<LocalDate> visibleSessionRangeStart,
                           ReadOnlyObjectProperty<LocalDate> visibleSessionRangeEnd,
                           CommandBox.CommandExecutor commandExecutor) {
        super(FXML);
        this.allPersons = allPersons;
        this.commandExecutor = commandExecutor;
        this.visibleSessionRangeStart = visibleSessionRangeStart;
        this.visibleSessionRangeEnd = visibleSessionRangeEnd;
        configureButtons();

        personListView.setItems(personList);
        personListView.setCellFactory(listView ->
                new PersonListViewCell(groups, attendanceViewActive, activeGroupName, activeSessionDate));

        attendanceViewActive.addListener((observable, oldValue, newValue) -> {
            personListView.refresh();
            refreshAttendanceMatrix(personList, attendanceViewActive.get(), activeGroupName.get(),
                    activeSessionDate.get());
        });
        activeGroupName.addListener((observable, oldValue, newValue) -> {
            personListView.refresh();
            refreshAttendanceMatrix(personList, attendanceViewActive.get(), newValue, activeSessionDate.get());
        });
        activeSessionDate.addListener((observable, oldValue, newValue) -> {
            personListView.refresh();
            refreshAttendanceMatrix(personList, attendanceViewActive.get(), activeGroupName.get(), newValue);
        });
        visibleSessionRangeStart.addListener((observable, oldValue, newValue) ->
                refreshAttendanceMatrix(personList, attendanceViewActive.get(),
                        activeGroupName.get(), activeSessionDate.get()));
        visibleSessionRangeEnd.addListener((observable, oldValue, newValue) ->
                refreshAttendanceMatrix(personList, attendanceViewActive.get(),
                        activeGroupName.get(), activeSessionDate.get()));
        groups.addListener((ListChangeListener<Group>) change -> personListView.refresh());
        personList.addListener((ListChangeListener<Person>) change -> refreshAttendanceMatrix(
                personList, attendanceViewActive.get(), activeGroupName.get(), activeSessionDate.get()));
        allPersons.addListener((ListChangeListener<Person>) change -> refreshAttendanceMatrix(
                personList, attendanceViewActive.get(), activeGroupName.get(), activeSessionDate.get()));

        refreshAttendanceMatrix(personList, attendanceViewActive.get(),
                activeGroupName.get(), activeSessionDate.get());
    }

    private void configureButtons() {
        attendanceExportButton.setOnAction(event -> executeUiCommand("exportview"));
    }

    private void refreshAttendanceMatrix(ObservableList<Person> personList,
                                         boolean isAttendanceViewActive,
                                         GroupName activeGroupName,
                                         LocalDate activeSessionDate) {
        boolean showMatrix = isAttendanceViewActive && activeGroupName != null;
        currentActiveGroupName = activeGroupName;
        currentActiveSessionDate = activeSessionDate;
        currentVisibleRangeStart = visibleSessionRangeStart.get();
        currentVisibleRangeEnd = visibleSessionRangeEnd.get();
        attendanceMatrixContainer.setManaged(showMatrix);
        attendanceMatrixContainer.setVisible(showMatrix);
        personListView.setManaged(!showMatrix);
        personListView.setVisible(!showMatrix);
        attendanceMatrixGrid.getChildren().clear();

        if (!showMatrix) {
            return;
        }

        List<LocalDate> sessionDates = getSessionDates(activeGroupName);

        LocalDate focusDate = activeSessionDate != null
                ? activeSessionDate
                : (sessionDates.isEmpty() ? null : sessionDates.get(0));

        String activeSessionNote = activeSessionDate == null
                ? ""
                : getSessionNote(activeGroupName, activeSessionDate);
        attendanceMatrixTitle.setText(buildMatrixTitle(activeGroupName, activeSessionDate));
        attendanceMatrixSubtitle.setText(buildMatrixSubtitle(activeSessionDate, activeSessionNote));
        populateMatrixMeta(personList.size(), sessionDates.size(), activeSessionDate, activeSessionNote);
        populateLegend();
        populateSummary(personList, activeGroupName, focusDate);
        attendanceMatrixScrollHint.setVisible(sessionDates.size() > 4);
        attendanceMatrixScrollHint.setManaged(sessionDates.size() > 4);

        if (sessionDates.isEmpty()) {
            attendanceMatrixGrid.setManaged(true);
            attendanceMatrixGrid.setVisible(true);
            attendanceMatrixEmptyState.setManaged(true);
            attendanceMatrixEmptyState.setVisible(true);
            addEmptyStateStudentRows(personList);
            return;
        }

        attendanceMatrixGrid.setManaged(true);
        attendanceMatrixGrid.setVisible(true);
        attendanceMatrixEmptyState.setManaged(false);
        attendanceMatrixEmptyState.setVisible(false);

        addHeaderRow(sessionDates, activeGroupName, activeSessionDate);
        addStudentRows(personList, activeGroupName, activeSessionDate, sessionDates);
    }

    private String buildMatrixTitle(GroupName activeGroupName, LocalDate activeSessionDate) {
        if (activeSessionDate == null) {
            return activeGroupName + " Attendance Overview";
        }
        return activeGroupName + " Attendance Overview - Highlighting "
                + activeSessionDate.format(MATRIX_TITLE_DATE_FORMATTER);
    }

    private String buildMatrixSubtitle(LocalDate activeSessionDate, String activeSessionNote) {
        if (activeSessionDate == null) {
            return "Semester view across all recorded sessions for the current class.";
        }
        if (!activeSessionNote.isBlank()) {
            return "Selected session note: " + activeSessionNote + ". Click another date header to refocus.";
        }
        return "Semester view with the selected session column highlighted. Click another date header to refocus.";
    }

    private List<LocalDate> getSessionDates(GroupName groupName) {
        return allPersons.stream()
                .filter(person -> person.hasGroup(groupName))
                .flatMap(person -> person.getGroupSessions()
                        .getOrDefault(groupName, new SessionList())
                        .getSessions()
                        .stream())
                .map(Session::getDate)
                .filter(date -> currentVisibleRangeStart == null || !date.isBefore(currentVisibleRangeStart))
                .filter(date -> currentVisibleRangeEnd == null || !date.isAfter(currentVisibleRangeEnd))
                .distinct()
                .sorted()
                .toList();
    }

    private void populateMatrixMeta(int studentCount, int sessionCount, LocalDate activeSessionDate,
                                    String activeSessionNote) {
        attendanceMatrixMeta.getChildren().clear();
        attendanceMatrixMeta.getChildren().add(createMetaChip(studentCount + " students"));
        attendanceMatrixMeta.getChildren().add(createMetaChip(sessionCount + " sessions"));
        attendanceMatrixMeta.getChildren().add(createMetaChip(
                activeSessionDate == null
                        ? "No date highlighted"
                        : "Highlight " + activeSessionDate.format(MATRIX_DATE_FORMATTER)));
        if (!activeSessionNote.isBlank()) {
            attendanceMatrixMeta.getChildren().add(createMetaChip("Note: " + activeSessionNote,
                    "attendance-matrix-note-chip"));
        }
        if (currentVisibleRangeStart != null || currentVisibleRangeEnd != null) {
            String rangeText = String.format("Range %s to %s",
                    currentVisibleRangeStart == null ? "start" : currentVisibleRangeStart.format(MATRIX_DATE_FORMATTER),
                    currentVisibleRangeEnd == null ? "end" : currentVisibleRangeEnd.format(MATRIX_DATE_FORMATTER));
            attendanceMatrixMeta.getChildren().add(createMetaChip(rangeText));
        }
    }

    private void populateLegend() {
        attendanceMatrixLegend.getChildren().clear();
        attendanceMatrixLegend.getChildren().add(createLegendChip("P Present", "attendance-matrix-status-present"));
        attendanceMatrixLegend.getChildren().add(createLegendChip("A Absent", "attendance-matrix-status-absent"));
        attendanceMatrixLegend.getChildren().add(createLegendChip("- Uninitialised",
                "attendance-matrix-status-uninitialised"));
    }

    private void populateSummary(ObservableList<Person> personList,
                                 GroupName activeGroupName,
                                 LocalDate focusDate) {
        if (focusDate == null) {
            attendanceMatrixSummary.setText("Create or add a session to see per-session attendance totals.");
            return;
        }

        int presentCount = 0;
        int absentCount = 0;
        int uninitialisedCount = 0;
        for (Person person : personList) {
            Attendance attendance = person.getAttendance(activeGroupName, focusDate);
            switch (attendance.value) {
            case PRESENT -> presentCount++;
            case ABSENT -> absentCount++;
            case UNINITIALISED -> uninitialisedCount++;
            default -> throw new IllegalStateException("Unexpected attendance status: " + attendance.value);
            }
        }

        String noteSuffix = buildSessionNoteSuffix(activeGroupName, focusDate);
        attendanceMatrixSummary.setText(String.format(
                "Summary for %s%s: %d present, %d absent, %d uninitialised.",
                focusDate.format(MATRIX_DATE_FORMATTER), noteSuffix, presentCount, absentCount, uninitialisedCount));
    }

    private void addHeaderRow(List<LocalDate> sessionDates,
                              GroupName activeGroupName,
                              LocalDate activeSessionDate) {
        attendanceMatrixGrid.add(createHeaderLabel("Student", true, null), STUDENT_COLUMN_INDEX, HEADER_ROW_INDEX);
        for (int columnIndex = 0; columnIndex < sessionDates.size(); columnIndex++) {
            LocalDate sessionDate = sessionDates.get(columnIndex);
            Label headerLabel = createHeaderLabel(sessionDate.format(MATRIX_DATE_FORMATTER), false, sessionDate);
            if (sessionDate.equals(activeSessionDate)) {
                headerLabel.getStyleClass().add("attendance-matrix-header-active");
            }
            attendanceMatrixGrid.add(headerLabel, columnIndex + COLUMN_INDEX_OFFSET, HEADER_ROW_INDEX);
        }
    }

    private void addStudentRows(ObservableList<Person> personList,
                                GroupName groupName,
                                LocalDate activeSessionDate,
                                List<LocalDate> sessionDates) {
        for (int rowIndex = 0; rowIndex < personList.size(); rowIndex++) {
            Person person = personList.get(rowIndex);
            String rowStyleClass = getRowStyleClass(rowIndex);

            Label studentLabel = createStudentLabel(person, rowIndex, rowStyleClass);
            attendanceMatrixGrid.add(studentLabel, STUDENT_COLUMN_INDEX, rowIndex + FIRST_DATA_ROW_OFFSET);

            for (int columnIndex = 0; columnIndex < sessionDates.size(); columnIndex++) {
                LocalDate sessionDate = sessionDates.get(columnIndex);
                VBox cell = createAttendanceCell(person, groupName, sessionDate,
                        sessionDate.equals(activeSessionDate), rowStyleClass);
                attendanceMatrixGrid.add(cell, columnIndex + COLUMN_INDEX_OFFSET, rowIndex + FIRST_DATA_ROW_OFFSET);
            }
        }
    }

    private void addEmptyStateStudentRows(ObservableList<Person> personList) {
        attendanceMatrixGrid.add(createHeaderLabel("Student", true, null), STUDENT_COLUMN_INDEX, HEADER_ROW_INDEX);
        for (int rowIndex = 0; rowIndex < personList.size(); rowIndex++) {
            Person person = personList.get(rowIndex);
            String rowStyleClass = getRowStyleClass(rowIndex);
            attendanceMatrixGrid.add(createStudentLabel(person, rowIndex, rowStyleClass),
                    STUDENT_COLUMN_INDEX, rowIndex + FIRST_DATA_ROW_OFFSET);
        }
    }

    private String getRowStyleClass(int rowIndex) {
        return rowIndex % EVEN_ROW_MODULUS == 0
                ? "attendance-matrix-row-even"
                : "attendance-matrix-row-odd";
    }

    private Label createStudentLabel(Person person, int rowIndex, String rowStyleClass) {
        Label studentLabel = new Label((rowIndex + 1) + ". " + person.getName().fullName);
        studentLabel.getStyleClass().addAll("attendance-matrix-student", rowStyleClass);
        studentLabel.setWrapText(true);
        studentLabel.setMinWidth(STUDENT_COLUMN_WIDTH);
        studentLabel.setPrefWidth(STUDENT_COLUMN_WIDTH);
        studentLabel.setMaxWidth(STUDENT_COLUMN_WIDTH);
        studentLabel.setMinHeight(MATRIX_CELL_HEIGHT);
        studentLabel.setPrefHeight(MATRIX_CELL_HEIGHT);
        studentLabel.setMaxHeight(MATRIX_CELL_HEIGHT);
        return studentLabel;
    }

    private Label createHeaderLabel(String text,
                                    boolean studentColumn,
                                    LocalDate sessionDate) {
        Label headerLabel = new Label(text);
        headerLabel.getStyleClass().add("attendance-matrix-header");
        if (studentColumn) {
            headerLabel.getStyleClass().add("attendance-matrix-header-student");
            headerLabel.setMinWidth(STUDENT_COLUMN_WIDTH);
            headerLabel.setPrefWidth(STUDENT_COLUMN_WIDTH);
            headerLabel.setMaxWidth(STUDENT_COLUMN_WIDTH);
        } else {
            headerLabel.setMinWidth(96);
            headerLabel.setPrefWidth(96);
            headerLabel.setMaxWidth(96);
            headerLabel.setWrapText(true);
            headerLabel.getStyleClass().add("attendance-matrix-header-clickable");
            String sessionNote = currentActiveGroupName == null
                    ? ""
                    : getSessionNote(currentActiveGroupName, sessionDate);
            String fullDateText = sessionDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    + ", " + sessionDate.format(COMMAND_DATE_FORMATTER)
                    + (sessionNote.isBlank() ? "" : "\nNote: " + sessionNote)
                    + "\nClick to focus this session";
            headerLabel.setTooltip(new Tooltip(fullDateText));
            headerLabel.setOnMouseClicked(event -> executeUiCommand(buildViewCommandForSessionDate(sessionDate)));
        }
        headerLabel.setPadding(new Insets(8));
        headerLabel.setMinHeight(MATRIX_CELL_HEIGHT);
        headerLabel.setPrefHeight(MATRIX_CELL_HEIGHT);
        headerLabel.setMaxHeight(MATRIX_CELL_HEIGHT);
        return headerLabel;
    }

    private Label createMetaChip(String text) {
        return createMetaChip(text, "attendance-matrix-meta-chip");
    }

    private Label createMetaChip(String text, String styleClass) {
        Label chip = new Label(text);
        chip.getStyleClass().add(styleClass);
        return chip;
    }

    private Label createLegendChip(String text, String statusStyleClass) {
        Label chip = new Label(text);
        chip.getStyleClass().addAll("attendance-matrix-legend-chip", statusStyleClass);
        return chip;
    }

    private VBox createAttendanceCell(Person person,
                                      GroupName groupName,
                                      LocalDate sessionDate,
                                      boolean isHighlightedDate,
                                      String rowStyleClass) {
        Attendance attendance = person.getAttendance(groupName, sessionDate);
        String attendanceShortLabel = switch (attendance.value) {
        case PRESENT -> "P";
        case ABSENT -> "A";
        case UNINITIALISED -> "-";
        };

        Label attendanceLabel = new Label(attendanceShortLabel);
        attendanceLabel.getStyleClass().addAll("attendance-matrix-cell-badge", attendanceStyleClass(attendance));

        Label participationLabel = new Label("PV " + person.getParticipation(groupName, sessionDate));
        participationLabel.getStyleClass().add("attendance-matrix-participation");

        VBox cell = new VBox(4, attendanceLabel, participationLabel);
        cell.getStyleClass().addAll("attendance-matrix-cell", rowStyleClass);
        if (isHighlightedDate) {
            cell.getStyleClass().add("attendance-matrix-cell-active");
        }
        cell.setPadding(new Insets(8));
        cell.setMinWidth(96);
        cell.setPrefWidth(96);
        cell.setMaxWidth(96);
        cell.setMinHeight(MATRIX_CELL_HEIGHT);
        cell.setPrefHeight(MATRIX_CELL_HEIGHT);
        cell.setMaxHeight(MATRIX_CELL_HEIGHT);
        Tooltip.install(cell, new Tooltip(buildCellTooltip(person, groupName, sessionDate, attendance)));
        return cell;
    }

    private String buildCellTooltip(Person person,
                                    GroupName groupName,
                                    LocalDate sessionDate,
                                    Attendance attendance) {
        return person.getName().fullName + "\n"
                + sessionDate.format(COMMAND_DATE_FORMATTER) + "\n"
                + "Attendance: " + attendance.value + "\n"
                + "Participation: " + person.getParticipation(groupName, sessionDate);
    }

    private String attendanceStyleClass(Attendance attendance) {
        return switch (attendance.value) {
        case PRESENT -> "attendance-matrix-status-present";
        case ABSENT -> "attendance-matrix-status-absent";
        case UNINITIALISED -> "attendance-matrix-status-uninitialised";
        };
    }

    private String buildSessionNoteSuffix(GroupName groupName, LocalDate sessionDate) {
        String sessionNote = getSessionNote(groupName, sessionDate);
        return sessionNote.isBlank() ? "" : " (" + sessionNote + ")";
    }

    private String getSessionNote(GroupName groupName, LocalDate sessionDate) {
        return allPersons.stream()
                .filter(person -> person.hasGroup(groupName))
                .map(person -> person.getSessionNote(groupName, sessionDate))
                .filter(note -> !note.isBlank())
                .findFirst()
                .orElse("");
    }

    private String buildViewCommandForSessionDate(LocalDate sessionDate) {
        StringBuilder commandBuilder = new StringBuilder("view d/")
                .append(sessionDate.format(COMMAND_DATE_FORMATTER));
        if (currentVisibleRangeStart != null) {
            commandBuilder.append(" from/")
                    .append(currentVisibleRangeStart.format(COMMAND_DATE_FORMATTER));
        }
        if (currentVisibleRangeEnd != null) {
            commandBuilder.append(" to/")
                    .append(currentVisibleRangeEnd.format(COMMAND_DATE_FORMATTER));
        }
        return commandBuilder.toString();
    }

    private void executeUiCommand(String commandText) {
        try {
            commandExecutor.execute(commandText);
        } catch (CommandException | ParseException e) {
            // MainWindow already updates the result display on failure.
        }
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        private final ObservableList<Group> groups;
        private final ReadOnlyBooleanProperty attendanceViewActive;
        private final ReadOnlyObjectProperty<GroupName> activeGroupName;
        private final ReadOnlyObjectProperty<LocalDate> activeSessionDate;

        PersonListViewCell(ObservableList<Group> groups,
                           ReadOnlyBooleanProperty attendanceViewActive,
                           ReadOnlyObjectProperty<GroupName> activeGroupName,
                           ReadOnlyObjectProperty<LocalDate> activeSessionDate) {
            this.groups = groups;
            this.attendanceViewActive = attendanceViewActive;
            this.activeGroupName = activeGroupName;
            this.activeSessionDate = activeSessionDate;
        }

        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1, attendanceViewActive.get(),
                        activeGroupName.get(), activeSessionDate.get(), groups).getRoot());
            }
        }
    }
}
