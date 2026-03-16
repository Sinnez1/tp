package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.model.classspace.ClassSpaceName;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Email;
import seedu.address.model.person.MatricNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Participation;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_MATRIC_NUMBER = "A0505123U";

    private Name name;
    private Phone phone;
    private Email email;
    private MatricNumber matricNumber;
    private Set<Tag> tags;
    private Set<ClassSpaceName> classSpaces;
    private Attendance attendance;
    private Participation participation;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        matricNumber = new MatricNumber(DEFAULT_MATRIC_NUMBER);
        tags = new HashSet<>();
        classSpaces = new HashSet<>();
        attendance = new Attendance(Attendance.Status.UNINITIALISED);
        participation = new Participation(0);
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        email = personToCopy.getEmail();
        matricNumber = personToCopy.getMatricNumber();
        tags = new HashSet<>(personToCopy.getTags());
        classSpaces = new HashSet<>(personToCopy.getClassSpaces());
        attendance = personToCopy.getAttendance();
        participation = personToCopy.getParticipation();
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code MatricNumber} of the {@code Person} that we are building.
     */
    public PersonBuilder withMatricNumber(String matricNumber) {
        this.matricNumber = new MatricNumber(matricNumber);
        return this;
    }

    /**
     * Sets the {@code ClassSpaces} of the {@code Person} that we are building.
     */
    public PersonBuilder withClassSpaces(String ... classSpaces) {
        this.classSpaces = Stream.of(classSpaces)
                .map(ClassSpaceName::new)
                .collect(Collectors.toSet());
        return this;
    }

    /**
     * Sets the {@code Attendance} of the {@code Person} that we are building.
     */
    public PersonBuilder withAttendance(String attendance) {
        this.attendance = new Attendance(attendance);
        return this;
    }

    /**
     * Sets the {@code Participation} of the {@code Person} that we are building.
     */
    public PersonBuilder withParticipation(int participation) {
        this.participation = new Participation(participation);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Builds and returns a {@code Person} with the configured details.
     *
     * @return a new {@code Person} instance
     */
    public Person build() {
        Person person = new Person(name, phone, email, matricNumber, classSpaces, tags);
        person = new Person(person, attendance);
        person = new Person(person, participation);
        return person;
    }

}
