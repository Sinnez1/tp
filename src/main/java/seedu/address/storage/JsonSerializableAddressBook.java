package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import seedu.address.model.classspace.ClassSpace;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {
    public static final String MESSAGE_DUPLICATE_CLASS_SPACE = "Class space list contains duplicate class space(s).";
    private static final Logger logger = LogsCenter.getLogger(JsonSerializableAddressBook.class);

    private final List<JsonNode> persons = new ArrayList<>();
    private final List<JsonNode> preservedSkippedPersons = new ArrayList<>();
    private final List<JsonAdaptedClassSpace> classSpaces = new ArrayList<>();
    private final List<String> loadWarnings = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons and class spaces.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonNode> persons,
            @JsonProperty("classSpaces") List<JsonAdaptedClassSpace> classSpaces) {
        if (persons != null) {
            this.persons.addAll(persons);
        }
        if (classSpaces != null) {
            this.classSpaces.addAll(classSpaces);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        this(source, List.of());
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use,
     * while preserving raw person entries that were skipped during loading.
     *
     * @param source Address book data to serialize.
     * @param preservedSkippedPersons Raw person JSON nodes that should be written back unchanged.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source, List<JsonNode> preservedSkippedPersons) {
        persons.addAll(source.getPersonList().stream()
                .map(JsonAdaptedPerson::new)
                .map(JsonUtil::toJsonNode)
                .collect(Collectors.toList()));
        if (preservedSkippedPersons != null) {
            for (JsonNode skippedPerson : preservedSkippedPersons) {
                persons.add(skippedPerson.deepCopy());
            }
        }
        classSpaces.addAll(source.getClassSpaceList().stream()
                .map(JsonAdaptedClassSpace::new)
                .collect(Collectors.toList()));
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
     * @return Unmodifiable list of deep-copied skipped person JSON nodes.
     */
    public List<JsonNode> getPreservedSkippedPersons() {
        List<JsonNode> copies = new ArrayList<>();
        for (JsonNode skippedPerson : preservedSkippedPersons) {
            copies.add(skippedPerson.deepCopy());
        }
        return Collections.unmodifiableList(copies);
    }
    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        loadWarnings.clear();
        preservedSkippedPersons.clear();

        logger.info("Loading address book: " + classSpaces.size() + " class space(s), "
                + persons.size() + " person(s)");

        loadClassSpaces(addressBook);
        loadPersons(addressBook);

        logger.info("Address book loaded: " + addressBook.getPersonList().size()
                + " person(s) loaded, " + loadWarnings.size() + " skipped");

        return addressBook;
    }

    private void loadPersons(AddressBook addressBook) {
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
                String identifier = person.getName().fullName + " (Matric: " + person.getMatricNumber().value + ")";
                logger.warning("Skipping duplicate contact at entry #" + (index + 1) + ": " + identifier);
                preservedSkippedPersons.add(rawPersonNode.deepCopy());
                loadWarnings.add("Skipped duplicate contact: " + identifier);
                return;
            }
            ensureClassSpacesExist(addressBook, person);
            addressBook.addPerson(person);
        } catch (IllegalValueException | JsonProcessingException e) {
            String identifier = getRawPersonIdentifier(rawPersonNode, index);
            logger.warning("Skipping invalid contact " + identifier + ": " + e.getMessage());
            preservedSkippedPersons.add(rawPersonNode.deepCopy());
            loadWarnings.add("Skipped invalid contact " + identifier + ": " + e.getMessage());
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
            ClassSpace classSpace = new ClassSpace(classSpaceName);
            if (!addressBook.hasClassSpace(classSpace)) {
                addressBook.addClassSpace(classSpace);
            }
        }
    }

    private void loadClassSpaces(AddressBook addressBook) throws IllegalValueException {
        for (JsonAdaptedClassSpace jsonAdaptedClassSpace : classSpaces) {
            ClassSpace classSpace = jsonAdaptedClassSpace.toModelType();
            if (addressBook.hasClassSpace(classSpace)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_CLASS_SPACE);
            }
            addressBook.addClassSpace(classSpace);
        }
    }

    private static List<JsonNode> copyJsonNodes(List<JsonNode> nodes) {
        List<JsonNode> copies = new ArrayList<>();
        for (JsonNode node : nodes) {
            copies.add(node.deepCopy());
        }
        return copies;
    }
}
