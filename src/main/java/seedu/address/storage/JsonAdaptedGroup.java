package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;

/**
 * Jackson-friendly version of {@link Group}.
 */
// @@author ongrussell
class JsonAdaptedGroup {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Group's %s field is missing!";

    private final String name;
    private final List<JsonAdaptedAssignment> assignments = new ArrayList<>();

    @JsonCreator
    public JsonAdaptedGroup(@JsonProperty("name") String name,
                            @JsonProperty("assignments") List<JsonAdaptedAssignment> assignments) {
        this.name = name;
        if (assignments != null) {
            this.assignments.addAll(assignments);
        }
    }

    public JsonAdaptedGroup(Group source) {
        name = source.getGroupName().value;
        assignments.addAll(source.getAssignments().stream().map(JsonAdaptedAssignment::new).toList());
    }

    public Group toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    GroupName.class.getSimpleName()));
        }
        if (!GroupName.isValidGroupName(name)) {
            throw new IllegalValueException(GroupName.MESSAGE_CONSTRAINTS);
        }

        List<Assignment> modelAssignments = new ArrayList<>();
        for (JsonAdaptedAssignment assignment : assignments) {
            modelAssignments.add(assignment.toModelType());
        }
        return new Group(new GroupName(name), modelAssignments);
    }
}
// @@author
