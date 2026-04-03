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
import seedu.address.model.group.Group;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "taa")
class JsonSerializableAddressBook {
    private static final Logger logger = LogsCenter.getLogger(JsonSerializableAddressBook.class);

    private final List<JsonNode> persons = new ArrayList<>();
    private final List<JsonNode> preservedSkippedPersons = new ArrayList<>();
    private final List<JsonNode> groups = new ArrayList<>();
    private final List<JsonNode> preservedSkippedGroups = new ArrayList<>();
    private final List<String> loadWarnings = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons and groups.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonNode> persons,
                                       @JsonProperty("groups") List<JsonNode> groups,
                                       @JsonProperty("preservedSkippedPersons") List<JsonNode> preservedSkippedPersons,
                                       @JsonProperty("preservedSkippedGroups") List<JsonNode>
                                                   preservedSkippedGroups,
                                       @JsonProperty("loadWarnings") List<String> loadWarnings) {
        if (persons != null) {
            this.persons.addAll(persons);
        }
        if (groups != null) {
            this.groups.addAll(groups);
        }
        if (preservedSkippedPersons != null) {
            this.preservedSkippedPersons.addAll(preservedSkippedPersons);
        }
        if (preservedSkippedGroups != null) {
            this.preservedSkippedGroups.addAll(preservedSkippedGroups);
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
     * @param preservedSkippedGroups Raw group JSON nodes that should be written back unchanged.
     * @param loadWarnings Load warning issues to be added into JSON.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source,
                                       List<JsonNode> preservedSkippedPersons,
                                       List<JsonNode> preservedSkippedGroups,
                                       List<String> loadWarnings) {
        persons.addAll(source.getPersonList().stream()
                .map(JsonAdaptedPerson::new)
                .map(JsonUtil::toJsonNode)
                .collect(Collectors.toList()));
        addDeepCopies(this.preservedSkippedPersons, preservedSkippedPersons);
        groups.addAll(source.getGroupList().stream()
                .map(JsonAdaptedGroup::new)
                .map(JsonUtil::toJsonNode)
                .collect(Collectors.toList()));
        addDeepCopies(this.preservedSkippedGroups, preservedSkippedGroups);
        if (loadWarnings != null) {
            this.loadWarnings.addAll(loadWarnings);
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
     * Returns the raw skipped group entries that should be preserved on the next save.
     *
     * @return Unmodifiable list of skipped group JSON nodes.
     */
    public List<JsonNode> getPreservedSkippedGroups() {
        return Collections.unmodifiableList(preservedSkippedGroups);
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        loadWarnings.clear();

        // previouslySkippedPersons and previouslySkippedGroups are like snapshots of preserved entries of previous
        // load. Meant to prevent duplicate warnings by not validating fresh entries.
        List<JsonNode> previouslySkippedPersons = new ArrayList<>(preservedSkippedPersons);
        List<JsonNode> previouslySkippedGroups = new ArrayList<>(preservedSkippedGroups);
        preservedSkippedPersons.clear();
        preservedSkippedGroups.clear();

        logger.info("Loading address book: " + groups.size() + " group(s), "
                + persons.size() + " person(s), "
                + previouslySkippedGroups.size() + " preserved-skipped group(s), "
                + previouslySkippedPersons.size() + " preserved-skipped person(s)");

        loadGroups(addressBook);
        revalidatePreservedGroups(addressBook, previouslySkippedGroups);
        loadPersons(addressBook);
        revalidatePreservedPersons(addressBook, previouslySkippedPersons);

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
            PersonLoadValidator.validateGroupsExist(addressBook, person);
            PersonLoadValidator.validateAssignmentGrades(addressBook, person);
            PersonLoadValidator.validateGroupSessions(person);
            addressBook.addPerson(person);
        } catch (IllegalValueException | JsonProcessingException e) {
            skipInvalidPerson(rawPersonNode, index, e.getMessage());
        }
    }

    private void skipDuplicatePerson(JsonNode rawPersonNode, Person person, int index) {
        String identifier = person.getNameValue() + " (Matric: " + person.getMatricNumberValue() + ")";
        logger.warning("Skipping duplicate contact at entry #" + (index + 1) + ": " + identifier);
        preservedSkippedPersons.add(rawPersonNode.deepCopy());
        loadWarnings.add("Skipped duplicate contact: " + identifier);
    }

    private void skipInvalidPerson(JsonNode rawPersonNode, int index, String errorMessage) {
        String identifier = getRawEntryIdentifier(rawPersonNode, index);
        String formattedWarning = LoadWarningFormatter.formatInvalidEntryWarning("contact", identifier, errorMessage);
        logger.warning(formattedWarning);
        preservedSkippedPersons.add(rawPersonNode.deepCopy());
        loadWarnings.add(formattedWarning);
    }

    private void loadGroups(AddressBook addressBook) {
        requireNonNull(addressBook);
        for (int i = 0; i < groups.size(); i++) {
            loadGroup(addressBook, groups.get(i), i);
        }
    }

    private void loadGroup(AddressBook addressBook, JsonNode rawGroupNode, int index) {
        requireNonNull(addressBook);
        requireNonNull(rawGroupNode);
        assert index >= 0 : "Group index should never be negative";

        try {
            JsonAdaptedGroup jsonAdaptedGroup =
                    JsonUtil.fromJsonNode(rawGroupNode, JsonAdaptedGroup.class);
            Group group = jsonAdaptedGroup.toModelType();

            if (addressBook.hasGroup(group)) {
                skipDuplicateGroup(rawGroupNode, group, index);
                return;
            }

            addressBook.addGroup(group);
        } catch (IllegalValueException | JsonProcessingException e) {
            skipInvalidGroup(rawGroupNode, index, e.getMessage());
        }
    }

    private void skipDuplicateGroup(JsonNode rawGroupNode, Group group, int index) {
        String identifier = "'" + group.getGroupNameValue() + "'";
        String formattedWarning = LoadWarningFormatter.formatDuplicateEntryWarning("group", identifier);
        logger.warning("Skipping duplicate group at entry #" + (index + 1) + ": " + identifier);
        preservedSkippedGroups.add(rawGroupNode.deepCopy());
        loadWarnings.add(formattedWarning);
    }

    private void skipInvalidGroup(JsonNode rawGroupNode, int index, String errorMessage) {
        String identifier = getRawEntryIdentifier(rawGroupNode, index);
        String formattedWarning = LoadWarningFormatter.formatInvalidEntryWarning("group", identifier, errorMessage);
        logger.warning(formattedWarning);
        preservedSkippedGroups.add(rawGroupNode.deepCopy());
        loadWarnings.add(formattedWarning);
    }

    private static String getRawEntryIdentifier(JsonNode rawNode, int index) {
        JsonNode nameNode = rawNode.get("name");
        if (nameNode != null && !nameNode.isNull()) {
            return "'" + nameNode.asText() + "'";
        }
        return "entry #" + (index + 1) + " (missing name)";
    }

    private static void addDeepCopies(List<JsonNode> target, List<JsonNode> source) {
        if (source == null) {
            return;
        }
        for (JsonNode node : source) {
            target.add(node.deepCopy());
        }
    }

    /**
     * Re-attempts validation of all groups preserved from a previous session.
     * Groups that now pass are moved to the address book and removed from the
     * preserved list. Groups that still fail remain preserved with a fresh warning.
     */
    private void revalidatePreservedGroups(AddressBook addressBook, List<JsonNode> preservedSkippedGroupsList) {
        requireNonNull(addressBook);
        requireNonNull(preservedSkippedGroupsList);

        for (int i = 0; i < preservedSkippedGroupsList.size(); i++) {
            JsonNode rawGroupNode = preservedSkippedGroupsList.get(i);
            String identifier = getRawEntryIdentifier(rawGroupNode, i);
            try {
                JsonAdaptedGroup jsonAdaptedGroup = JsonUtil.fromJsonNode(rawGroupNode, JsonAdaptedGroup.class);
                Group group = jsonAdaptedGroup.toModelType();

                if (addressBook.hasGroup(group)) {
                    skipDuplicateGroup(rawGroupNode, group, i);
                    continue;
                }

                logger.info("Preserved skipped group now valid, moving to valid group list: " + identifier);
                addressBook.addGroup(group);

            } catch (IllegalValueException | JsonProcessingException e) {
                skipInvalidGroup(rawGroupNode, i, e.getMessage());
            }
        }
    }

    /**
     * Re-attempts validation of all persons preserved from a previous session.
     * Persons that now pass are moved to the address book and removed from the
     * preserved list.
     * Persons that still fail remain preserved with a fresh warning.
     * Groups are revalidated first so a person whose group was also just moved
     * can succeed in the same load cycle.
     */
    private void revalidatePreservedPersons(AddressBook addressBook, List<JsonNode> preservedSkippedPersonsList) {
        requireNonNull(addressBook);
        requireNonNull(preservedSkippedPersonsList);

        for (int i = 0; i < preservedSkippedPersonsList.size(); i++) {
            JsonNode rawPersonNode = preservedSkippedPersonsList.get(i);
            String identifier = getRawEntryIdentifier(rawPersonNode, i);
            try {
                JsonAdaptedPerson jsonAdaptedPerson = JsonUtil.fromJsonNode(rawPersonNode, JsonAdaptedPerson.class);
                Person person = jsonAdaptedPerson.toModelType();

                if (addressBook.hasPerson(person)) {
                    skipDuplicatePerson(rawPersonNode, person, i);
                    continue;
                }

                PersonLoadValidator.validateGroupsExist(addressBook, person);
                PersonLoadValidator.validateAssignmentGrades(addressBook, person);
                PersonLoadValidator.validateGroupSessions(person);

                logger.info("Preserved skipped contact now valid, moving to valid person list: " + identifier);
                addressBook.addPerson(person);

            } catch (IllegalValueException | JsonProcessingException e) {
                skipInvalidPerson(rawPersonNode, i, e.getMessage());
            }
        }
    }

}
