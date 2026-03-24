package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.classspace.Group;
import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_CLASS_SPACE = "Group list contains duplicate group(s).";
    public static final String MESSAGE_INVALID_MATRICULATION_NUMBER = "Invalid matriculation number.";
    private static final Logger logger = LogsCenter.getLogger(JsonSerializableAddressBook.class);

    private final List<JsonNode> persons = new ArrayList<>();
    private final List<JsonNode> preservedSkippedPersons = new ArrayList<>();
    private final List<JsonNode> classSpaces = new ArrayList<>();
    private final List<JsonNode> preservedSkippedClassSpaces = new ArrayList<>();
    private final List<String> loadWarnings = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons and class spaces.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonNode> persons,
                                       @JsonProperty("classSpaces") List<JsonNode> classSpaces,
                                       @JsonProperty("preservedSkippedPersons") List<JsonNode> preservedSkippedPersons,
                                       @JsonProperty("preservedSkippedClassSpaces") List<JsonNode>
                                                   preservedSkippedClassSpaces,
                                       @JsonProperty("loadWarnings") List<String> loadWarnings) {
        if (persons != null) {
            this.persons.addAll(persons);
        }
        if (classSpaces != null) {
            this.classSpaces.addAll(classSpaces);
        }
        if (preservedSkippedPersons != null) {
            this.preservedSkippedPersons.addAll(preservedSkippedPersons);
        }
        if (preservedSkippedClassSpaces != null) {
            this.preservedSkippedClassSpaces.addAll(preservedSkippedClassSpaces);
        }
        if (loadWarnings != null) {
            this.loadWarnings.addAll(loadWarnings);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        this(source, List.of(), List.of(), List.of());
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use,
     * while preserving raw person entries that were skipped during loading.
     *
     * @param source Address book data to serialize.
     * @param preservedSkippedPersons Raw person JSON nodes that should be written back unchanged.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source, List<JsonNode> preservedSkippedPersons) {
        this(source, preservedSkippedPersons, List.of(), List.of());
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use,
     * while preserving raw person entries that were skipped during loading.
     *
     * @param source Address book data to serialize.
     * @param preservedSkippedPersons Raw person JSON nodes that should be written back unchanged.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source,
                                       List<JsonNode> preservedSkippedPersons,
                                       List<JsonNode> preservedSkippedClassSpaces,
                                       List<String> preservedLoadWarnings) {
        persons.addAll(source.getPersonList().stream()
                .map(JsonAdaptedPerson::new)
                .map(JsonUtil::toJsonNode)
                .collect(Collectors.toList()));
        if (preservedSkippedPersons != null) {
            for (JsonNode skippedPerson : preservedSkippedPersons) {
                this.preservedSkippedPersons.add(skippedPerson.deepCopy());
            }
        }
        classSpaces.addAll(source.getClassSpaceList().stream()
                .map(JsonAdaptedClassSpace::new)
                .map(JsonUtil::toJsonNode)
                .collect(Collectors.toList()));
        if (preservedSkippedClassSpaces != null) {
            for (JsonNode skippedClassSpace : preservedSkippedClassSpaces) {
                this.preservedSkippedClassSpaces.add(skippedClassSpace.deepCopy());
            }
        }
        if (preservedLoadWarnings != null) {
            this.loadWarnings.addAll(preservedLoadWarnings);
        }
    }

    /**
     * Returns an unmodifiable list of warnings accumulated during loading of app.
     *
     * @return A list of warnings describing issues with contacts during loading.
     */
    public List<String> getLoadWarnings() {
        return Collections.unmodifiableList(loadWarnings);
    }

    /**
     * Returns the raw skipped person entries that should be preserved on the next save.
     *
     * @return Unmodifiable list of skipped person JSON nodes.
     */
    public List<JsonNode> getPreservedSkippedPersons() {
        return Collections.unmodifiableList(preservedSkippedPersons);
    }

    /**
     * Returns the raw skipped class space entries that should be preserved on the next save.
     *
     * @return Unmodifiable list of skipped class space JSON nodes.
     */
    public List<JsonNode> getPreservedSkippedClassSpaces() {
        return Collections.unmodifiableList(preservedSkippedClassSpaces);
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        List<String> previousWarnings = new ArrayList<>(loadWarnings);
        loadWarnings.clear();

        logger.info("Loading address book: " + classSpaces.size() + " class space(s), "
                + persons.size() + " person(s)");

        loadClassSpaces(addressBook);
        loadPersons(addressBook);
        loadWarnings.addAll(0, previousWarnings);

        logger.info("Address book loaded: " + addressBook.getPersonList().size()
                + " person(s) loaded, " + loadWarnings.size() + " skipped");

        return addressBook;
    }

    private void loadPersons(AddressBook addressBook) {
        requireNonNull(addressBook);
        for (int i = 0; i < persons.size(); i++) {
            loadPerson(addressBook, persons.get(i), i);
        }
    }

    private void loadPerson(AddressBook addressBook, JsonNode rawPersonNode, int index) {
        requireNonNull(addressBook);
        requireNonNull(rawPersonNode);
        assert index >= 0 : "Person index should never be negative";

        try {
            JsonAdaptedPerson jsonAdaptedPerson = JsonUtil.fromJsonNode(rawPersonNode, JsonAdaptedPerson.class);
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                skipDuplicatePerson(rawPersonNode, person, index);
                return;
            }
            ensureClassSpacesExist(addressBook, person);
            validateAssignmentGrades(addressBook, person);
            validateClassSpaceSessions(addressBook, person);
            addressBook.addPerson(person);
        } catch (IllegalValueException | JsonProcessingException e) {
            skipInvalidPerson(rawPersonNode, index, e.getMessage());
        }
    }

    private void skipDuplicatePerson(JsonNode rawPersonNode, Person person, int index) {
        String identifier = person.getName().fullName + " (Matric: " + person.getMatricNumber().value + ")";
        logger.warning("Skipping duplicate contact at entry #" + (index + 1) + ": " + identifier);
        preservedSkippedPersons.add(rawPersonNode.deepCopy());
        loadWarnings.add("Skipped duplicate contact: " + identifier);
    }

    private void skipInvalidPerson(JsonNode rawPersonNode, int index, String errorMessage) {
        String identifier = getRawPersonIdentifier(rawPersonNode, index);
        String formattedWarning = formatInvalidContactWarning(identifier, errorMessage);
        logger.warning(formattedWarning);
        preservedSkippedPersons.add(rawPersonNode.deepCopy());
        loadWarnings.add(formattedWarning);
    }

    private void validateAssignmentGrades(AddressBook addressBook, Person person) throws IllegalValueException {
        for (var classSpaceEntry : person.getAssignmentGrades().entrySet()) {
            ClassSpaceName classSpaceName = classSpaceEntry.getKey();
            Map<AssignmentName, Integer> grades = classSpaceEntry.getValue();

            validatePersonIsMemberOfClassSpace(person, classSpaceName);
            /*
            This should never be reached as ensureClassSpacesExist guarantees the class space
            exists before this method is called. The orElseThrow is a defensive guard against
            future errors in the load sequence.
             */
            Group group = addressBook.getClassSpaceList().stream()
                    .filter(cs -> cs.getClassSpaceName().equals(classSpaceName))
                    .findFirst()
                    .orElseThrow(() ->
                            new AssertionError("Class space '" + classSpaceName.value
                                    + "' should exist after ensureClassSpacesExist"));
            validateGradesAgainstClassSpace(group, classSpaceName, grades);
        }
    }

    private void validatePersonIsMemberOfClassSpace(Person person, ClassSpaceName classSpaceName)
            throws IllegalValueException {
        if (!person.getClassSpaces().contains(classSpaceName)) {
            throw new IllegalValueException(String.format(
                    "Person has grades for class space '%s' but is not a member of it.",
                    classSpaceName.value));
        }
    }

    private void validateGradesAgainstClassSpace(Group group, ClassSpaceName classSpaceName,
                                                 Map<AssignmentName, Integer> grades) throws IllegalValueException {
        for (var gradeEntry : grades.entrySet()) {
            AssignmentName assignmentName = gradeEntry.getKey();
            int grade = gradeEntry.getValue();

            if (!group.hasAssignment(assignmentName)) {
                throw new IllegalValueException(String.format(
                        "Person has a grade for assignment '%s' in class space '%s',"
                                + "but that assignment does not exist.",
                        assignmentName.value, classSpaceName.value));
            }

            Assignment assignment = group.findAssignmentByName(assignmentName).get();
            if (grade > assignment.getMaxMarks()) {
                throw new IllegalValueException(String.format(
                        "Grade %d for assignment '%s' in class space '%s' exceeds max marks of %d.",
                        grade, assignmentName.value, classSpaceName.value, assignment.getMaxMarks()));
            }
        }
    }

    private void validateClassSpaceSessions(AddressBook addressBook, Person person) throws IllegalValueException {
        for (ClassSpaceName classSpaceName : person.getClassSpaceSessions().keySet()) {
            if (!person.getClassSpaces().contains(classSpaceName)) {
                throw new IllegalValueException(String.format(
                        "Person has sessions for class space '%s' but is not a member of it.",
                        classSpaceName.value));
            }
        }
    }

    private String getRawPersonIdentifier(JsonNode rawPersonNode, int index) {
        JsonNode nameNode = rawPersonNode.get("name");
        if (nameNode != null && !nameNode.isNull()) {
            return "'" + nameNode.asText() + "'";
        }
        return "entry #" + (index + 1) + " (missing name)";
    }

    private void ensureClassSpacesExist(AddressBook addressBook, Person person) {
        for (var classSpaceName : person.getClassSpaces()) {
            Group group = new Group(classSpaceName);
            if (!addressBook.hasClassSpace(group)) {
                addressBook.addClassSpace(group);
            }
        }
    }

    private void loadClassSpaces(AddressBook addressBook) {
        requireNonNull(addressBook);
        for (int i = 0; i < classSpaces.size(); i++) {
            loadClassSpace(addressBook, classSpaces.get(i), i);
        }
    }

    private void loadClassSpace(AddressBook addressBook, JsonNode rawClassSpaceNode, int index) {
        requireNonNull(addressBook);
        requireNonNull(rawClassSpaceNode);
        assert index >= 0 : "Class space index should never be negative";

        try {
            JsonAdaptedClassSpace jsonAdaptedClassSpace =
                    JsonUtil.fromJsonNode(rawClassSpaceNode, JsonAdaptedClassSpace.class);
            Group group = jsonAdaptedClassSpace.toModelType();

            if (addressBook.hasClassSpace(group)) {
                String identifier = "'" + group.getClassSpaceName().value + "'";
                logger.warning("Skipping duplicate class space at entry #" + (index + 1) + ": " + identifier);
                preservedSkippedClassSpaces.add(rawClassSpaceNode.deepCopy());
                loadWarnings.add("Skipped duplicate class space: " + identifier);
                return;
            }

            addressBook.addClassSpace(group);
        } catch (IllegalValueException | JsonProcessingException e) {
            String identifier = getRawClassSpaceIdentifier(rawClassSpaceNode, index);
            String formattedWarning = formatInvalidClassSpaceWarning(identifier, e.getMessage());
            logger.warning(formattedWarning);
            preservedSkippedClassSpaces.add(rawClassSpaceNode.deepCopy());
            loadWarnings.add(formattedWarning);
        }
    }

    private String getRawClassSpaceIdentifier(JsonNode rawClassSpaceNode, int index) {
        JsonNode nameNode = rawClassSpaceNode.get("name");
        if (nameNode != null && !nameNode.isNull()) {
            return "'" + nameNode.asText() + "'";
        }
        return "entry #" + (index + 1) + " (missing name)";
    }


    private String formatInvalidContactWarning(String identifier, String errorMessage) {
        String[] errors = errorMessage.split(";\\s*");

        StringBuilder sb = new StringBuilder("Skipped invalid contact ")
                .append(identifier)
                .append(":\n");

        for (String error : errors) {
            sb.append("- ").append(error).append("\n");
        }

        return sb.toString().trim();
    }

    private String formatInvalidClassSpaceWarning(String identifier, String errorMessage) {
        String[] errors = errorMessage.split(";\\s*");

        StringBuilder sb = new StringBuilder("Skipped invalid class space ")
                .append(identifier)
                .append(":\n");

        for (String error : errors) {
            sb.append("- ").append(error).append("\n");
        }

        return sb.toString().trim();
    }

}
