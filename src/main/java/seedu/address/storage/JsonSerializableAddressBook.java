package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.classspace.ClassSpace;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_CLASS_SPACE = "Class space list contains duplicate class space(s).";
    public static final String MESSAGE_INVALID_MATRICULATION_NUMBER = "Invalid matriculation number.";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedClassSpace> classSpaces = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons and class spaces.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
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
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
        classSpaces.addAll(source.getClassSpaceList().stream()
                .map(JsonAdaptedClassSpace::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();

        for (JsonAdaptedClassSpace jsonAdaptedClassSpace : classSpaces) {
            ClassSpace classSpace = jsonAdaptedClassSpace.toModelType();
            if (addressBook.hasClassSpace(classSpace)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_CLASS_SPACE);
            }
            addressBook.addClassSpace(classSpace);
        }

        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            for (var classSpaceName : person.getClassSpaces()) {
                ClassSpace classSpace = new ClassSpace(classSpaceName);
                if (!addressBook.hasClassSpace(classSpace)) {
                    addressBook.addClassSpace(classSpace);
                }
            }
            addressBook.addPerson(person);
        }
        return addressBook;
    }
}
